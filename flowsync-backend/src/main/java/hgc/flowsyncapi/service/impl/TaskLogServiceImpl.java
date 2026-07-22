package hgc.flowsyncapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import hgc.flowsyncapi.entity.TaskLog;
import hgc.flowsyncapi.mapper.TaskLogMapper;
import hgc.flowsyncapi.service.TaskLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskLogServiceImpl implements TaskLogService {

    private final TaskLogMapper taskLogMapper;

    public TaskLogServiceImpl(TaskLogMapper taskLogMapper) {
        this.taskLogMapper = taskLogMapper;
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
    public TaskLog addTaskLog(TaskLog log) {
        taskLogMapper.insert(log);
        return log;
    }
}
