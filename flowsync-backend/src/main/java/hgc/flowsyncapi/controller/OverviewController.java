package hgc.flowsyncapi.controller;

import hgc.flowsyncapi.common.ApiResponse;
import hgc.flowsyncapi.service.OverviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "首页总览", description = "仪表盘统计数据")
@RestController
@RequestMapping("/api/overview")
public class OverviewController {

    private final OverviewService overviewService;

    public OverviewController(OverviewService overviewService) {
        this.overviewService = overviewService;
    }

    @Operation(summary = "获取总览统计（用户数/项目数/任务数/总结数）")
    @GetMapping
    public ApiResponse<?> overview() {
        return ApiResponse.ok(overviewService.getOverview());
    }
}
