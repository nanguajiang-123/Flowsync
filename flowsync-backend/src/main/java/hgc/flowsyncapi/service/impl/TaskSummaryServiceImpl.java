package hgc.flowsyncapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hgc.flowsyncapi.entity.TaskSummary;
import hgc.flowsyncapi.mapper.TaskSummaryMapper;
import hgc.flowsyncapi.service.OperationLogService;
import hgc.flowsyncapi.service.TaskSummaryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskSummaryServiceImpl implements TaskSummaryService {

    private final TaskSummaryMapper taskSummaryMapper;
    private final OperationLogService operationLogService;

    public TaskSummaryServiceImpl(TaskSummaryMapper taskSummaryMapper,
                                  OperationLogService operationLogService) {
        this.taskSummaryMapper = taskSummaryMapper;
        this.operationLogService = operationLogService;
    }

    @Override
    public List<TaskSummary> listSummaries() {
        LambdaQueryWrapper<TaskSummary> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(TaskSummary::getId);
        return taskSummaryMapper.selectList(wrapper);
    }

    @Override
    public TaskSummary addSummary(TaskSummary summary, Long operatorId) {
        summary.setCreatedBy(operatorId);
        taskSummaryMapper.insert(summary);

        // 记录操作日志
        operationLogService.record(operatorId, "TASK_SUMMARY", "CREATE",
                summary.getId(),
                "新增了总结",
                summary.getProjectId());

        return summary;
    }
}
