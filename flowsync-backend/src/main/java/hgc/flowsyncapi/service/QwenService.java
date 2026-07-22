package hgc.flowsyncapi.service;

import hgc.flowsyncapi.dto.AiTaskPlanItem;
import java.util.List;

public interface QwenService {
    String getTaskSuggestion(String projectName, String taskTitle, String taskDescription);
    List<AiTaskPlanItem> generateTaskPlan(String projectName);
}
