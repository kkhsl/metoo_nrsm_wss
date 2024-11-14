package com.metoo.nrsm.core.config.socket;

import org.springframework.stereotype.Component;

@Component
public class JobTest {

//    @Scheduled(cron = "*/5 * * * * ?")
//    public void nrsm() throws InterruptedException {
//            for (int i = 1; i <= 10; i++){
//                Thread.sleep(1000);
//                System.out.println("a" + i);
//            }
//    }
//
//    @Scheduled(cron = "*/5 * * * * ?")
//    public void wss() throws InterruptedException {
//        for (int i = 1; i <= 10; i++){
//            Thread.sleep(1000);
//            System.out.println("b" + i);
//        }
//    }

//    @Async
//    @Scheduled(cron = "*/3 * * * * ?")
    public void w() throws InterruptedException {
        Long time=System.currentTimeMillis();
        System.out.println(Thread.currentThread().getId() + "任1务开始");
        System.out.println("===任务执行耗时："+(System.currentTimeMillis()-time)+"===");

    }

    //    @Async
//    @Scheduled(cron = "*/3 * * * * ?")
    public void ee() throws InterruptedException {
        Long time=System.currentTimeMillis();
        Thread.sleep(10000);
        System.out.println(Thread.currentThread().getId() + "任2务开始");
        System.out.println("===任务执行耗时："+(System.currentTimeMillis()-time)+"===");

    }

    //    @Async
//    @Scheduled(cron = "*/3 * * * * ?")
    public void eee() throws InterruptedException {
        Long time=System.currentTimeMillis();
        System.out.println(Thread.currentThread().getId() + "任3务开始");
        System.out.println("===任务执行耗时："+(System.currentTimeMillis()-time)+"===");
    }

}
