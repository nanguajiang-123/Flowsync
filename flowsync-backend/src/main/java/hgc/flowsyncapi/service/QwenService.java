package hgc.flowsyncapi.service;

import hgc.flowsyncapi.dto.AiTaskPlanResponse;
import hgc.flowsyncapi.entity.User;

import java.util.List;

public interface QwenService {

    /** 单任务 AI 建议 */
    String getTaskSuggestion(String projectName, String taskTitle, String taskDescription);

    /** AI 任务拆解 —— 返回拆解结果 */
    AiTaskPlanResponse generateTaskPlan(String projectName, String goal,
                                        String description, List<User> members);
}
