package com.metoo.nrsm.core.manager;

import com.metoo.nrsm.core.api.service.ITerminalService;
import com.metoo.nrsm.core.api.service.IWUserService;
import com.metoo.nrsm.core.config.socket.NoticeWebsocketResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/admin")
@RestController
public class TestController {

    @Autowired
    private ITerminalService networkElementService;
    @Autowired
    private IWUserService userService;

//    @GetMapping
//    public Object get(){
//        String params = "{\"noticeType\":\"1\", \"userId\":\"1\", \"params\":{\"currentPage\":1,\"pageSize\":2}}";
//        NoticeWebsocketResp resp = this.networkElementService.getNeAvailable("/nrsm/ne/testApi", params);
//        return resp;
//    }

    @GetMapping("/getUser")
    public Object getUser(){
        NoticeWebsocketResp resp = this.userService.selectObjById(1L);
        return resp;
    }

    @GetMapping("/test")
    public Object test(){
        return "test";
    }


}
