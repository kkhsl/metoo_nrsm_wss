package com.metoo.nrsm.core.config.socket.linux.command.ping.pingUtils;

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
 * @date 2024-03-28 10:57
 */
@Slf4j
public class SshPingThread implements Runnable {

    private String sid;
    private String ip;

    public SshPingThread(String sid, String ip){
        this.sid = sid;
        this.ip = ip;
    }

    @Override
    public void run(){
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

            session.execCommand("ping " + ip);

            BufferedReader br = new BufferedReader(new InputStreamReader(session.getStdout()));
            String line;
            while((line = br.readLine())!=null){
                log.info(line);
                if(Thread.currentThread().isInterrupted()){
                    break;
                }
                log.info(line + "打断状态：" + Thread.currentThread().isInterrupted());
                // 断开连接时-可以使用clientMap清除sid，同一使用这个条件判断退出循环
                // 也可以使用终端线程的方法，
                // 中断线程new Thread可以实现；线程池待测试，终端后，仍然执行
                boolean flag = NoticeEndpoint.sendPingMessage2(sid, line);
                if(!flag){
                    break;
                }
            }
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

}
