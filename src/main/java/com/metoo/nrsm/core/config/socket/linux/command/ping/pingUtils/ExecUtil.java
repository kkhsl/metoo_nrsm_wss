package com.metoo.nrsm.core.config.socket.linux.command.ping.pingUtils;

import com.metoo.nrsm.core.config.socket.NoticeEndpoint;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-03-29 10:22
 */
@Slf4j
public class ExecUtil {

    private static Map<String, Thread> threadMap = new ConcurrentHashMap<>();

    public static Thread exec(String sid, String[] params){
        Thread t = new Thread(() -> {
            ProcessBuilder builder = new ProcessBuilder(params); // 修改参数以符合你的需求

            try {
                Process process = builder.start();

                // 读取ping命令的输出
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));

                String line;

                while ((line = reader.readLine()) != null) {
                    if(Thread.currentThread().isInterrupted()){
                        break;
                    }
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
        });
        t.start();
        threadMap.put(sid, t);
        return t;
    }

    public static void interruptExec(String sid){
        if(threadMap.get(sid) != null){
            threadMap.get(sid).interrupt();

        }
    }
}
