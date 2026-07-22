package hgc.flowsyncapi.controller;

import hgc.flowsyncapi.common.ApiResponse;
import hgc.flowsyncapi.entity.TaskInfo;
import hgc.flowsyncapi.service.TaskInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    private final TaskInfoService taskInfoService;

    public TaskController(TaskInfoService taskInfoService) {
        this.taskInfoService = taskInfoService;
    }

    @GetMapping
    public ApiResponse<?> list(@RequestParam(required = false) Long projectId) {
        try {
            List<TaskInfo> list = taskInfoService.listTasks(projectId);
            return ApiResponse.ok(list);
        } catch (Exception e) {
            log.error("获取任务列表失败", e);
            return ApiResponse.fail("获取任务列表失败: " + e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<?> save(@RequestBody TaskInfo task) {
        try {
            return ApiResponse.ok("保存成功", taskInfoService.saveTask(task));
        } catch (Exception e) {
            log.error("保存任务失败", e);
            return ApiResponse.fail("保存任务失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        try {
            taskInfoService.deleteTask(id);
            return ApiResponse.ok("删除成功");
        } catch (Exception e) {
            log.error("删除任务失败", e);
            return ApiResponse.fail("删除任务失败: " + e.getMessage());
        }
    }
}
