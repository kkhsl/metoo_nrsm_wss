package com.metoo.nrsm.core.config.socket.linux.command.ping.pingUtils;

import com.metoo.nrsm.core.config.socket.NoticeEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-03-26 15:25
 */
@Slf4j
public class PingThread {

    private boolean flag = true;

    public void setFlag(boolean flag){
        this.flag = flag;
    }

    private static Map<String, Thread> threadMap = new ConcurrentHashMap<>();

//    @Test
//    public void test(){
//
//        Thread t = ping("ping 192.168.5.101 -t");
////        String uuid = UUID.randomUUID().toString();
////        System.out.println(uuid);
////        threadMap.put(uuid, t);
//    }
//
//    @Test
//    public void test2(){
//        Thread t = threadMap.get("");
//        t.interrupt();
//    }

//    public Thread ping(String param){
//        if(StringUtil.isNotEmpty(param)){
//            String[] params = param.split(" ");
//            Thread t = exec(params);
//            return t;
//        }
//        return null;
//    }

    public static void close(){
        for (String s : PingThread.threadMap.keySet()) {
            PingThread.threadMap.get(s).interrupt();
        }

    }

    @Test
    public static void test2(String sid){
        threadMap.get(sid).interrupt();
    }

    public void test(String sid){
        Thread t = exec(sid);
        threadMap.put(sid, t);
    }

    // 执行并返回执行ping线程，可用手动关闭输出
    public Thread exec(String sid){
        Thread t = new Thread(() -> {
            ProcessBuilder builder = new ProcessBuilder("ping", "192.168.5.101", "-t"); // 修改参数以符合你的需求

            try {
                Process process = builder.start();

                // 读取ping命令的输出
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("GBK")));

                String line;

                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    NoticeEndpoint.sendPingMessage(sid, line);
                }
                // 等待ping命令执行完成
                process.waitFor();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
        return t;
    }

    public void testExec(String sid){
        Thread t = new Thread(() -> {
            ProcessBuilder builder = new ProcessBuilder("ping", "192.168.5.101", "-t"); // 修改参数以符合你的需求

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
        });
        t.start();
    }

    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            ProcessBuilder builder = new ProcessBuilder("ping", "192.168.5.101", "-t"); // 修改参数以符合你的需求

            try {
                Process process = builder.start();

                // 读取ping命令的输出
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;

                while ((line = reader.readLine()) != null) {
//                    NoticeEndpoint.sendPingMessage(sid, line);
                }
                // 等待ping命令执行完成
                process.waitFor();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }


    public void testExec2(String sid, String ip){
        ProcessBuilder builder = new ProcessBuilder("ping", ip, "-t"); // 修改参数以符合你的需求

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

    /**
     * 可用
     * @param sid
     * @param ip
     */
    public static void testExec3(String sid, String ip){
        ProcessBuilder builder = new ProcessBuilder("ping", ip, "-t"); // 修改参数以符合你的需求

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

    public void exec5(String sid, String ip){

        Thread t = exec4(sid, ip);

        threadMap.put(sid, t);

    }

    /**
     * 可用
     * @param sid
     * @param ip
     */
    public static Thread exec4(String sid, String ip){
        Thread t = new Thread(() -> {
            ProcessBuilder builder = new ProcessBuilder("ping", ip); // 修改参数以符合你的需求

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

    public static Thread exec5(String sid, String[] params){
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

