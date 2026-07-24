# FlowSync 小组任务协同管理系统

## 项目概述
面向教学场景的小组任务协同管理系统，支持项目创建、AI 任务拆解、进度记录、总结输出全流程。

## 技术栈
| 层次 | 技术 | 版本 | 端口 |
|------|------|------|------|
| 前端 | Vue 3 + Vue Router 4 | 3.x | 8081 |
| 前端 UI | Element Plus | 2.x | - |
| HTTP 客户端 | Axios | 1.x | - |
| 后端 | Spring Boot | 3.3.5 | 8080 |
| ORM | MyBatis-Plus | 3.5.8 | - |
| 数据库 | MySQL | 8.x | 3306 |
| 鉴权 | JWT (jjwt) | 0.12.5 | - |
| 密码加密 | BCrypt (spring-security-crypto) | - | - |
| API 文档 | SpringDoc OpenAPI | 2.1.0 | - |
| AI 模型 | 阿里云千问 (DashScope) | qwen-plus | - |

## 项目结构
```
FlowSync/
├── flowsync-backend/                 # 后端 Spring Boot
│   ├── pom.xml
│   └── src/main/
│       ├── java/hgc/flowsyncapi/
│       │   ├── common/               # 统一响应封装 (ApiResponse)
│       │   ├── config/               # 配置 (6 个)
│       │   │   ├── CorsConfig.java           # 跨域
│       │   │   ├── MyBatisPlusConfig.java     # MyBatis-Plus
│       │   │   ├── PasswordConfig.java        # BCrypt 加密
│       │   │   ├── PasswordMigrationRunner.java # 明文密码自动升级
│       │   │   ├── SpringDocConfig.java       # Swagger Authorize 按钮
│       │   │   └── WebMvcConfig.java          # JWT 拦截器注册 + 白名单
│       │   ├── controller/           # 控制器 (9 个)
│       │   │   ├── AuthController.java
│       │   │   ├── ProjectController.java
│       │   │   ├── TaskController.java
│       │   │   ├── TaskLogController.java
│       │   │   ├── TaskSummaryController.java
│       │   │   ├── OperationLogController.java
│       │   │   ├── OverviewController.java
│       │   │   ├── UserController.java
│       │   │   └── AiController.java
│       │   ├── service/              # 业务逻辑 (9 个)
│       │   ├── mapper/               # MyBatis-Plus Mapper (6 个)
│       │   ├── entity/               # 实体类 (6 个)
│       │   ├── dto/                  # 传输对象 (10 个)
│       │   └── security/             # JWT 鉴权 (3 个)
│       │       ├── JwtUtils.java             # Token 生成/解析/校验
│       │       ├── JwtInterceptor.java       # 请求拦截器
│       │       └── SecurityUtils.java        # ThreadLocal + 权限校验
│       └── resources/
│           ├── application.yml
│           ├── db/
│           │   ├── init.sql          # 建表 + 初始数据
│           │   └── seed-data.sql     # 演示数据
│           └── sql/
│               └── operation_log.sql # 操作日志表 DDL
├── flowsync-frontend/                # 前端 Vue 3
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── api/
│       ├── router/
│       ├── views/
│       ├── components/
│       └── styles/
├── docker-compose.yml                # MySQL 容器
├── .env.example                      # 环境变量模板
└── README.md
```

## 快速启动

### 0. 配置环境变量
```bash
cp .env.example .env
# 编辑 .env，填入真实的 DASHSCOPE_API_KEY 和 MySQL 连接信息
```

### 1. 启动数据库
```bash
docker-compose up -d mysql
```
容器首次启动会自动执行 `db/init.sql` 建表和插入初始数据。

### 2. 后端启动
```bash
cd flowsync-backend
mvn spring-boot:run
# 启动后访问 http://localhost:8080/swagger-ui.html 查看 API 文档
```

### 3. 前端启动
```bash
cd flowsync-frontend
npm install
npm run dev
# 启动后访问 http://localhost:8081
```

### 4. 测试账号
| 用户名 | 密码 | 角色 |
|--------|------|------|
| leader | 123456 | 负责人 |
| member1 | 123456 | 成员 |
| member2 | 123456 | 成员 |

> 首次启动时 `PasswordMigrationRunner` 会自动将数据库中的明文密码升级为 BCrypt 哈希。

## 鉴权流程

```
前端登录 → POST /api/auth/login
           ↓
后端返回 JWT token
           ↓
前端存入 localStorage，请求时带 Header: Authorization: Bearer <token>
           ↓
JwtInterceptor 拦截 /api/** 并校验 token
           ↓
解析 userId/username/role → 存入 SecurityUtils ThreadLocal
           ↓
Controller 通过 SecurityUtils.getCurrentUserId() 获取当前用户
           ↓
请求结束 → SecurityUtils.clear() 清理 ThreadLocal
```

白名单（无需 token）：`/api/auth/login`、`/api/auth/register`、Swagger 文档路径。

## API 接口列表

| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 认证 | POST | /api/auth/register | 用户注册（公开） |
| 认证 | POST | /api/auth/login | 用户登录，返回 JWT（公开） |
| 项目 | GET | /api/projects/list | 项目列表，可选 ?ownerId= 筛选 |
| 项目 | POST | /api/projects/save | 新增/编辑项目 |
| 项目 | DELETE | /api/projects/{id} | 删除项目 |
| 任务 | GET | /api/tasks/list | 任务列表，可选 ?projectId= 筛选 |
| 任务 | POST | /api/tasks/save | 新增/编辑任务 |
| 任务 | DELETE | /api/tasks/{id} | 删除任务 |
| 进度 | GET | /api/task-logs/list | 任务进度记录列表 |
| 进度 | POST | /api/task-logs/add | 添加任务进度记录 |
| 总结 | GET | /api/summaries/list | 项目总结列表 |
| 总结 | POST | /api/summaries/add | 添加项目总结 |
| 操作日志 | GET | /api/operation-logs/list | 项目操作审计日志（仅项目负责人） |
| 总览 | GET | /api/overview | 项目总览数据 |
| 用户 | GET | /api/users/list | 全部用户列表 |
| 用户 | POST | /api/users/update-password | 修改密码 |
| AI | POST | /api/ai/task-suggestion | 单个任务 AI 建议 |
| AI | POST | /api/ai/task-plan | AI 任务拆解 |
| AI | POST | /api/ai/task-plan/import | 导入 AI 拆解任务 |

> 所有接口请求体均不包含 `ownerId`/`creatorId` 字段，由后端从 JWT Token 自动获取。

## 权限说明

系统只有两种角色：**负责人** 和 **成员**。

| 操作 | 负责人 | 成员 |
|------|--------|------|
| 创建项目 | ✅ | ❌ |
| 编辑项目 | ✅ 仅自己的项目 | ❌ |
| 删除项目 | ✅ 仅自己的项目 | ❌ |
| 创建任务 | ✅ | ✅ |
| 编辑任务 | ✅ 仅自己创建的 | ✅ 仅自己创建的 |
| 删除任务 | ✅ 仅自己创建的 | ✅ 仅自己创建的 |
| 查看操作日志 | ✅ 仅自己的项目 | ❌ |

权限校验在 Controller 层通过 `SecurityUtils` 统一方法完成：

```java
SecurityUtils.requireLeaderRole()                 // 必须是"负责人"
SecurityUtils.requireOwner(project.getOwnerId())  // 必须是项目负责人
SecurityUtils.requireCreator(task.getCreatorId()) // 必须是任务创建者
```

## 环境变量

| 变量 | 说明 | 默认值 |
|------|------|--------|
| MYSQL_HOST | MySQL 主机 | localhost |
| MYSQL_PORT | MySQL 端口 | 3306 |
| MYSQL_DATABASE | 数据库名 | flowsync_simple |
| MYSQL_USER | 数据库用户 | root |
| MYSQL_PASSWORD | 数据库密码 | root |
| DASHSCOPE_API_KEY | 千问 API Key | - |
| JWT_SECRET | JWT 签名密钥 | flowsync-dev-secret-key-... |
| JWT_EXPIRATION | Token 过期时间（秒） | 86400 |
| JWT_ALGORITHM | 签名算法 | HS256 |
