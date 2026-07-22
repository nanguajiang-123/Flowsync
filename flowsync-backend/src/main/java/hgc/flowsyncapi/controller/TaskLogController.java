package hgc.flowsyncapi.controller;

import hgc.flowsyncapi.common.ApiResponse;
import hgc.flowsyncapi.entity.TaskLog;
import hgc.flowsyncapi.service.TaskLogService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/task-logs")
public class TaskLogController {

    private final TaskLogService taskLogService;

    public TaskLogController(TaskLogService taskLogService) {
        this.taskLogService = taskLogService;
    }

    /** 获取进度记录列表，支持 ?taskId= 筛选 */
    @GetMapping
    public ApiResponse<?> list(@RequestParam(required = false) Long taskId) {
        return ApiResponse.ok(taskLogService.listTaskLogs(taskId));
    }

    /** 新增进度记录 */
    @PostMapping
    public ApiResponse<?> add(@RequestBody TaskLog log) {
        return ApiResponse.ok("新增成功", taskLogService.addTaskLog(log));
    }
}
