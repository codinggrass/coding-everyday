package com.codinggrass.learn.java.concurrent.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * @author hao hao
 * @date : 2023/4/12
 **/
@Slf4j
public class DaemonThread {
    public static void main(String[] args) {
        Thread mainThread = new Thread(() -> {
            Thread timerThread = new TimerThread();
            // 设置子线程为daemon ，则当jvm结束后，会将daemon进程结束
            timerThread.setDaemon(true);
            timerThread.start();
            log.info("main thread end");
        });
        mainThread.start();

    }


}

@Slf4j
class TimerThread extends Thread {
    @Override
    public void run() {
        while (true) {
            try {
                log.info("Deamon Thread");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.info("{} exit", Thread.currentThread().getName());
                break;
            }
        }
    }

}

