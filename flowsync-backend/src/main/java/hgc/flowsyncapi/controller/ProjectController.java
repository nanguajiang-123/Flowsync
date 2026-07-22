package hgc.flowsyncapi.controller;

import hgc.flowsyncapi.common.ApiResponse;
import hgc.flowsyncapi.entity.ProjectInfo;
import hgc.flowsyncapi.service.ProjectInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "项目管理", description = "项目的增删改查")
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectInfoService projectInfoService;

    public ProjectController(ProjectInfoService projectInfoService) {
        this.projectInfoService = projectInfoService;
    }

    @Operation(summary = "获取项目列表")
    @GetMapping("/list")
    public ApiResponse<?> list() {
        return ApiResponse.ok(projectInfoService.listProjects());
    }

    @Operation(summary = "新增或编辑项目")
    @PostMapping("/save")
    public ApiResponse<?> save(
            @RequestParam Long currentUserId,
            @RequestBody ProjectInfo project) {

        // 仅新增项目时，自动将当前登录用户设为负责人
        if (project.getId() == null) {
            project.setOwnerId(currentUserId);
        }

        return ApiResponse.ok(
                "保存成功",
                projectInfoService.saveProject(project)
        );
    }

    @Operation(summary = "删除项目")
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        projectInfoService.deleteProject(id);
        return ApiResponse.ok("删除成功");
    }
}
