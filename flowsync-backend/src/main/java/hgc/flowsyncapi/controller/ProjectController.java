package hgc.flowsyncapi.controller;

import hgc.flowsyncapi.common.ApiResponse;
import hgc.flowsyncapi.entity.ProjectInfo;
import hgc.flowsyncapi.service.ProjectInfoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectInfoService projectInfoService;

    public ProjectController(ProjectInfoService projectInfoService) {
        this.projectInfoService = projectInfoService;
    }

    /** 获取项目列表 */
    @GetMapping
    public ApiResponse<?> list() {
        return ApiResponse.ok(projectInfoService.listProjects());
    }

    /** 新增或编辑项目 */
    @PostMapping
    public ApiResponse<?> save(@RequestBody ProjectInfo project) {
        return ApiResponse.ok("保存成功", projectInfoService.saveProject(project));
    }

    /** 删除项目 */
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        projectInfoService.deleteProject(id);
        return ApiResponse.ok("删除成功");
    }
}
