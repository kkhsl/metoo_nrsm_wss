package com.metoo.nrsm.core.api.service.impl;

import com.metoo.nrsm.core.api.service.IWUserService;
import com.metoo.nrsm.core.config.http.RestTemplateUtil;
import com.metoo.nrsm.core.config.socket.NoticeWebsocketResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WUserServiceImpl implements IWUserService {

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Override
    public NoticeWebsocketResp selectObjById(Long id) {
        String url = "/ws/api/user";
        StringBuffer sb = new StringBuffer(url);
        sb.append("?userId=" + id);
        NoticeWebsocketResp result = restTemplateUtil.get(sb.toString());
        return result;
    }
}
