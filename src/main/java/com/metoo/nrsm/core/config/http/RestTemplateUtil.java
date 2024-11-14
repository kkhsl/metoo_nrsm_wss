package com.metoo.nrsm.core.config.http;

import com.alibaba.fastjson.JSONObject;
import com.metoo.nrsm.core.config.socket.NoticeWebsocketResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateUtil {

    @Autowired
    private RestTemplate restTemplate;

    private static String BASE_URL;

    @Value("${wsUrl}")
    public void setUrl(String url) {
        RestTemplateUtil.BASE_URL = url;
    }

    public NoticeWebsocketResp getObjByStr(String url, String params){
        String realUrl = BASE_URL + url;
        ResponseEntity<String> result = null;
        try {
            result = this.restTemplate.getForEntity(realUrl + "?requestParams={params}", String.class, params);
            NoticeWebsocketResp resp = JSONObject.parseObject(result.getBody(), NoticeWebsocketResp.class);
            return resp;
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return new NoticeWebsocketResp();
    }

    public NoticeWebsocketResp get(String url){
        String realUrl = BASE_URL + url;
        ResponseEntity<String> result = null;
        try {
            result = this.restTemplate.getForEntity(realUrl, String.class);
            if(result.getStatusCodeValue() == 200){
                NoticeWebsocketResp resp = JSONObject.parseObject(result.getBody(), NoticeWebsocketResp.class);
                return resp;
            }else{
                NoticeWebsocketResp resp = new NoticeWebsocketResp();
                resp.setNoticeStatus(-1);
                return resp;
            }
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return new NoticeWebsocketResp();
    }

}
