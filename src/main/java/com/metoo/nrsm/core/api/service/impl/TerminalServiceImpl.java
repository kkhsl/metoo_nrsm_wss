package com.metoo.nrsm.core.api.service.impl;

import com.metoo.nrsm.core.api.service.ITerminalService;
import com.metoo.nrsm.core.config.http.RestTemplateUtil;
import com.metoo.nrsm.core.config.socket.NoticeWebsocketResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TerminalServiceImpl implements ITerminalService {

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Override
    public NoticeWebsocketResp getTerminal(String params) {
        String url = "/ws/api/terminal/dt";
        NoticeWebsocketResp result = restTemplateUtil.getObjByStr(url, params);
        return result;
    }

    @Override
    public NoticeWebsocketResp getPartitionTerminal(String params) {
        String url = "/ws/api/terminal/partition";
        NoticeWebsocketResp result = restTemplateUtil.getObjByStr(url, params);
        return result;
    }

    @Override
    public NoticeWebsocketResp getTerminalCount(String params) {
        String url = "/ws/api/terminal/count";
        NoticeWebsocketResp result = restTemplateUtil.getObjByStr(url, params);
        return result;
    }

    @Override
    public NoticeWebsocketResp getUnitTerminal(String params) {
        String url = "/ws/api/terminal/partition/unit/terminal";
        NoticeWebsocketResp result = restTemplateUtil.getObjByStr(url, params);
        return result;
    }
}
