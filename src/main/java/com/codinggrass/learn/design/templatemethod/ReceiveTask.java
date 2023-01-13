package com.codinggrass.learn.design.templatemethod;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReceiveTask extends AbstractTemplate{
    @Override
    protected void postReceiveTask() {
        log.info("post ReceiveTask...");
        log.info("step 5");
        log.info("step 6");
    }

    @Override
    protected void preReceiveTask() {
        log.info("step 1");
        log.info("step 2");
        log.info("pre ReceiveTask...");
    }
}
