package com.metoo.nrsm.core.api.service;

import com.metoo.nrsm.core.config.socket.NoticeWebsocketResp;

public interface ITerminalService {

    NoticeWebsocketResp getTerminal(String params);

    NoticeWebsocketResp getPartitionTerminal(String params);

    NoticeWebsocketResp getTerminalCount(String params);

    NoticeWebsocketResp getUnitTerminal(String params);

}
