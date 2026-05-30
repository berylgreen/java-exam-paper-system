@echo off
setlocal EnableDelayedExpansion

set "JAVA_HOME=C:\Program Files\Java\jdk-17.0.2"
set "PATH=%JAVA_HOME%\bin;%PATH%"

set "BASE_DIR=%~dp0"
set "BACKEND_DIR=%BASE_DIR%backend"
set "FRONTEND_DIR=%BASE_DIR%frontend"

set "BACKEND_LOG=%BASE_DIR%backend.log"
set "FRONTEND_LOG=%BASE_DIR%frontend.log"

set "BACKEND_PORT=8080"
set "FRONTEND_PORT=5173"

if "%1"=="" goto usage
if /i "%1"=="start" goto start
if /i "%1"=="stop" goto stop
if /i "%1"=="restart" goto restart
if /i "%1"=="status" goto status

:usage
echo 用法: %~nx0 {start^|stop^|restart^|status}
echo.
echo   start    启动后端和前端服务
echo   stop     停止所有服务
echo   restart  重启所有服务
echo   status   查看服务状态
exit /b 1

:start
echo ========== 启动出题组卷系统 ==========
call :start_backend
call :start_frontend
echo.
call :status
goto :eof

:stop
echo ========== 停止出题组卷系统 ==========
call :stop_service "前端" %FRONTEND_PORT%
call :stop_service "后端" %BACKEND_PORT%
goto :eof

:restart
echo ========== 重启出题组卷系统 ==========
call :stop_service "前端" %FRONTEND_PORT%
call :stop_service "后端" %BACKEND_PORT%
ping -n 3 127.0.0.1 >nul
call :start_backend
call :start_frontend
echo.
call :status
goto :eof

:status
echo ==========================================
echo   出题组卷系统 服务状态
echo ==========================================
call :check_port %BACKEND_PORT% b_pid
if defined b_pid (
    echo   [INFO] 后端服务:  运行中 ^(PID: !b_pid!^)  http://localhost:%BACKEND_PORT%
) else (
    echo   [WARN] 后端服务:  已停止
)

call :check_port %FRONTEND_PORT% f_pid
if defined f_pid (
    echo   [INFO] 前端服务:  运行中 ^(PID: !f_pid!^)  http://localhost:%FRONTEND_PORT%
) else (
    echo   [WARN] 前端服务:  已停止
)
echo ==========================================
goto :eof

:start_backend
call :check_port %BACKEND_PORT% b_pid
if defined b_pid (
    echo [WARN] 后端服务已在运行 ^(PID: !b_pid!^)
    exit /b
)
echo [INFO] 正在启动后端服务...
cd /d "%BACKEND_DIR%"
start "ExamSystem-Backend" /MIN cmd /c "mvn spring-boot:run > "%BACKEND_LOG%" 2>&1"

:: 等待启动
set count=0
:wait_backend
call :check_port %BACKEND_PORT% b_pid
if defined b_pid (
    echo [INFO] 后端服务启动成功 ^(PID: !b_pid!^) — http://localhost:%BACKEND_PORT%
    cd /d "%BASE_DIR%"
    exit /b
)
set /a count+=1
if !count! lss 60 (
    ping -n 3 127.0.0.1 >nul
    goto wait_backend
)
echo [ERROR] 后端服务启动超时，请查看日志: %BACKEND_LOG%
cd /d "%BASE_DIR%"
exit /b

:start_frontend
call :check_port %FRONTEND_PORT% f_pid
if defined f_pid (
    echo [WARN] 前端服务已在运行 ^(PID: !f_pid!^)
    exit /b
)
echo [INFO] 正在启动前端服务...
cd /d "%FRONTEND_DIR%"
if not exist "node_modules\" (
    echo [INFO] 首次启动，正在安装前端依赖...
    call npm install >> "%FRONTEND_LOG%" 2>&1
)
start "ExamSystem-Frontend" /MIN cmd /c "npm run dev -- --host 0.0.0.0 > "%FRONTEND_LOG%" 2>&1"

:: 等待启动
set count=0
:wait_frontend
call :check_port %FRONTEND_PORT% f_pid
if defined f_pid (
    echo [INFO] 前端服务启动成功 ^(PID: !f_pid!^) — http://localhost:%FRONTEND_PORT%
    cd /d "%BASE_DIR%"
    exit /b
)
set /a count+=1
if !count! lss 15 (
    ping -n 3 127.0.0.1 >nul
    goto wait_frontend
)
echo [ERROR] 前端服务启动超时，请查看日志: %FRONTEND_LOG%
cd /d "%BASE_DIR%"
exit /b

:stop_service
set "svc_name=%~1"
set "svc_port=%~2"
call :check_port %svc_port% pid
if not defined pid (
    echo [WARN] %svc_name%服务未在运行
    exit /b
)
echo [INFO] 正在停止%svc_name%服务 ^(PID: !pid!^)...
taskkill /PID !pid! /T /F >nul 2>&1
ping -n 2 127.0.0.1 >nul
call :check_port %svc_port% pid2
if defined pid2 (
    echo [WARN] %svc_name%服务未响应，强制终止...
    taskkill /PID !pid2! /T /F >nul 2>&1
)
echo [INFO] %svc_name%服务已停止
exit /b

:check_port
set "port=%~1"
set "rtn_var=%~2"
set "%rtn_var%="
for /f "tokens=2,5" %%a in ('netstat -ano ^| find "LISTENING"') do (
    echo %%a| findstr /e ":%port%" >nul
    if not errorlevel 1 (
        set "%rtn_var%=%%b"
        exit /b
    )
)
exit /b
