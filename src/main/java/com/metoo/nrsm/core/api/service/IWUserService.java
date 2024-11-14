package com.metoo.nrsm.core.api.service;

import com.metoo.nrsm.core.config.socket.NoticeWebsocketResp;

public interface IWUserService {

    NoticeWebsocketResp selectObjById(Long id);
}
