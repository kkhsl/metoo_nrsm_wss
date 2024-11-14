package com.metoo.nrsm.core.config.socket.thread;

import com.alibaba.fastjson.JSONObject;
import com.metoo.nrsm.core.api.service.ITerminalService;
import com.metoo.nrsm.core.config.application.ApplicationContextUtils;
import com.metoo.nrsm.core.config.socket.NoticeWebsocketResp;
import org.springframework.util.StringUtils;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-04-03 14:24
 */
public class GetPartitionTerminalRunnable implements Runnable {

    private String sid;

    private Object params;

    private Map<String, Map<String, String>> taskParams;

    public Map<String, Session> clients;



    public GetPartitionTerminalRunnable() {
    }

    public GetPartitionTerminalRunnable(String sid, Object params, Map<String, Map<String, String>> taskParams, Map<String, Session> clients) {
        this.sid = sid;
        this.params = params;
        this.taskParams = taskParams;
        this.clients = clients;
    }



    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        ITerminalService terminalService = (ITerminalService) ApplicationContextUtils.getBean("terminalServiceImpl");

        NoticeWebsocketResp resp = terminalService.getPartitionTerminal(JSONObject.toJSONString(params));

        if (!StringUtils.isEmpty(sid)) {
            String message = JSONObject.toJSONString(resp);
            Map userMap = taskParams.get(sid);
            if (userMap != null) {
                Session session = clients.get(sid);
                if (session != null) {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
