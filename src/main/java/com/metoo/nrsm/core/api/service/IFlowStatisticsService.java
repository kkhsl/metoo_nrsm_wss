package com.metoo.nrsm.core.api.service;

import com.metoo.nrsm.core.config.socket.NoticeWebsocketResp;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-04-02 11:37
 */
public interface IFlowStatisticsService {

    NoticeWebsocketResp getFlux(String params);
}
