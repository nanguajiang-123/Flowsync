package hgc.flowsyncapi.controller;

import hgc.flowsyncapi.common.ApiResponse;
import hgc.flowsyncapi.entity.TaskSummary;
import hgc.flowsyncapi.service.TaskSummaryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/summaries")
public class TaskSummaryController {

    private final TaskSummaryService taskSummaryService;

    public TaskSummaryController(TaskSummaryService taskSummaryService) {
        this.taskSummaryService = taskSummaryService;
    }

    /** 获取总结列表 */
    @GetMapping
    public ApiResponse<?> list() {
        return ApiResponse.ok(taskSummaryService.listSummaries());
    }

    /** 新增总结 */
    @PostMapping
    public ApiResponse<?> add(@RequestBody TaskSummary summary) {
        return ApiResponse.ok("新增成功", taskSummaryService.addSummary(summary));
    }
}
