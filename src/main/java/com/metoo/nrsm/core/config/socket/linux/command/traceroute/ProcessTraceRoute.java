package com.metoo.nrsm.core.config.socket.linux.command.traceroute;

import com.metoo.nrsm.core.config.socket.NoticeEndpoint;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-03-28 10:57
 */
@Service
public class ProcessTraceRoute implements TraceRoute {

    @Override
    public void exec(String sid, String ip) {
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
