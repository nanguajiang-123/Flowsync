package hgc.flowsyncapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import hgc.flowsyncapi.entity.TaskInfo;
import hgc.flowsyncapi.mapper.TaskInfoMapper;
import hgc.flowsyncapi.service.TaskInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskInfoServiceImpl implements TaskInfoService {

    private final TaskInfoMapper taskInfoMapper;

    public TaskInfoServiceImpl(TaskInfoMapper taskInfoMapper) {
        this.taskInfoMapper = taskInfoMapper;
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
    public TaskInfo saveTask(TaskInfo task) {
        if (task.getId() != null && taskInfoMapper.selectById(task.getId()) != null) {
            taskInfoMapper.updateById(task);
        } else {
            taskInfoMapper.insert(task);
        }
        return task;
    }

    @Override
    public void deleteTask(Long id) {
        taskInfoMapper.deleteById(id);
    }
}
