package hgc.flowsyncapi.controller;

import hgc.flowsyncapi.common.ApiResponse;
import hgc.flowsyncapi.entity.TaskLog;
import hgc.flowsyncapi.service.TaskLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "任务进度", description = "任务进度记录的查看与新增")
@RestController
@RequestMapping("/api/task-logs")
public class TaskLogController {

    private final TaskLogService taskLogService;

    public TaskLogController(TaskLogService taskLogService) {
        this.taskLogService = taskLogService;
    }

    @Operation(summary = "获取进度记录列表")
    @GetMapping("/list")
    public ApiResponse<?> list(@Parameter(description = "任务ID（可选）") @RequestParam(required = false) Long taskId) {
        return ApiResponse.ok(taskLogService.listTaskLogs(taskId));
    }

    @Operation(summary = "新增进度记录")
    @PostMapping("/add")
    public ApiResponse<?> add(
            @RequestParam Long currentUserId,
            @RequestBody TaskLog taskLog) {

        // 操作人必须以当前登录用户为准
        taskLog.setOperatorId(currentUserId);

        return ApiResponse.ok(
                "新增成功",
                taskLogService.addTaskLog(taskLog, currentUserId)
        );
    }
}
