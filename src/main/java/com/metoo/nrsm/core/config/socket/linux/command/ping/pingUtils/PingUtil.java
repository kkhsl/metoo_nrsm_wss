package com.metoo.nrsm.core.config.socket.linux.command.ping.pingUtils;

import com.metoo.nrsm.core.config.socket.NoticeEndpoint;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-03-26 15:25
 */
@Component
public class PingUtil {


    public void exec(String sid, String ip){

        ProcessBuilder builder = new ProcessBuilder("ping", ip); // 修改参数以符合你的需求

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
