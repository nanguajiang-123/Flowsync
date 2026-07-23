-- =====================================================
-- 操作日志表
-- 记录系统中所有增删改操作，用于审计和回溯
-- =====================================================

CREATE TABLE IF NOT EXISTS `operation_log` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     BIGINT       NOT NULL                COMMENT '操作人ID',
    `module`      VARCHAR(32)  NOT NULL                COMMENT '操作模块：PROJECT / TASK / TASK_LOG / TASK_SUMMARY',
    `action`      VARCHAR(16)  NOT NULL                COMMENT '操作类型：CREATE / UPDATE / DELETE',
    `target_id`   BIGINT       DEFAULT NULL            COMMENT '操作目标记录ID',
    `detail`      VARCHAR(512) DEFAULT NULL            COMMENT '操作详情描述',
    `project_id`  BIGINT       DEFAULT NULL            COMMENT '所属项目ID（用于权限过滤，非项目级操作可为空）',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id`    (`user_id`),
    INDEX `idx_project_id` (`project_id`),
    INDEX `idx_module`     (`module`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
