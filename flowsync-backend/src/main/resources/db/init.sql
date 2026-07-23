-- ============================================================
-- FlowSync 数据库初始化脚本
-- 数据库：flowsync_simple  |  字符集：utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS flowsync_simple
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE flowsync_simple;

-- ============================================================
-- 1. sys_user 用户表
-- ============================================================
DROP TABLE IF EXISTS task_summary;
DROP TABLE IF EXISTS task_log;
DROP TABLE IF EXISTS task_info;
DROP TABLE IF EXISTS project_info;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '主键',
    username    VARCHAR(50)     NOT NULL UNIQUE              COMMENT '用户名，登录账号',
    password    VARCHAR(100)    NOT NULL                     COMMENT 'BCrypt密码哈希',
    real_name   VARCHAR(50)     NOT NULL                     COMMENT '真实姓名',
    role        VARCHAR(30)     NOT NULL                     COMMENT '角色（管理员/负责人/成员）',
    phone       VARCHAR(20)     DEFAULT NULL                 COMMENT '联系电话',
    email       VARCHAR(100)    DEFAULT NULL                 COMMENT '电子邮箱',
    create_time DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间'
) ENGINE=InnoDB COMMENT='系统用户表';

-- ============================================================
-- 2. project_info 项目表
-- ============================================================
CREATE TABLE project_info (
    id          BIGINT          PRIMARY KEY AUTO_INCREMENT  COMMENT '主键',
    name        VARCHAR(100)    NOT NULL                     COMMENT '项目名称',
    description VARCHAR(500)    DEFAULT NULL                 COMMENT '项目说明',
    status      VARCHAR(20)     NOT NULL                     COMMENT '项目状态（未开始/进行中/已完成）',
    priority    VARCHAR(20)     NOT NULL                     COMMENT '优先级（低/中/高）',
    owner_id    BIGINT          DEFAULT NULL                 COMMENT '项目负责人 ID',
    start_date  DATE            DEFAULT NULL                 COMMENT '开始日期',
    end_date    DATE            DEFAULT NULL                 COMMENT '结束日期',
    create_time DATETIME        DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
    FOREIGN KEY (owner_id) REFERENCES sys_user(id)
) ENGINE=InnoDB COMMENT='项目信息表';

-- ============================================================
-- 3. task_info 任务表
-- ============================================================
CREATE TABLE task_info (
    id            BIGINT        PRIMARY KEY AUTO_INCREMENT  COMMENT '主键',
    project_id    BIGINT        NOT NULL                     COMMENT '所属项目 ID',
    parent_id     BIGINT        DEFAULT NULL                 COMMENT '父任务 ID（支持任务层级）',
    title         VARCHAR(100)  NOT NULL                     COMMENT '任务标题',
    description   VARCHAR(1000) DEFAULT NULL                 COMMENT '任务说明',
    assignee_id   BIGINT        DEFAULT NULL                 COMMENT '任务负责人 ID',
    creator_id    BIGINT        DEFAULT NULL                 COMMENT '任务创建人 ID',
    status        VARCHAR(20)   NOT NULL                     COMMENT '任务状态（未开始/进行中/已完成）',
    priority      VARCHAR(20)   NOT NULL                     COMMENT '优先级（低/中/高）',
    due_date      DATE          DEFAULT NULL                 COMMENT '截止日期',
    ai_suggestion TEXT          DEFAULT NULL                 COMMENT '千问 AI 建议内容',
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
    FOREIGN KEY (project_id)    REFERENCES project_info(id),
    FOREIGN KEY (parent_id)     REFERENCES task_info(id),
    FOREIGN KEY (assignee_id)   REFERENCES sys_user(id),
    FOREIGN KEY (creator_id)    REFERENCES sys_user(id)
) ENGINE=InnoDB COMMENT='任务信息表';

-- ============================================================
-- 4. task_log 任务进度记录表
-- ============================================================
CREATE TABLE task_log (
    id              BIGINT      PRIMARY KEY AUTO_INCREMENT  COMMENT '主键',
    task_id         BIGINT      NOT NULL                     COMMENT '关联任务 ID',
    progress_percent INT        NOT NULL                     COMMENT '进度百分比（0-100）',
    content         VARCHAR(1000) NOT NULL                   COMMENT '进度说明文字',
    operator_id     BIGINT      DEFAULT NULL                 COMMENT '记录人 ID',
    create_time     DATETIME    DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
    FOREIGN KEY (task_id)     REFERENCES task_info(id),
    FOREIGN KEY (operator_id) REFERENCES sys_user(id)
) ENGINE=InnoDB COMMENT='任务进度记录表';

-- ============================================================
-- 5. task_summary 总结表
-- ============================================================
CREATE TABLE task_summary (
    id           BIGINT         PRIMARY KEY AUTO_INCREMENT  COMMENT '主键',
    project_id   BIGINT         NOT NULL                     COMMENT '所属项目 ID',
    task_id      BIGINT         DEFAULT NULL                 COMMENT '关联任务 ID（可选）',
    summary_type VARCHAR(30)    NOT NULL                     COMMENT '总结类型（阶段总结/最终总结）',
    content      VARCHAR(2000)  NOT NULL                     COMMENT '总结内容',
    created_by   BIGINT         DEFAULT NULL                 COMMENT '创建人 ID',
    create_time  DATETIME       DEFAULT CURRENT_TIMESTAMP    COMMENT '创建时间',
    FOREIGN KEY (project_id) REFERENCES project_info(id),
    FOREIGN KEY (task_id)    REFERENCES task_info(id),
    FOREIGN KEY (created_by) REFERENCES sys_user(id)
) ENGINE=InnoDB COMMENT='总结表';

-- ============================================================
-- 预置测试数据
-- ============================================================
INSERT INTO sys_user (username, password, real_name, role, phone, email) VALUES
('admin',   '123456', '系统管理员', '管理员', '13800000000', 'admin@flowsync.cn'),
('leader',  '123456', '项目负责人', '负责人', '13800000001', 'leader@flowsync.cn'),
('member1', '123456', '张三',       '成员',   '13800000002', 'zhangsan@flowsync.cn'),
('member2', '123456', '李四',       '成员',   '13800000003', 'lisi@flowsync.cn');
