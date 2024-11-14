package com.metoo.nrsm.core.config.socket.linux.command.exec;

import com.metoo.nrsm.core.config.socket.Global;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-03-28 11:08
 *
 * 创建执行traceroute命令工厂类
 */
public class CommandFactory {

    public ExecCommand getExecCommand(){
        if (Global.env.equals("prod")) {
            return new ProcessCommand();
        }else if("dev".equals(Global.env)){
            return new SshCommand();
        }
        return null;
    }

    public static class Exec{

        private static Map<String, Thread> threadMap = new ConcurrentHashMap<>();

        public static void exec(String sid, String[] param, Integer type){
            CommandFactory commandFactory = new CommandFactory();
            ExecCommand command = commandFactory.getExecCommand();
            Thread t = command.exec(sid, param, type);
            synchronized (threadMap) {
                threadMap.put(sid, t);
            }
        }

        public static void interruptExec(String sid){
            synchronized (threadMap) {
                if(threadMap.get(sid) != null){
                    try {
                        threadMap.get(sid).interrupt();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    threadMap.remove(sid);
                }
            }
        }
    }

}
