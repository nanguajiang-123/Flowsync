package hgc.flowsyncapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import hgc.flowsyncapi.entity.ProjectInfo;
import hgc.flowsyncapi.mapper.ProjectInfoMapper;
import hgc.flowsyncapi.security.SecurityUtils;
import hgc.flowsyncapi.service.OperationLogService;
import hgc.flowsyncapi.service.ProjectInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectInfoServiceImpl implements ProjectInfoService {

    private final ProjectInfoMapper projectInfoMapper;
    private final OperationLogService operationLogService;

    public ProjectInfoServiceImpl(ProjectInfoMapper projectInfoMapper,
                                  OperationLogService operationLogService) {
        this.projectInfoMapper = projectInfoMapper;
        this.operationLogService = operationLogService;
    }

    /**
     * 校验当前用户是否有项目管理权限（负责人或管理员）
     */
    private void checkProjectPermission() {
        String role = SecurityUtils.getCurrentRole();
        if (!("负责人".equals(role) || "管理员".equals(role))) {
            throw new RuntimeException("权限不足：仅项目负责人可管理项目");
        }
    }

    @Override
    public List<ProjectInfo> listProjects(Long ownerId) {
        LambdaQueryWrapper<ProjectInfo> wrapper = new LambdaQueryWrapper<>();
        if (ownerId != null) {
            wrapper.eq(ProjectInfo::getOwnerId, ownerId);
        }
        wrapper.orderByDesc(ProjectInfo::getId);
        return projectInfoMapper.selectList(wrapper);
    }

    @Override
    public ProjectInfo saveProject(ProjectInfo project, Long operatorId) {
        checkProjectPermission();

        boolean isUpdate = project.getId() != null
                && projectInfoMapper.selectById(project.getId()) != null;

        if (isUpdate) {
            projectInfoMapper.updateById(project);
            // 记录操作日志
            operationLogService.record(operatorId, "PROJECT", "UPDATE",
                    project.getId(),
                    "更新了项目「" + project.getName() + "」",
                    project.getId());
        } else {
            projectInfoMapper.insert(project);
            // 记录操作日志（此时 project.getId() 已被 MyBatis-Plus 回填）
            operationLogService.record(operatorId, "PROJECT", "CREATE",
                    project.getId(),
                    "创建了项目「" + project.getName() + "」",
                    project.getId());
        }
        return project;
    }

    @Override
    public void deleteProject(Long id, Long operatorId) {
        checkProjectPermission();

        ProjectInfo project = projectInfoMapper.selectById(id);
        if (project != null) {
            // 先记录日志，再删除
            operationLogService.record(operatorId, "PROJECT", "DELETE",
                    id,
                    "删除了项目「" + project.getName() + "」",
                    id);
        }
        projectInfoMapper.deleteById(id);
    }
}
