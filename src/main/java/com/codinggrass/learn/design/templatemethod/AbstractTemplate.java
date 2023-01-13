package com.codinggrass.learn.design.templatemethod;

import lombok.extern.slf4j.Slf4j;

/**
 * 模板方法抽象类
 */
@Slf4j
public abstract class AbstractTemplate {
    public void receiveTask() {
        preReceiveTask();
        common();
        postReceiveTask();
    }

    protected abstract void postReceiveTask();

    protected abstract void preReceiveTask();

    private void common() {
        log.info("common task handle");
    }
}
