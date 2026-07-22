package hgc.flowsyncapi.service;

import hgc.flowsyncapi.entity.TaskLog;

import java.util.List;

public interface TaskLogService {

    /** 获取进度记录列表，可按 taskId 筛选，按 id 倒序 */
    List<TaskLog> listTaskLogs(Long taskId);

    /** 新增进度记录 */
    TaskLog addTaskLog(TaskLog log, Long operatorId);
}
