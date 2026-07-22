# FlowSync 小组任务协同管理系统

## 项目概述
面向教学场景的小组任务协同管理系统，支持项目创建、AI 任务拆解、进度记录、总结输出全流程。

## 技术栈
| 层次 | 技术 | 版本 | 端口 |
|------|------|------|------|
| 前端 | Vue 3 + Vue Router 4 | 3.x | 8081 |
| 前端 UI | Element Plus | 2.x | - |
| HTTP | Axios | 1.x | - |
| 后端 | Spring Boot | 3.3.5 | 8080 |
| ORM | MyBatis-Plus | 3.5.8 | - |
| 数据库 | MySQL | 8.x | 3306 |
| AI 模型 | 阿里云千问 (DashScope) | qwen-plus | - |

## 项目结构
```
FlowSync/
├── flowsync-backend/          # 后端 Spring Boot
│   ├── pom.xml
│   └── src/main/
│       ├── java/hgc/flowsyncapi/
│       │   ├── common/        # 统一响应封装
│       │   ├── config/        # CORS、MyBatis-Plus 配置
│       │   ├── controller/    # 控制器 (8 个)
│       │   ├── service/       # 业务逻辑 (8 个)
│       │   ├── mapper/        # 数据访问 (5 个)
│       │   ├── entity/        # 实体类 (5 个)
│       │   └── dto/           # 传输对象 (7 个)
│       └── resources/
│           ├── application.yml
│           └── db/init.sql    # 数据库初始化脚本
├── flowsync-frontend/         # 前端 Vue 3
│   ├── package.json
│   ├── vite.config.js
│   └── src/
│       ├── api/               # API 封装 (9 个模块)
│       ├── router/            # 路由 + 守卫
│       ├── views/             # 页面 (Login + Home)
│       ├── components/        # 面板组件 (8 个)
│       └── styles/            # 全局样式
└── README.md
```

## 快速启动

### 1. 数据库初始化
在 MySQL 中执行 `flowsync-backend/src/main/resources/db/init.sql`

### 2. 后端启动
```bash
cd flowsync-backend
mvn spring-boot:run
# 启动后访问 http://localhost:8080/swagger-ui.html
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
| admin | 123456 | 管理员 |
| leader | 123456 | 负责人 |
| member1 | 123456 | 成员 |
| member2 | 123456 | 成员 |

## API 接口列表

| 模块 | 方法 | 路径 |
|------|------|------|
| 认证 | POST | /api/auth/login |
| 项目 | GET/POST/DELETE | /api/projects |
| 任务 | GET/POST/DELETE | /api/tasks |
| 进度 | GET/POST | /api/task-logs |
| 总结 | GET/POST | /api/summaries |
| 总览 | GET | /api/overview |
| 用户 | GET | /api/users |
| 用户 | POST | /api/users/update-password |
| AI | POST | /api/ai/task-suggestion |
| AI | POST | /api/ai/task-plan |
| AI | POST | /api/ai/task-plan/import |

## 权限说明
- **管理员/负责人**: 全部菜单和操作可见
- **成员**: 仅可见总览、任务管理（仅更新自己任务状态）、进度跟踪、总结中心、成员列表、个人信息
- 权限控制在前端通过菜单和按钮显隐实现（教学简化方案，预留后端权限校验扩展空间）
