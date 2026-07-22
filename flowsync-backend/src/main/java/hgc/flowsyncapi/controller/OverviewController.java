package hgc.flowsyncapi.controller;

import hgc.flowsyncapi.common.ApiResponse;
import hgc.flowsyncapi.service.OverviewService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/overview")
public class OverviewController {

    private final OverviewService overviewService;

    public OverviewController(OverviewService overviewService) {
        this.overviewService = overviewService;
    }

    /** 获取总览统计 */
    @GetMapping
    public ApiResponse<?> overview() {
        return ApiResponse.ok(overviewService.getOverview());
    }
}
