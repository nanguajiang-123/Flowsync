package hgc.flowsyncapi.controller;

import hgc.flowsyncapi.common.ApiResponse;
import hgc.flowsyncapi.entity.ProjectInfo;
import hgc.flowsyncapi.mapper.ProjectInfoMapper;
import hgc.flowsyncapi.security.SecurityUtils;
import hgc.flowsyncapi.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "操作日志", description = "查看项目操作日志（仅项目负责人可查看）")
@RestController
@RequestMapping("/api/operation-logs")
public class OperationLogController {

    private final OperationLogService operationLogService;
    private final ProjectInfoMapper projectInfoMapper;

    public OperationLogController(OperationLogService operationLogService,
                                  ProjectInfoMapper projectInfoMapper) {
        this.operationLogService = operationLogService;
        this.projectInfoMapper = projectInfoMapper;
    }

    @Operation(summary = "获取项目的操作日志列表")
    @GetMapping("/list")
    public ApiResponse<?> list(
            @Parameter(description = "项目ID") @RequestParam Long projectId) {

        Long currentUserId = SecurityUtils.getCurrentUserId();

        // 权限校验：只有项目负责人才能查看操作日志
        ProjectInfo project = projectInfoMapper.selectById(projectId);
        if (project == null) {
            return ApiResponse.fail("项目不存在");
        }
        if (!project.getOwnerId().equals(currentUserId)) {
            return ApiResponse.fail("无权查看：仅项目负责人可查看操作日志");
        }

        return ApiResponse.ok(operationLogService.listByProject(projectId));
    }
}
