#!/bin/bash
#
# 出题组卷系统 启动/停止脚本
# 用法: ./exam-system.sh {start|stop|restart|status}
#

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
BACKEND_DIR="$BASE_DIR/backend"
FRONTEND_DIR="$BASE_DIR/frontend"

BACKEND_LOG="$BASE_DIR/backend.log"
FRONTEND_LOG="$BASE_DIR/frontend.log"
BACKEND_PID_FILE="$BASE_DIR/.backend.pid"
FRONTEND_PID_FILE="$BASE_DIR/.frontend.pid"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

log_info()  { echo -e "${GREEN}[INFO]${NC}  $1"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC}  $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# 检查进程是否存活
is_running() {
    local pid_file="$1"
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if kill -0 "$pid" 2>/dev/null; then
            return 0
        fi
    fi
    return 1
}

# 清理指定端口上的残留进程
cleanup_port() {
    local name="$1"
    local port="$2"
    local pids
    pids=$(lsof -ti :"$port" 2>/dev/null | grep -v "^$")
    if [ -n "$pids" ]; then
        log_warn "${name}端口 $port 仍有残留进程，正在清理..."
        echo "$pids" | xargs kill 2>/dev/null
        sleep 2
        pids=$(lsof -ti :"$port" 2>/dev/null | grep -v "^$")
        if [ -n "$pids" ]; then
            log_warn "残留进程未响应，强制终止..."
            echo "$pids" | xargs kill -9 2>/dev/null
        fi
        log_info "${name}端口 $port 已清理"
    fi
}

# 启动后端
start_backend() {
    if is_running "$BACKEND_PID_FILE"; then
        log_warn "后端服务已在运行 (PID: $(cat $BACKEND_PID_FILE))"
        return 0
    fi

    log_info "正在启动后端服务..."
    cd "$BACKEND_DIR"
    setsid nohup mvn spring-boot:run > "$BACKEND_LOG" 2>&1 &
    local pid=$!
    echo "$pid" > "$BACKEND_PID_FILE"

    # 等待后端启动完成 (最多等待 120 秒)
    local count=0
    local max_wait=120
    while [ $count -lt $max_wait ]; do
        if curl -s http://localhost:8080/api/papers > /dev/null 2>&1; then
            log_info "后端服务启动成功 (PID: $pid) — http://localhost:8080"
            return 0
        fi
        # 检查进程是否还活着
        if ! kill -0 "$pid" 2>/dev/null; then
            log_error "后端服务启动失败，请查看日志: $BACKEND_LOG"
            rm -f "$BACKEND_PID_FILE"
            return 1
        fi
        sleep 2
        count=$((count + 2))
    done

    log_error "后端服务启动超时，请查看日志: $BACKEND_LOG"
    return 1
}

# 启动前端
start_frontend() {
    if is_running "$FRONTEND_PID_FILE"; then
        log_warn "前端服务已在运行 (PID: $(cat $FRONTEND_PID_FILE))"
        return 0
    fi

    log_info "正在启动前端服务..."
    cd "$FRONTEND_DIR"

    # 如果 node_modules 不存在则先安装依赖
    if [ ! -d "node_modules" ]; then
        log_info "首次启动，正在安装前端依赖..."
        npm install >> "$FRONTEND_LOG" 2>&1
    fi

    setsid nohup npm run dev -- --host 0.0.0.0 > "$FRONTEND_LOG" 2>&1 &
    local pid=$!
    echo "$pid" > "$FRONTEND_PID_FILE"

    # 等待前端启动完成 (最多等待 30 秒)
    local count=0
    local max_wait=30
    while [ $count -lt $max_wait ]; do
        if curl -s http://localhost:5173 > /dev/null 2>&1; then
            log_info "前端服务启动成功 (PID: $pid) — http://localhost:5173"
            return 0
        fi
        if ! kill -0 "$pid" 2>/dev/null; then
            log_error "前端服务启动失败，请查看日志: $FRONTEND_LOG"
            rm -f "$FRONTEND_PID_FILE"
            return 1
        fi
        sleep 1
        count=$((count + 1))
    done

    log_error "前端服务启动超时，请查看日志: $FRONTEND_LOG"
    return 1
}

# 停止服务 (递归终止子进程 + 端口兜底)
stop_service() {
    local name="$1"
    local pid_file="$2"
    local port="$3"

    if ! is_running "$pid_file"; then
        log_warn "${name}服务未在运行"
        rm -f "$pid_file"
        # PID 文件无效时，通过端口兜底清理
        if [ -n "$port" ]; then
            cleanup_port "$name" "$port"
        fi
        return 0
    fi

    local pid=$(cat "$pid_file")
    log_info "正在停止${name}服务 (PID: $pid)..."

    # 获取进程组 ID，终止整个进程组
    local pgid
    pgid=$(ps -o pgid= -p "$pid" 2>/dev/null | tr -d ' ')
    if [ -n "$pgid" ] && [ "$pgid" != "0" ]; then
        kill -- -"$pgid" 2>/dev/null
    else
        pkill -P "$pid" 2>/dev/null
        kill "$pid" 2>/dev/null
    fi

    # 等待进程退出 (最多 10 秒)
    local count=0
    while [ $count -lt 10 ]; do
        if ! kill -0 "$pid" 2>/dev/null; then
            log_info "${name}服务已停止"
            rm -f "$pid_file"
            # 确认端口已释放
            if [ -n "$port" ]; then
                cleanup_port "$name" "$port"
            fi
            return 0
        fi
        sleep 1
        count=$((count + 1))
    done

    # 强制终止
    log_warn "${name}服务未响应，强制终止..."
    if [ -n "$pgid" ] && [ "$pgid" != "0" ]; then
        kill -9 -- -"$pgid" 2>/dev/null
    else
        pkill -9 -P "$pid" 2>/dev/null
        kill -9 "$pid" 2>/dev/null
    fi
    rm -f "$pid_file"
    log_info "${name}服务已强制停止"

    # 最终端口清理
    if [ -n "$port" ]; then
        cleanup_port "$name" "$port"
    fi
}

# 查看状态
show_status() {
    echo "=========================================="
    echo "  出题组卷系统 服务状态"
    echo "=========================================="

    if is_running "$BACKEND_PID_FILE"; then
        local bpid=$(cat "$BACKEND_PID_FILE")
        echo -e "  后端服务:  ${GREEN}运行中${NC} (PID: $bpid)  http://localhost:8080"
    else
        echo -e "  后端服务:  ${RED}已停止${NC}"
    fi

    if is_running "$FRONTEND_PID_FILE"; then
        local fpid=$(cat "$FRONTEND_PID_FILE")
        echo -e "  前端服务:  ${GREEN}运行中${NC} (PID: $fpid)  http://localhost:5173"
    else
        echo -e "  前端服务:  ${RED}已停止${NC}"
    fi

    echo "=========================================="
}

# 主入口
case "$1" in
    start)
        log_info "========== 启动出题组卷系统 =========="
        start_backend
        start_frontend
        echo ""
        show_status
        ;;
    stop)
        log_info "========== 停止出题组卷系统 =========="
        stop_service "前端" "$FRONTEND_PID_FILE" 5173
        stop_service "后端" "$BACKEND_PID_FILE" 8080
        ;;
    restart)
        log_info "========== 重启出题组卷系统 =========="
        stop_service "前端" "$FRONTEND_PID_FILE" 5173
        stop_service "后端" "$BACKEND_PID_FILE" 8080
        sleep 2
        start_backend
        start_frontend
        echo ""
        show_status
        ;;
    status)
        show_status
        ;;
    *)
        echo "用法: $0 {start|stop|restart|status}"
        echo ""
        echo "  start    启动后端和前端服务"
        echo "  stop     停止所有服务"
        echo "  restart  重启所有服务"
        echo "  status   查看服务状态"
        exit 1
        ;;
esac
