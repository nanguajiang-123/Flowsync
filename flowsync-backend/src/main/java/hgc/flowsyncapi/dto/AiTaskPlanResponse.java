package hgc.flowsyncapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class AiTaskPlanResponse {
    private String summary;
    private List<AiTaskPlanItem> items;
}
