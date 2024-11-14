package com.metoo.nrsm.core.config.socket.linux.command.traceroute;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import com.metoo.nrsm.core.config.socket.NoticeEndpoint;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-03-28 11:25
 */
public class TraceRouteDemo {


    @Test
    public void ssh() throws IOException {
        String host = "192.168.5.205";
        int port = 22;
        String username = "nrsm";
        String password = "metoo89745000";
        // 创建连接
        Connection conn = new Connection(host, port);
        // 启动连接
        conn.connect();
        // 验证用户密码
        conn.authenticateWithPassword(username, password);

        Session session = conn.openSession();

        session.execCommand("traceroute 202.103.100.247");

        consumeInputStream2(session.getStdout());
    }

    public static void consumeInputStream2(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while((line = br.readLine())!=null){
            System.out.println(line);
        }
    }

    /**
     *   消费inputstream，并返回
     */
    public static String consumeInputStream(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String s ;
        StringBuilder sb = new StringBuilder();
        while((s=br.readLine())!=null){
            sb.append(s);
        }
        return sb.toString();
    }

    @Test
    public void processBuilder(String sid, String ip){

        ProcessBuilder builder = new ProcessBuilder("traceroute", ip); // 修改参数以符合你的需求

        try {
            Process process = builder.start();

            // 读取ping命令的输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));

            String line;

            while ((line = reader.readLine()) != null) {
                NoticeEndpoint.sendPingMessage(sid, line);
            }
            // 等待ping命令执行完成
            process.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


}
