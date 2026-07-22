package hgc.flowsyncapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hgc.flowsyncapi.entity.ProjectInfo;
import hgc.flowsyncapi.mapper.ProjectInfoMapper;
import hgc.flowsyncapi.service.ProjectInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectInfoServiceImpl implements ProjectInfoService {

    private final ProjectInfoMapper projectInfoMapper;

    public ProjectInfoServiceImpl(ProjectInfoMapper projectInfoMapper) {
        this.projectInfoMapper = projectInfoMapper;
    }

    @Override
    public List<ProjectInfo> listProjects() {
        LambdaQueryWrapper<ProjectInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ProjectInfo::getId);
        return projectInfoMapper.selectList(wrapper);
    }

    @Override
    public ProjectInfo saveProject(ProjectInfo project) {
        if (project.getId() != null && projectInfoMapper.selectById(project.getId()) != null) {
            projectInfoMapper.updateById(project);
        } else {
            projectInfoMapper.insert(project);
        }
        return project;
    }

    @Override
    public void deleteProject(Long id) {
        projectInfoMapper.deleteById(id);
    }
}
