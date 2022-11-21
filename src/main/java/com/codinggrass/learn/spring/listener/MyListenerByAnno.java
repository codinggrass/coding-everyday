package com.codinggrass.learn.spring.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyListenerByAnno {
    @EventListener
    public void processListener(MyEvent event) {
        String eventMessage = event.getMessage();
        log.info("from MyListenerByAnno message {}", eventMessage);
    }
}
