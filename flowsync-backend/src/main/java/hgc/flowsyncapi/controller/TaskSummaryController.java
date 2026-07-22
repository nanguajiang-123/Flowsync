package hgc.flowsyncapi.controller;

import hgc.flowsyncapi.common.ApiResponse;
import hgc.flowsyncapi.entity.TaskSummary;
import hgc.flowsyncapi.service.TaskSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "任务总结", description = "阶段总结与最终总结的查看与新增")
@RestController
@RequestMapping("/api/summaries")
public class TaskSummaryController {

    private final TaskSummaryService taskSummaryService;

    public TaskSummaryController(TaskSummaryService taskSummaryService) {
        this.taskSummaryService = taskSummaryService;
    }

    @Operation(summary = "获取总结列表")
    @GetMapping
    public ApiResponse<?> list() {
        return ApiResponse.ok(taskSummaryService.listSummaries());
    }

    @Operation(summary = "新增总结")
    @PostMapping
    public ApiResponse<?> add(@RequestBody TaskSummary summary) {
        return ApiResponse.ok("新增成功", taskSummaryService.addSummary(summary));
    }
}
