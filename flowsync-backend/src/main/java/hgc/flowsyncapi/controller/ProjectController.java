package hgc.flowsyncapi.controller;

import hgc.flowsyncapi.common.ApiResponse;
import hgc.flowsyncapi.dto.ProjectSaveRequest;
import hgc.flowsyncapi.entity.ProjectInfo;
import hgc.flowsyncapi.mapper.ProjectInfoMapper;
import hgc.flowsyncapi.security.SecurityUtils;
import hgc.flowsyncapi.service.ProjectInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "项目管理", description = "项目的增删改查")
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectInfoService projectInfoService;
    private final ProjectInfoMapper projectInfoMapper;

    public ProjectController(ProjectInfoService projectInfoService,
                             ProjectInfoMapper projectInfoMapper) {
        this.projectInfoService = projectInfoService;
        this.projectInfoMapper = projectInfoMapper;
    }

    @Operation(summary = "获取项目列表（可选按负责人筛选）")
    @GetMapping("/list")
    public ApiResponse<?> list(@Parameter(description = "负责人ID（可选）") @RequestParam(required = false) Long ownerId) {
        return ApiResponse.ok(projectInfoService.listProjects(ownerId));
    }

    @Operation(summary = "新增或编辑项目")
    @PostMapping("/save")
    public ApiResponse<?> save(@RequestBody ProjectSaveRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        if (request.getId() != null) {
            // 编辑：只有项目负责人本人可以操作
            ProjectInfo existing = projectInfoMapper.selectById(request.getId());
            if (existing == null) {
                return ApiResponse.fail("项目不存在");
            }
            if (!currentUserId.equals(existing.getOwnerId())) {
                return ApiResponse.fail("权限不足：您不是该项目的负责人");
            }
        } else {
            // 新建：只有"负责人"角色可以创建项目
            if (!"负责人".equals(SecurityUtils.getCurrentRole())) {
                return ApiResponse.fail("权限不足：仅项目负责人可创建项目");
            }
        }

        // DTO → Entity
        ProjectInfo project = new ProjectInfo();
        project.setId(request.getId());
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStatus(request.getStatus());
        project.setPriority(request.getPriority());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setOwnerId(currentUserId);

        return ApiResponse.ok("保存成功",
                projectInfoService.saveProject(project, currentUserId));
    }

    @Operation(summary = "删除项目")
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        // 只有项目负责人本人可以删除
        ProjectInfo existing = projectInfoMapper.selectById(id);
        if (existing == null) {
            return ApiResponse.fail("项目不存在");
        }
        if (!currentUserId.equals(existing.getOwnerId())) {
            return ApiResponse.fail("权限不足：您不是该项目的负责人");
        }

        projectInfoService.deleteProject(id, currentUserId);
        return ApiResponse.ok("删除成功");
    }
}
