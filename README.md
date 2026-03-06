# Java 程序设计基础 — 出题组卷系统

> Spring Boot 3.4 + H2 + Vue 3 + Element Plus

## 功能

- **题库管理** — 150道题，覆盖10章节，6种题型(单选/多选/判断/填空/简答/编程)
- **20套预置试卷** — 首次启动自动初始化，每套28题100分
- **自动组卷** — 按题型数量、章节范围、难度比例(简单/中等/困难)随机抽取
- **试卷预览** — 按题型分组排版，支持显示/隐藏答案解析
- **Word导出** — 一键导出.docx格式试卷

## 快速启动

```bash
# 1. 启动后端 (首次会自动下载Maven依赖)
cd backend
mvn spring-boot:run
# 后端运行在 http://localhost:8080

# 2. 启动前端
cd frontend
npm install   # 首次需要
npm run dev
# 前端运行在 http://localhost:5173
```

## 项目结构

```
├── backend/          # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/java/com/exam/
│       ├── entity/       # JPA实体 (Question, ExamPaper, PaperQuestion)
│       ├── repository/   # 数据访问
│       ├── service/      # 业务逻辑 (含自动组卷算法)
│       ├── controller/   # REST API
│       ├── dto/          # 数据传输对象
│       └── config/       # 配置 + 数据初始化
├── frontend/         # Vue 3 + Vite 前端
│   └── src/
│       ├── views/        # 页面组件
│       ├── api/          # API封装
│       └── router.js     # 路由配置
└── docs/             # 文档
```

## API 端点

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/questions?type=&chapter=&difficulty=&page=0&size=20` | 题库查询 |
| POST | `/api/questions` | 新增题目 |
| GET | `/api/papers` | 试卷列表 |
| GET | `/api/papers/{id}` | 试卷详情 |
| POST | `/api/papers/auto-generate` | 自动组卷 |
| GET | `/api/papers/{id}/export` | 导出Word |

## 数据库

默认使用 H2 嵌入式数据库（零安装），数据文件存储在 `backend/data/examdb.mv.db`。

如需切换 MySQL，修改 `application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/exam?useSSL=false
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: your_password
```
