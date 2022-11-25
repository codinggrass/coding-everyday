package com.codinggrass.learn.spring.listener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
//TODO
public class ListenerMain {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ListenerMain.class);
        ConfigurableApplicationContext context = springApplication.run(args);

        context.publishEvent(new MyEvent(new Object(),"first time publish event, happy!"));
    }
}
