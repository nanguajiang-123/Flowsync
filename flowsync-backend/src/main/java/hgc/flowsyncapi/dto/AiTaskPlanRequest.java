package hgc.flowsyncapi.dto;

import lombok.Data;

@Data
public class AiTaskPlanRequest {
    private Long projectId;
    private Long operatorId;
    private String projectName;
    private String goal;
    private String description;
}
