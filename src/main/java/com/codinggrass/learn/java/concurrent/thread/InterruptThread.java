package com.codinggrass.learn.java.concurrent.thread;

import lombok.extern.slf4j.Slf4j;

/**
 * @author hao hao
 * @date : 2023/1/14
 **/
@Slf4j
public class InterruptThread {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new MyThread2();
        thread.start();
        Thread.sleep(1000);
        /**
         * 设置中断的2种方式：
         *          1、join->外层interrupt->抛出InterruptedException
         *          2、外层interrupt->内层判断isInterrupted是否退出run方法
         *          3、再内层线程中定义标识位
         *          如果对main线程调用interrupt()，join()方法会立刻抛出InterruptedException
         *          如果新开线程中正在join等待，对其调用interrupt，立刻抛出InterruptedException
         *          此例子中，main.start -> MyThread2.start ->MyThread1 -> MyThread1.join
         *          -> main.interrupt -> MyThread2抛出中断异常
         */
        thread.interrupt();
        log.info("end");

        // 3、再内层线程中定义标识位
        MyThread3 myThread3 = new MyThread3();
        myThread3.start();
        Thread.sleep(1000);
        //
        /**
         * 线程间共享的变量,使用volatile关键字标记,确保每个线程都能读取到更新后的变量值
         * 为什么要对线程间共享的变量用关键字volatile声明？这涉及到Java的内存模型。
         * 在Java虚拟机中，变量的值保存在主内存中，但是，当线程访问变量时，它会先获取一个副本，
         * 并保存在自己的工作内存中。如果线程修改了变量的值，虚拟机会在某个时刻把修改后的值回写到主内存，但是，这个时间是不确定的！
         *
         * ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐
         *            Main Memory
         * │                               │
         *    ┌───────┐┌───────┐┌───────┐
         * │  │ var A ││ var B ││ var C │  │
         *    └───────┘└───────┘└───────┘
         * │     │ ▲               │ ▲     │
         *  ─ ─ ─│─│─ ─ ─ ─ ─ ─ ─ ─│─│─ ─ ─
         *       │ │               │ │
         * ┌ ─ ─ ┼ ┼ ─ ─ ┐   ┌ ─ ─ ┼ ┼ ─ ─ ┐
         *       ▼ │               ▼ │
         * │  ┌───────┐  │   │  ┌───────┐  │
         *    │ var A │         │ var C │
         * │  └───────┘  │   │  └───────┘  │
         *    Thread 1          Thread 2
         * └ ─ ─ ─ ─ ─ ─ ┘   └ ─ ─ ─ ─ ─ ─ ┘
         * 这会导致如果一个线程更新了某个变量，另一个线程读取的值可能还是更新前的。
         * 例如，主内存的变量a = true，线程1执行a = false时，它在此刻仅仅是把变量a的副本变成了false，
         * 主内存的变量a还是true，在JVM把修改后的a回写到主内存之前，其他线程读取到的a的值仍然是true，这就造成了多线程之间共享的变量不一致。
         *
         * 因此，volatile关键字的目的是告诉虚拟机：
         *
         * 每次访问变量时，总是获取主内存的最新值；
         * 每次修改变量后，立刻回写到主内存。
         * volatile关键字解决的是可见性问题：当一个线程修改了某个共享变量的值，其他线程能够立刻看到修改后的值。
         *
         * 如果我们去掉volatile关键字，运行上述程序，发现效果和带volatile差不多，
         * 这是因为在x86的架构下，JVM回写主内存的速度非常快，但是，换成ARM的架构，就会有显著的延迟。
         *
         */
        myThread3.running = false;
        myThread3.join();
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
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.info("MyThread1 InterruptedException");
                break;
            }

        }
    }
}

@Slf4j
class MyThread2 extends Thread {
    public void run() {
        Thread thread = new MyThread1();
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            log.info("MyThread2 InterruptedException");
        }
        thread.interrupt();
        log.info("MyThread2 end");
    }
}

@Slf4j
class MyThread3 extends Thread {
    public volatile boolean running = true;

    public void run() {
        while (running) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            log.info("MyThread3 running");
        }
        log.info("MyThread3 end ");
    }
}
