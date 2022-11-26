package com.codinggrass.learn.java.concurrent.ballsimple;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author hao hao
 * @Date : 2022/11/26 22:40
 **/
public class BallComponent extends JPanel {
    public static final int DEFAULT_WIDTH = 450;
    public static final int DEFAULT_HIGH = 500;

    private List<Ball> ballList = new ArrayList<>();

    void add(Ball ball) {
        ballList.add(ball);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;
//        ballList.stream().forEach(ball -> graphics2D.fill(ball.getShape()));
        for (Ball ball : ballList) {
            graphics2D.fill(ball.getShape());
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HIGH);
    }
}
