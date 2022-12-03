package com.codinggrass.learn.java.concurrent.ballsimple;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

@Slf4j
public class BounceFrame extends JFrame {
    private BallComponent component;
    public static final int STEPS = 1000;
    public static final int DELAY = 3;
    Thread a;

    BounceFrame() {
        setTitle("tan tan qiu");
        component = new BallComponent();
        add(component, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        addButton(buttonPanel, "start", event -> addBallInNewThread());
        addButton(buttonPanel, "close", event -> System.exit(0));
        addButton(buttonPanel, "interrupt", event -> interruptThread());
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
    }

    private void interruptThread() {
        log.info("interruptThread {}", a.getName());
        a.interrupt();
    }

    private void addBall() {
        try {
            Ball ball = new Ball();
            component.add(ball);
            for (int i = 0; i < STEPS; i++) {
                ball.move(component.getBounds());
                component.paint(component.getGraphics());
                Thread.sleep(DELAY);
            }
        } catch (InterruptedException e) {
            log.info("ball InterruptedException:{}", e);
        }
    }

    private void addBallInNewThread() {
        Ball ball = new Ball();
        component.add(ball);
        Runnable r = () -> {
            log.info("current thread {}", Thread.currentThread().getName());
            log.info("current thread isInterrupted {}", Thread.currentThread().isInterrupted());
            try {
                for (int i = 0; i < STEPS; i++) {
                    ball.move(component.getBounds());
                    component.paint(component.getGraphics());
                    Thread.sleep(DELAY);
                }
            } catch (InterruptedException e) {
                log.info("current thread isInterrupted {} --", Thread.currentThread().isInterrupted());
                Thread.currentThread().interrupt();
            }
            /*
             * 线程的状态，新建、可运行、阻塞、等待、时间等待、终止
             * 线程的属性：
             * 1、线程优先级
             *   每个线程都有一个优先级，默认集成父线程
             *   最低1 - 5 - 10 高 10级
             *   static void yield() ：当前线程优先级处于让步状态，同级的线程可以优先执被调度
             *2、守护线程
             *   setDemon(true) 把当前线程转化为守护线程
             *   守护线程不访问固有资源、文件、数据库
             * 3、线程组
             * 4、未捕获异常处理器
             *
             *
             * */
            log.info("thread state 33 {}", Thread.currentThread().getState());
        };
        Thread thread = new Thread(r);
        log.info("thread state 11 {}", thread.getState());
        thread.start();
        a = thread;
        log.info("thread state 22 {}", thread.getState());
    }


    private void addButton(Container container, String title, ActionListener listener) {
        JButton button = new JButton(title);
        container.add(button);
        button.addActionListener(listener);
    }
}
