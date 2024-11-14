package com.metoo.nrsm.core.config.socket.linux.command.exec;

import com.metoo.nrsm.core.config.socket.NoticeEndpoint;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-03-29 10:37
 */
@Slf4j
public class ProcessCommand implements ExecCommand {

    @Override
    public Thread exec(String sid, String[] param, Integer type) {
        Thread t = new Thread(() -> {
            ProcessBuilder builder = new ProcessBuilder(param); // 修改参数以符合你的需求

            try {
                Process process = builder.start();

                // 读取ping命令的输出
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));

                String line;
                StringBuffer stringBuffer = new StringBuffer();
                if(type != null && type == 1){
                    while((line = reader.readLine())!=null){
                        stringBuffer.append(line).append("\n\r");
                    }
                    NoticeEndpoint.sendPingMessage(sid, stringBuffer.toString());
                }else{
                    while ((line = reader.readLine()) != null) {

                        log.info("线程打断状态：" + Thread.currentThread().isInterrupted());

                        // 调用中断关闭接口
                        if(Thread.currentThread().isInterrupted()){
                            break;
                        }

                        // 断开连接，手动终止循环
                        boolean flag = NoticeEndpoint.sendPingMessage(sid, line);
                        if(!flag){
                            break;
                        }

//                        NoticeEndpoint.sendPingMessage(sid, line);
                    }
                    // 等待ping命令执行完成
                    process.waitFor();
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                // 这里不用两阶段终止，异常抛出自动终止（非循环体内休眠打断）
            }
        });
        t.start();
        return t;
    }
}
