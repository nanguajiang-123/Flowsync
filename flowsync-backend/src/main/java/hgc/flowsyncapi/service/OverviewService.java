package hgc.flowsyncapi.service;

import java.util.Map;

public interface OverviewService {

    /** 获取总览统计 */
    Map<String, Long> getOverview();
}
