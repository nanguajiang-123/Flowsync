package hgc.flowsyncapi.controller;

import hgc.flowsyncapi.common.ApiResponse;
import hgc.flowsyncapi.entity.TaskInfo;
import hgc.flowsyncapi.service.TaskInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "任务管理", description = "任务的增删改查，支持按项目筛选")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private final TaskInfoService taskInfoService;

    public TaskController(TaskInfoService taskInfoService) {
        this.taskInfoService = taskInfoService;
    }

    @Operation(summary = "获取任务列表")
    @GetMapping("/list")
    public ApiResponse<?> list(@Parameter(description = "项目ID（可选）") @RequestParam(required = false) Long projectId) {
        try {
            List<TaskInfo> list = taskInfoService.listTasks(projectId);
            return ApiResponse.ok(list);
        } catch (Exception e) {
            log.error("获取任务列表失败", e);
            return ApiResponse.fail("获取任务列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "新增或编辑任务")
    @PostMapping("/save")
    public ApiResponse<?> save(
            @RequestParam Long currentUserId,
            @RequestBody TaskInfo task) {

        try {
            // 只有创建新任务时才设置创建人
            if (task.getId() == null) {
                task.setCreatorId(currentUserId);
            }

            return ApiResponse.ok(
                    "保存成功",
                    taskInfoService.saveTask(task, currentUserId)
            );
        } catch (Exception e) {
            log.error("保存任务失败", e);
            return ApiResponse.fail("保存任务失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除任务")
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id,
                                  @RequestParam Long currentUserId) {
        try {
            taskInfoService.deleteTask(id, currentUserId);
            return ApiResponse.ok("删除成功");
        } catch (Exception e) {
            log.error("删除任务失败", e);
            return ApiResponse.fail("删除任务失败: " + e.getMessage());
        }
    }
}
