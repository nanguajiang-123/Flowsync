package hgc.flowsyncapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AI 拆解返回的单条任务 —— 新格式（JSON 数组）
 */
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

    // ---- getters / setters ----
    public String getProjectname() { return projectname; }
    public void setProjectname(String projectname) { this.projectname = projectname; }

    public String getTaskdesc() { return taskdesc; }
    public void setTaskdesc(String taskdesc) { this.taskdesc = taskdesc; }

    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public Long getAssigneeId() { return assigneeId; }
    public void setAssigneeId(Long assigneeId) { this.assigneeId = assigneeId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getSuggestedDays() { return suggestedDays; }
    public void setSuggestedDays(Integer suggestedDays) { this.suggestedDays = suggestedDays; }
}
