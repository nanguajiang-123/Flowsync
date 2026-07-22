package hgc.flowsyncapi.controller;

import hgc.flowsyncapi.common.ApiResponse;
import hgc.flowsyncapi.dto.*;
import hgc.flowsyncapi.entity.TaskInfo;
import hgc.flowsyncapi.entity.User;
import hgc.flowsyncapi.service.QwenService;
import hgc.flowsyncapi.service.TaskInfoService;
import hgc.flowsyncapi.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final QwenService qwenService;
    private final UserService userService;
    private final TaskInfoService taskInfoService;

    public AiController(QwenService qwenService, UserService userService, TaskInfoService taskInfoService) {
        this.qwenService = qwenService;
        this.userService = userService;
        this.taskInfoService = taskInfoService;
    }

    /** 单任务 AI 建议 */
    @PostMapping("/task-suggestion")
    public ApiResponse<?> taskSuggestion(@RequestBody AiTaskSuggestionRequest request) {
        try {
            String suggestion = qwenService.getTaskSuggestion(
                    request.getProjectName(),
                    request.getTaskTitle(),
                    request.getTaskDescription());
            Map<String, String> result = new HashMap<>();
            result.put("suggestion", suggestion);
            return ApiResponse.ok(result);
        } catch (Exception e) {
            return ApiResponse.fail("AI 建议生成失败: " + e.getMessage());
        }
    }

    /** AI 任务拆解 */
    @PostMapping("/task-plan")
    public ApiResponse<?> taskPlan(@RequestBody AiTaskPlanRequest request) {
        try {
            List<User> members = userService.listUsers();
            AiTaskPlanResponse plan = qwenService.generateTaskPlan(
                    request.getProjectName(),
                    request.getGoal(),
                    request.getDescription(),
                    members);
            return ApiResponse.ok(plan);
        } catch (Exception e) {
            return ApiResponse.fail("AI 任务拆解失败: " + e.getMessage());
        }
    }

    /** 导入 AI 拆解任务 */
    @PostMapping("/task-plan/import")
    public ApiResponse<?> importTaskPlan(@RequestBody AiTaskPlanImportRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            return ApiResponse.fail("没有要导入的任务");
        }

        List<TaskInfo> imported = new ArrayList<>();
        for (AiTaskPlanItem item : request.getItems()) {
            TaskInfo task = new TaskInfo();
            task.setProjectId(request.getProjectId());
            task.setTitle(item.getTitle());
            task.setDescription(item.getDescription());
            task.setPriority(item.getPriority() != null ? item.getPriority() : "中");
            task.setStatus("未开始");
            task.setAssigneeId(item.getAssigneeId());
            task.setCreatorId(request.getCreatorId());
            if (item.getSuggestedDays() != null) {
                task.setDueDate(LocalDate.now().plusDays(item.getSuggestedDays()));
            }
            taskInfoService.saveTask(task);
            imported.add(task);
        }
        return ApiResponse.ok("成功导入 " + imported.size() + " 个任务", imported);
    }
}
