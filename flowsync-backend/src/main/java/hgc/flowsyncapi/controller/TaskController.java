package hgc.flowsyncapi.controller;

import hgc.flowsyncapi.common.ApiResponse;
import hgc.flowsyncapi.dto.TaskSaveRequest;
import hgc.flowsyncapi.entity.TaskInfo;
import hgc.flowsyncapi.mapper.TaskInfoMapper;
import hgc.flowsyncapi.security.SecurityUtils;
import hgc.flowsyncapi.service.TaskInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "任务管理", description = "任务的增删改查，支持按项目筛选")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskInfoService taskInfoService;
    private final TaskInfoMapper taskInfoMapper;

    public TaskController(TaskInfoService taskInfoService,
                          TaskInfoMapper taskInfoMapper) {
        this.taskInfoService = taskInfoService;
        this.taskInfoMapper = taskInfoMapper;
    }

    @Operation(summary = "获取任务列表")
    @GetMapping("/list")
    public ApiResponse<?> list(@Parameter(description = "项目ID（可选）") @RequestParam(required = false) Long projectId) {
        List<TaskInfo> list = taskInfoService.listTasks(projectId);
        return ApiResponse.ok(list);
    }

    @Operation(summary = "新增或编辑任务")
    @PostMapping("/save")
    public ApiResponse<?> save(@RequestBody TaskSaveRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        if (request.getId() != null) {
            // 编辑：只有任务创建者本人可以操作
            TaskInfo existing = taskInfoMapper.selectById(request.getId());
            if (existing == null) {
                return ApiResponse.fail("任务不存在");
            }
            SecurityUtils.requireCreator(existing.getCreatorId());
        }

        TaskInfo task = new TaskInfo();
        task.setId(request.getId());
        task.setProjectId(request.getProjectId());
        task.setParentId(request.getParentId());
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setAssigneeId(request.getAssigneeId());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setAiSuggestion(request.getAiSuggestion());
        task.setCreatorId(currentUserId);

        return ApiResponse.ok("保存成功",
                taskInfoService.saveTask(task, currentUserId));
    }

    @Operation(summary = "删除任务")
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        TaskInfo existing = taskInfoMapper.selectById(id);
        if (existing == null) {
            return ApiResponse.fail("任务不存在");
        }
        SecurityUtils.requireCreator(existing.getCreatorId());

        taskInfoService.deleteTask(id, SecurityUtils.getCurrentUserId());
        return ApiResponse.ok("删除成功");
    }
}
