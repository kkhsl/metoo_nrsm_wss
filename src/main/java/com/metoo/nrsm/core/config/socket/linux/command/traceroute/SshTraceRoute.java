package com.metoo.nrsm.core.config.socket.linux.command.traceroute;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import com.metoo.nrsm.core.config.socket.NoticeEndpoint;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-03-28 10:54
 */
@Service
public class SshTraceRoute implements TraceRoute {

    @Override
    public void exec(String sid, String ip) {
        String host = "192.168.5.205";
        int port = 22;
        String username = "nrsm";
        String password = "metoo89745000";
        // 创建连接
        Connection conn = new Connection(host, port);
        // 启动连接
        try {
            conn.connect();
            // 验证用户密码
            conn.authenticateWithPassword(username, password);

            Session session = conn.openSession();

            session.execCommand("traceroute " + ip);

            BufferedReader br = new BufferedReader(new InputStreamReader(session.getStdout()));
            String line;
            while((line = br.readLine())!=null){
                NoticeEndpoint.sendPingMessage(sid, line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
