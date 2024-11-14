package com.metoo.nrsm.core.config.socket.linux.command.ping.pingUtils;

import com.metoo.nrsm.core.config.socket.NoticeEndpoint;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-03-28 10:57
 */
@Slf4j
public class ProcessPingThread implements Runnable {

    private String sid;
    private String ip;

    public ProcessPingThread() {
    }

    public ProcessPingThread(String sid, String ip){
        this.sid = sid;
        this.ip = ip;
    }

    @Override
    public void run(){

        ProcessBuilder builder = new ProcessBuilder("ping", ip); // 修改参数以符合你的需求

        try {
            Process process = builder.start();

            // 读取ping命令的输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));

            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                boolean flag = NoticeEndpoint.sendPingMessage(sid, line);
                if(!flag){
                    break;
                }
            }
            // 等待ping命令执行完成
            process.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
