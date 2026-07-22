package hgc.flowsyncapi.service;

import hgc.flowsyncapi.entity.TaskSummary;

import java.util.List;

public interface TaskSummaryService {

    /** 获取总结列表，按 id 倒序 */
    List<TaskSummary> listSummaries();

    /** 新增总结 */
    TaskSummary addSummary(TaskSummary summary, Long operatorId);
}
