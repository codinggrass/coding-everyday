package com.codinggrass.learn.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * @author hao hao
 * @date : 2022/12/10
 **/
public class StringEventFactory implements EventFactory<StringEvent> {
    
    @Override
    public StringEvent newInstance() {
        return new StringEvent();
    }
}
