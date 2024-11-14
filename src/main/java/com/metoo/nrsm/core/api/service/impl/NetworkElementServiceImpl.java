package com.metoo.nrsm.core.api.service.impl;

import com.metoo.nrsm.core.api.service.INetworkElementService;
import com.metoo.nrsm.core.config.http.RestTemplateUtil;
import com.metoo.nrsm.core.config.socket.NoticeWebsocketResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NetworkElementServiceImpl implements INetworkElementService {

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Override
    public NoticeWebsocketResp getNeAvailable(String params) {
        String url = "/ws/api/ne/list";
        NoticeWebsocketResp result = restTemplateUtil.getObjByStr(url, params);
        return result;
    }

    @Override
    public NoticeWebsocketResp getNeSnmpStatus(String params) {
        String url = "/ws/api/ne/snmp/status";
        NoticeWebsocketResp result = restTemplateUtil.getObjByStr(url, params);
        return result;
    }

    @Override
    public NoticeWebsocketResp getOnlineAp(String params) {
        String url = "/ws/api/ac/ap/online";
        NoticeWebsocketResp result = restTemplateUtil.getObjByStr(url, params);
        return result;
    }

}
