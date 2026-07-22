package hgc.flowsyncapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import hgc.flowsyncapi.entity.TaskInfo;
import hgc.flowsyncapi.entity.TaskLog;
import hgc.flowsyncapi.mapper.TaskInfoMapper;
import hgc.flowsyncapi.mapper.TaskLogMapper;
import hgc.flowsyncapi.service.OperationLogService;
import hgc.flowsyncapi.service.TaskLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskLogServiceImpl implements TaskLogService {

    private final TaskLogMapper taskLogMapper;
    private final TaskInfoMapper taskInfoMapper;
    private final OperationLogService operationLogService;

    public TaskLogServiceImpl(TaskLogMapper taskLogMapper,
                              TaskInfoMapper taskInfoMapper,
                              OperationLogService operationLogService) {
        this.taskLogMapper = taskLogMapper;
        this.taskInfoMapper = taskInfoMapper;
        this.operationLogService = operationLogService;
    }

    @Override
    public List<TaskLog> listTaskLogs(Long taskId) {
        QueryWrapper<TaskLog> wrapper = new QueryWrapper<>();
        if (taskId != null) {
            wrapper.eq("task_id", taskId);
        }
        wrapper.orderByDesc("id");
        return taskLogMapper.selectList(wrapper);
    }

    @Override
    public TaskLog addTaskLog(TaskLog log, Long operatorId) {
        // 确保操作人正确
        log.setOperatorId(operatorId);
        taskLogMapper.insert(log);

        // 记录操作日志：通过 taskId 查出所属项目
        Long projectId = null;
        if (log.getTaskId() != null) {
            TaskInfo task = taskInfoMapper.selectById(log.getTaskId());
            if (task != null) {
                projectId = task.getProjectId();
            }
        }
        operationLogService.record(operatorId, "TASK_LOG", "CREATE",
                log.getId(),
                "记录了任务进度",
                projectId);

        return log;
    }
}
