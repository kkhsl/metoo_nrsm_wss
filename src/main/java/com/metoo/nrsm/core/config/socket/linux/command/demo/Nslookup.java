package com.metoo.nrsm.core.config.socket.linux.command.demo;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import com.metoo.nrsm.core.config.socket.NoticeEndpoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-04-01 10:09
 */
public class Nslookup {

    public void test(String sid, String[] param){
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

            StringBuilder sb = new StringBuilder();
            for (String s : param) {
                sb.append(s).append(" ");
            }

            session.execCommand(sb.toString().trim());

            BufferedReader br = new BufferedReader(new InputStreamReader(session.getStdout()));

            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while((line = br.readLine())!=null){
                stringBuffer.append(line).append("\n\r");
            }
            NoticeEndpoint.sendPingMessage(sid, stringBuffer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
