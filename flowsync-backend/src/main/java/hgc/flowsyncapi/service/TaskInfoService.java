package hgc.flowsyncapi.service;

import hgc.flowsyncapi.entity.TaskInfo;

import java.util.List;

public interface TaskInfoService {

    /** 获取任务列表，可按 projectId 筛选，按 id 倒序 */
    List<TaskInfo> listTasks(Long projectId);

    /** 新增或编辑任务：有 id 则更新，无 id 则新增 */
    TaskInfo saveTask(TaskInfo task);

    /** 删除任务 */
    void deleteTask(Long id);
}
