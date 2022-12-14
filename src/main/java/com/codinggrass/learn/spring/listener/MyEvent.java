package com.codinggrass.learn.spring.listener;


import org.springframework.context.ApplicationEvent;

public class MyEvent extends ApplicationEvent {
    private String message;

    public MyEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public MyEvent(Object source) {
        super(source);
    }

    public String getMessage() {
        return message;
    }
}
