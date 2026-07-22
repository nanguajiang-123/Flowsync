package hgc.flowsyncapi.service;

import hgc.flowsyncapi.entity.OperationLog;

import java.util.List;

public interface OperationLogService {

    /**
     * 记录一条操作日志（由各业务 Service 内部调用）
     */
    void record(Long userId, String module, String action, Long targetId, String detail, Long projectId);

    /**
     * 按项目ID查询操作日志列表（按时间倒序）
     */
    List<OperationLog> listByProject(Long projectId);
}
