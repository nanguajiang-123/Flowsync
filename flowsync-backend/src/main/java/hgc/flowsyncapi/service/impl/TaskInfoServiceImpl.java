package hgc.flowsyncapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import hgc.flowsyncapi.entity.TaskInfo;
import hgc.flowsyncapi.mapper.TaskInfoMapper;
import hgc.flowsyncapi.service.OperationLogService;
import hgc.flowsyncapi.service.TaskInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskInfoServiceImpl implements TaskInfoService {

    private final TaskInfoMapper taskInfoMapper;
    private final OperationLogService operationLogService;

    public TaskInfoServiceImpl(TaskInfoMapper taskInfoMapper,
                               OperationLogService operationLogService) {
        this.taskInfoMapper = taskInfoMapper;
        this.operationLogService = operationLogService;
    }

    @Override
    public List<TaskInfo> listTasks(Long projectId) {
        QueryWrapper<TaskInfo> wrapper = new QueryWrapper<>();
        if (projectId != null) {
            wrapper.eq("project_id", projectId);
        }
        wrapper.orderByDesc("id");
        return taskInfoMapper.selectList(wrapper);
    }

    @Override
    public TaskInfo saveTask(TaskInfo task, Long operatorId) {
        boolean isUpdate = task.getId() != null
                && taskInfoMapper.selectById(task.getId()) != null;

        if (isUpdate) {
            taskInfoMapper.updateById(task);
            operationLogService.record(operatorId, "TASK", "UPDATE",
                    task.getId(),
                    "更新了任务「" + task.getTitle() + "」",
                    task.getProjectId());
        } else {
            taskInfoMapper.insert(task);
            operationLogService.record(operatorId, "TASK", "CREATE",
                    task.getId(),
                    "创建了任务「" + task.getTitle() + "」",
                    task.getProjectId());
        }
        return task;
    }

    @Override
    public void deleteTask(Long id, Long operatorId) {
        TaskInfo task = taskInfoMapper.selectById(id);
        if (task != null) {
            // 先记录日志，再删除
            operationLogService.record(operatorId, "TASK", "DELETE",
                    id,
                    "删除了任务「" + task.getTitle() + "」",
                    task.getProjectId());
        }
        taskInfoMapper.deleteById(id);
    }
}
