package hgc.flowsyncapi.service;

import hgc.flowsyncapi.entity.ProjectInfo;

import java.util.List;

public interface ProjectInfoService {

    /** 获取项目列表（按 id 倒序），可选按负责人筛选 */
    List<ProjectInfo> listProjects(Long ownerId);

    /** 新增或编辑项目：有 id 则更新，无 id 则新增 */
    ProjectInfo saveProject(ProjectInfo project, Long operatorId);

    /** 删除项目 */
    void deleteProject(Long id, Long operatorId);
}
