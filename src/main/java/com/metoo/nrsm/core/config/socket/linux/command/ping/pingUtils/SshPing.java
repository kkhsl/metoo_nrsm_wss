package com.metoo.nrsm.core.config.socket.linux.command.ping.pingUtils;

import com.metoo.nrsm.core.config.socket.linux.command.ExecThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-03-28 10:57
 */
@Slf4j
@Service
public class SshPing implements Ping {

//    @Override
//    public void exec(String sid, String ip){
//        String host = "192.168.5.205";
//        int port = 22;
//        String username = "nrsm";
//        String password = "metoo89745000";
//        // 创建连接
//        Connection conn = new Connection(host, port);
//        // 启动连接
//        try {
//            conn.connect();
//            // 验证用户密码
//            conn.authenticateWithPassword(username, password);
//
//            Session session = conn.openSession();
//
//            session.execCommand("ping " + ip);
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(session.getStdout()));
//            String line;
//            while((line = br.readLine())!=null){
//                log.info(line);
//                boolean flag = NoticeEndpoint.sendPingMessage(sid, line);
//                if(!flag){
//                    break;
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }




    @Override
    public Future<?> exec(String sid, String ip){
//        Runnable t = new SshPingThread(sid, ip);
//        try {
//            ExecThreadPool.getInstance().addThread(t);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            threadMap.put("sid", (Thread) t);
//        }

        Runnable t = new SshPingThread(sid, ip);
        try {
           Future<?> future = ExecThreadPool.getInstance().submit(t);
           return future;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
