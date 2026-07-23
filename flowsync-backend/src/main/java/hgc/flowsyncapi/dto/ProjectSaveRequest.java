package hgc.flowsyncapi.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 项目保存请求体 —— 不包含 ownerId，由后端从 JWT 令牌中获取
 */
@Data
public class ProjectSaveRequest {

    private Long id;              // null = 新建，非 null = 编辑
    private String name;
    private String description;
    private String status;
    private String priority;
    private LocalDate startDate;
    private LocalDate endDate;
}
