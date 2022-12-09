package com.codinggrass.learn.disruptor;

import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

/**
 * @author hao hao
 * @date : 2022/12/10
 **/
@Slf4j
public class StringEventHandler implements EventHandler<StringEvent> {

    Consumer<?> consumer;

    public StringEventHandler(Consumer<?> consumer) {
        this.consumer = consumer;
    }

    @Override

    public void onEvent(StringEvent stringEvent, long sequence, boolean endOfBatch) throws Exception {
        log.info("sequence [{}] ,endOfBatch [{}] ,event :{}", sequence, endOfBatch, stringEvent);

        Thread.sleep(100);

        if (null != consumer) {
            consumer.accept(null);
        }
    }
}
