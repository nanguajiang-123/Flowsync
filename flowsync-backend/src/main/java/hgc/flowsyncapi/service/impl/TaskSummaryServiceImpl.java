package hgc.flowsyncapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hgc.flowsyncapi.entity.TaskSummary;
import hgc.flowsyncapi.mapper.TaskSummaryMapper;
import hgc.flowsyncapi.service.TaskSummaryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskSummaryServiceImpl implements TaskSummaryService {

    private final TaskSummaryMapper taskSummaryMapper;

    public TaskSummaryServiceImpl(TaskSummaryMapper taskSummaryMapper) {
        this.taskSummaryMapper = taskSummaryMapper;
    }

    @Override
    public List<TaskSummary> listSummaries() {
        LambdaQueryWrapper<TaskSummary> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(TaskSummary::getId);
        return taskSummaryMapper.selectList(wrapper);
    }

    @Override
    public TaskSummary addSummary(TaskSummary summary) {
        taskSummaryMapper.insert(summary);
        return summary;
    }
}
