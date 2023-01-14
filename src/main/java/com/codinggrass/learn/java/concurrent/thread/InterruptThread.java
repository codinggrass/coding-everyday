package com.codinggrass.learn.java.concurrent.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * @author hao hao
 * @date : 2023/1/14
 **/
@Slf4j
public class InterruptThread {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new MyThread1();
        thread.start();
        Thread.sleep(8);
        // 如果对main线程调用interrupt()，join()方法会立刻抛出InterruptedException
        thread.interrupt();
        thread.join();
        log.info("end");
    }
}

@Slf4j
class MyThread1 extends Thread {
    public void run() {
        int n = 0;
        while (!isInterrupted()) {
            n++;
            log.info(n + " hello!");
        }
    }
}
