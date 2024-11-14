package com.metoo.nrsm.core.config.socket.linux.command.traceroute;

import com.metoo.nrsm.core.config.socket.Global;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-03-28 11:08
 *
 * 创建执行traceroute命令工厂类
 */
public class TraceRouteFactory {

    public TraceRoute getTraceRoute(){
        if (Global.env.equals("prod")) {
            return new ProcessTraceRoute();
        }else if("dev".equals(Global.env)){
            return new SshTraceRoute();
        }
        return null;
    }

    public static class TraceRouteExec{
        public static void exec(String sid, String ip){
            TraceRouteFactory traceRouteFactory = new TraceRouteFactory();
            TraceRoute traceRouteService = traceRouteFactory.getTraceRoute();
            traceRouteService.exec(sid, ip);
        }
    }

}
