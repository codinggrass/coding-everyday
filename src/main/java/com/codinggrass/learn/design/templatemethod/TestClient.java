package com.codinggrass.learn.design.templatemethod;

public class TestClient {

    public static void main(String[] args) {
        AbstractTemplate receiveTask = new ReceiveTask();
        receiveTask.receiveTask();
        //打印 "hello"
    }
}
