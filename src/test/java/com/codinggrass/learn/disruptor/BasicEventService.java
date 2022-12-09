package com.codinggrass.learn.disruptor;

/**
 * @author hao hao
 * @date : 2022/12/10
 **/

public interface BasicEventService {

    void publish(String value);

    long eventCount();

    void init();
}
