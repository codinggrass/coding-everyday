package com.codinggrass.learn.java.concurrent.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * @author hao hao
 * @date : 2023/1/14
 **/
@Slf4j
public class CreateThread {
    public static void main(String[] args) {

        // 1 创建线程实例，调用start方法即可
        Thread thread = new Thread();
        thread.start();
        // 但是此线程什么都没做。

        // 2 创建自有线程实例，调用start方法
        log.info("new MyThread");
        Thread thread2 = new MyThread();
        thread2.start();

        // 3 创建线程实例，传入runable实例
        Thread thread3 = new Thread(new MyRunnable());
        thread3.start();

        // 4 java8 lambda创建
        Thread thread4 = new Thread(() -> log.info("start new thread in lambda {}", Thread.currentThread().getName()));
        //一个线程对象只能调用一次start()方法；
        thread4.start();
        
        // 直接调用run()方法，相当于调用了一个普通的Java方法，当前线程并没有任何改变，也不会启动新线程,必须调用Thread实例的start()方法才能启动新线程
        thread4.run();

        //可以对线程设定优先级 Thread.setPriority(int n) // 1~10, 默认值5
    }


}

@Slf4j
class MyRunnable implements Runnable {

    @Override
    public void run() {
        log.info("start new thread in MyRunable: {}", Thread.currentThread().getName());
    }
}

@Slf4j
class MyThread extends Thread {
    @Override
    public void run() {
        log.info("start new thread : {}", Thread.currentThread().getName());
    }
}

