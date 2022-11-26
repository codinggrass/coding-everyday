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

    BounceFrame() {
        setTitle("tan tan qiu");
        component = new BallComponent();
        add(component, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        addButton(buttonPanel, "start", event -> addBallInNewThread());
        addButton(buttonPanel, "close", event -> System.exit(0));
        add(buttonPanel, BorderLayout.SOUTH);
        pack();
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
                Thread.currentThread().interrupt();
            }
            log.info("thread state 33 {}", Thread.currentThread().getState());
        };
        Thread thread = new Thread(r);
        log.info("thread state 11 {}", thread.getState());
        thread.start();
        log.info("thread state 22 {}", thread.getState());
    }


    private void addButton(Container container, String title, ActionListener listener) {
        JButton button = new JButton(title);
        container.add(button);
        button.addActionListener(listener);
    }
}
