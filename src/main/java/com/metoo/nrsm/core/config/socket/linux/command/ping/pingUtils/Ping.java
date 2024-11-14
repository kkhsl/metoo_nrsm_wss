package com.metoo.nrsm.core.config.socket.linux.command.ping.pingUtils;

import java.util.concurrent.Future;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-03-28 10:53
 */
public interface Ping {

    Future<?> exec(String sid, String ip);
}
