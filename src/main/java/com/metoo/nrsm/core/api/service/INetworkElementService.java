package com.metoo.nrsm.core.api.service;

import com.metoo.nrsm.core.config.socket.NoticeWebsocketResp;

public interface INetworkElementService {

    NoticeWebsocketResp getNeAvailable(String params);

    NoticeWebsocketResp getNeSnmpStatus(String params);

    NoticeWebsocketResp getOnlineAp(String params);

}
