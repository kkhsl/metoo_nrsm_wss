package com.metoo.nrsm.core.config.socket.linux.command.ping.pingUtils;

import com.metoo.nrsm.core.config.socket.linux.command.ExecThreadPool;
import lombok.extern.slf4j.Slf4j;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-03-28 16:15
 */
@Slf4j
public class TestInterrupt {

    public static void main(String[] args) {
        Runnable r = new Runnable() {
            @Override
            public void run() {

                System.out.println();
                System.out.println(1);
            }
        };

        ExecThreadPool.getInstance().addThread(r);
        Thread a = (Thread) r;

        log.info("打断状态：" + a.isInterrupted());


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("打断状态：" + a.isInterrupted());

        log.info("running end......");

    }
}
