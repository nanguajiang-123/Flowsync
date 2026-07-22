package hgc.flowsyncapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hgc.flowsyncapi.entity.OperationLog;
import hgc.flowsyncapi.mapper.OperationLogMapper;
import hgc.flowsyncapi.service.OperationLogService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    public OperationLogServiceImpl(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    @Override
    public void record(Long userId, String module, String action, Long targetId, String detail, Long projectId) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setModule(module);
        log.setAction(action);
        log.setTargetId(targetId);
        log.setDetail(detail);
        log.setProjectId(projectId);
        operationLogMapper.insert(log);
    }

    @Override
    public List<OperationLog> listByProject(Long projectId) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OperationLog::getProjectId, projectId)
               .orderByDesc(OperationLog::getId);
        return operationLogMapper.selectList(wrapper);
    }
}
