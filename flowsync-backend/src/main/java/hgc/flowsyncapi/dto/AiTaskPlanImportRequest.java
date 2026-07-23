package hgc.flowsyncapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class AiTaskPlanImportRequest {
    private Long projectId;
    private List<AiTaskPlanItem> items;
}
