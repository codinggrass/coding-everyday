package com.codinggrass.learn.disruptor;

import com.lmax.disruptor.RingBuffer;

/**
 * @author hao hao
 * @date : 2022/12/10
 **/
public class StringEventProducer {
    private RingBuffer<StringEvent> ringBuffer;

    public StringEventProducer(RingBuffer<StringEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onDate(String content) {
        long sequence = ringBuffer.next();

        StringEvent stringEvent = ringBuffer.get(sequence);
        stringEvent.setValue(content);
        ringBuffer.publish(sequence);
        
    }
}
