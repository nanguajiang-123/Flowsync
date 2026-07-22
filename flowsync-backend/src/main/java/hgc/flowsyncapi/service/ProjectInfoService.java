package hgc.flowsyncapi.service;

import hgc.flowsyncapi.entity.ProjectInfo;

import java.util.List;

public interface ProjectInfoService {

    /** 获取全部项目（按 id 倒序） */
    List<ProjectInfo> listProjects();

    /** 新增或编辑项目：有 id 则更新，无 id 则新增 */
    ProjectInfo saveProject(ProjectInfo project);

    /** 删除项目 */
    void deleteProject(Long id);
}
