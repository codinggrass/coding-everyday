package com.codinggrass.learn.java.concurrent.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * @author hao hao
 * @date : 2023/1/14
 **/
@Slf4j
public class ThreadStatus {
    public static void main(String[] args) throws InterruptedException {

        // 一个线程还可以等待另一个线程直到其运行结束。例如，main线程在启动t线程后，可以通过t.join()等待t线程结束后再继续运行：
        Thread thread = new Thread(() -> {
            log.info("run thread {}", Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info("end thread {}", Thread.currentThread().getName());
        });
        log.info("start");
        thread.start();
        log.info("join");
        thread.join();//一直等待thread结束后，再继续执行。
        //thread.join(500); 指定500ms后不再等待
        //thread.join(500);
        log.info("end");

    }
}
