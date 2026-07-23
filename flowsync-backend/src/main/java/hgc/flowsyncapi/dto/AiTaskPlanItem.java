package hgc.flowsyncapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * AI 拆解返回的单条任务 —— 新格式（JSON 数组）
 */
@Data
public class AiTaskPlanItem {

    @JsonProperty("projectname")
    private String projectname;

    @JsonProperty("taskdesc")
    private String taskdesc;

    private String assignee;     // 负责人姓名：项目负责人/张三/李四

    private String status;       // 固定"未开始"

    private String priority;     // 高/中/低

    @JsonProperty("dueDate")
    private String dueDate;      // yyyy-MM-dd

    @JsonProperty("createTime")
    private String createTime;   // yyyy-MM-dd

    // ---- 前端导入时用的字段（非 AI 返回） ----
    private Long assigneeId;     // 映射后的用户 ID
    private String title;        // 兼容前端显示，映射自 taskdesc
    private String description;  // 兼容前端显示
    private Integer suggestedDays;
}
