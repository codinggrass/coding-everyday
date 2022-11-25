package com.codinggrass.learn.spring.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyFirstListener implements ApplicationListener<MyEvent> {

    @Override
    public void onApplicationEvent(MyEvent myEvent) {
        String eventMessage = myEvent.getMessage();
        log.info("message :{}",eventMessage);
    }
}
