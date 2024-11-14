package com.metoo.nrsm.core.config.socket.linux.command.ping.pingUtils;

import com.metoo.nrsm.core.config.socket.Global;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-03-28 11:08
 *
 * 创建执行traceroute命令工厂类
 */
public class PingFactory {

    public Ping getPing(){
        if (Global.env.equals("prod")) {
            return new ProcessPing();
        }else if("dev".equals(Global.env)){
            return new SshPing();
        }
        return null;
    }

    public static class PingExec{

        private static Map<String, Future> threadMap = new ConcurrentHashMap<>();

        public static void exec(String sid, String ip){
            PingFactory traceRouteFactory = new PingFactory();
            Ping ping = traceRouteFactory.getPing();
            Future<?> future = ping.exec(sid, ip);
            threadMap.put(sid, future);
        }

        public static void interruptExec(String sid){

            if(threadMap.get(sid) != null){

                Future<?> future = threadMap.get(sid);

                boolean flag = future.cancel(true); // 传入true表示尝试中断正在运行的任务

                System.out.println(flag);
            }
        }

    }

}
