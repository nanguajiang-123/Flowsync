package hgc.flowsyncapi.service.impl;

import hgc.flowsyncapi.mapper.*;
import hgc.flowsyncapi.service.OverviewService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OverviewServiceImpl implements OverviewService {

    private final UserMapper userMapper;
    private final ProjectInfoMapper projectInfoMapper;
    private final TaskInfoMapper taskInfoMapper;
    private final TaskSummaryMapper taskSummaryMapper;

    public OverviewServiceImpl(UserMapper userMapper, ProjectInfoMapper projectInfoMapper,
                               TaskInfoMapper taskInfoMapper, TaskSummaryMapper taskSummaryMapper) {
        this.userMapper = userMapper;
        this.projectInfoMapper = projectInfoMapper;
        this.taskInfoMapper = taskInfoMapper;
        this.taskSummaryMapper = taskSummaryMapper;
    }

    @Override
    public Map<String, Long> getOverview() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("userCount", userMapper.selectCount(null));
        stats.put("projectCount", projectInfoMapper.selectCount(null));
        stats.put("taskCount", taskInfoMapper.selectCount(null));
        stats.put("summaryCount", taskSummaryMapper.selectCount(null));
        return stats;
    }
}
