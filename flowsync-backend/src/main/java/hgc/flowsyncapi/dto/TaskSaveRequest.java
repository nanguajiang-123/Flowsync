package hgc.flowsyncapi.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 任务保存请求体 —— 不包含 creatorId，由后端从 JWT 令牌中获取
 */
@Data
public class TaskSaveRequest {

    private Long id;              // null = 新建，非 null = 编辑
    private Long projectId;
    private Long parentId;
    private String title;
    private String description;
    private Long assigneeId;
    private String status;
    private String priority;
    private LocalDate dueDate;
    private String aiSuggestion;
}
