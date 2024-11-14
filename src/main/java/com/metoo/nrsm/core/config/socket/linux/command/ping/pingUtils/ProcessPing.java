package com.metoo.nrsm.core.config.socket.linux.command.ping.pingUtils;

import com.metoo.nrsm.core.config.socket.linux.command.ExecThreadPool;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-03-28 10:57
 */
@Service
public class ProcessPing implements Ping {

    @Override
    public Future<?> exec(String sid, String ip){

        Runnable t = new ProcessPingThread(sid, ip);
        try {
            Future<?> future = ExecThreadPool.getInstance().submit(t);
            return future;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
