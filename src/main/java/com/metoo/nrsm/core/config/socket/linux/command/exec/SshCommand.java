package com.metoo.nrsm.core.config.socket.linux.command.exec;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import com.metoo.nrsm.core.config.socket.NoticeEndpoint;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-03-29 10:37
 */
@Slf4j
public class SshCommand implements ExecCommand {

    @Override
    public Thread exec(String sid, String[] param, Integer type) {
        Thread t = new Thread(() -> {
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

                StringBuffer stringBuffer = new StringBuffer();
                BufferedReader br = new BufferedReader(new InputStreamReader(session.getStdout()));
                String line;
                if(type != null && type == 1){
                    while((line = br.readLine())!=null){
                        stringBuffer.append(line).append("\n\r");
                    }
                    NoticeEndpoint.sendPingMessage(sid, stringBuffer.toString());
                }else{
                    while((line = br.readLine())!=null){
                        Thread current = Thread.currentThread();
                        log.info("线程打断状态：" + current.isInterrupted());
//                    if(Thread.currentThread().isInterrupted()){
//                        break;
//                    }

//                    boolean flag = NoticeEndpoint.sendPingMessage(sid, line);
//                    if(!flag){
//                        break;
//                    }

                        NoticeEndpoint.sendPingMessage(sid, line);

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            log.info("线程打断状态2：" + current.isInterrupted());
                            current.interrupt();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        return t;
    }
}
