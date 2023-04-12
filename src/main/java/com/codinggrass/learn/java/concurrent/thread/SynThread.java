package com.codinggrass.learn.java.concurrent.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * @author hao hao
 * @date : 2023/4/12
 **/
@Slf4j
public class SynThread {
    public static void main(String[] args) throws InterruptedException {
        Thread add = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                synchronized (Count.lock) {
                    Count.num++;
                }
            }
        });

        Thread dec = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                synchronized (Count.lock) {
                    Count.num--;
                }
            }
        });
        add.start();
        dec.start();
        add.join();
        dec.join();
        log.info("{}", Count.num);
    }
}

class Count {
    public static final Object lock = new Object();
    public static int num;
}
