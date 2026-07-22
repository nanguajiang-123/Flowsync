package hgc.flowsyncapi.controller;

import hgc.flowsyncapi.common.ApiResponse;
import hgc.flowsyncapi.entity.TaskInfo;
import hgc.flowsyncapi.service.TaskInfoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskInfoService taskInfoService;

    public TaskController(TaskInfoService taskInfoService) {
        this.taskInfoService = taskInfoService;
    }

    /** 获取任务列表，支持 ?projectId= 筛选 */
    @GetMapping
    public ApiResponse<?> list(@RequestParam(required = false) Long projectId) {
        return ApiResponse.ok(taskInfoService.listTasks(projectId));
    }

    /** 新增或编辑任务 */
    @PostMapping
    public ApiResponse<?> save(@RequestBody TaskInfo task) {
        return ApiResponse.ok("保存成功", taskInfoService.saveTask(task));
    }

    /** 删除任务 */
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        taskInfoService.deleteTask(id);
        return ApiResponse.ok("删除成功");
    }
}
