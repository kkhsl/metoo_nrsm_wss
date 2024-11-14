package com.metoo.nrsm.core.config.socket.linux.command.exec;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-03-29 10:36
 */
public interface ExecCommand {

    Thread exec(String sid, String[] param, Integer type);
}
