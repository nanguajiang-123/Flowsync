package hgc.flowsyncapi.controller;

import hgc.flowsyncapi.common.ApiResponse;
import hgc.flowsyncapi.dto.*;
import hgc.flowsyncapi.entity.TaskInfo;
import hgc.flowsyncapi.service.QwenService;
import hgc.flowsyncapi.service.TaskInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private static final Logger log = LoggerFactory.getLogger(AiController.class);
    private final QwenService qwenService;
    private final TaskInfoService taskInfoService;

    public AiController(QwenService qwenService, TaskInfoService taskInfoService) {
        this.qwenService = qwenService;
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
            log.error("AI 建议生成失败", e);
            return ApiResponse.fail("AI 建议生成失败: " + e.getMessage());
        }
    }

    /** AI 任务拆解 —— 新接口：入参简化，返回 JSON 数组 */
    @PostMapping("/task-plan")
    public ApiResponse<?> taskPlan(@RequestBody Map<String, Object> request) {
        try {
            String projectName = (String) request.getOrDefault("projectName", "");
            if (projectName.isEmpty()) {
                return ApiResponse.fail("项目名称不能为空");
            }
            List<AiTaskPlanItem> items = qwenService.generateTaskPlan(projectName);
            return ApiResponse.ok(items);
        } catch (Exception e) {
            log.error("AI 任务拆解失败", e);
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
            task.setTitle(item.getTitle() != null ? item.getTitle() : item.getTaskdesc());
            task.setDescription(item.getDescription() != null ? item.getDescription() : item.getTaskdesc());
            task.setPriority(item.getPriority() != null ? item.getPriority() : "中");
            task.setStatus("未开始");
            task.setAssigneeId(item.getAssigneeId());
            task.setCreatorId(request.getCreatorId());
            if (item.getDueDate() != null && !item.getDueDate().isEmpty()) {
                try {
                    task.setDueDate(LocalDate.parse(item.getDueDate()));
                } catch (Exception e) {
                    task.setDueDate(LocalDate.now().plusDays(7));
                }
            }
            taskInfoService.saveTask(task);
            imported.add(task);
        }
        log.info("导入 AI 任务: {} 条", imported.size());
        return ApiResponse.ok("成功导入 " + imported.size() + " 个任务", imported);
    }
}
