package com.metoo.nrsm.core.api.service.impl;

import com.metoo.nrsm.core.api.service.IFlowStatisticsService;
import com.metoo.nrsm.core.config.http.RestTemplateUtil;
import com.metoo.nrsm.core.config.socket.NoticeWebsocketResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-04-02 11:38
 */
@Service
public class FlowStatisticsServiceImpl implements IFlowStatisticsService {

    @Autowired
    private RestTemplateUtil restTemplateUtil;


    @Override
    public NoticeWebsocketResp getFlux(String params) {
        String url = "/ws/api/flow/statistics";
        NoticeWebsocketResp result = restTemplateUtil.getObjByStr(url, params);
        return result;
    }
}
