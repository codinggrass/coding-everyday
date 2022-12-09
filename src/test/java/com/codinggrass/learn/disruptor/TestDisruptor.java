package com.codinggrass.learn.disruptor;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * @author hao hao
 * @date : 2022/12/10
 **/
//@RunWith(SpringRunner.class)
////@SpringBootTest
@Slf4j
//@SpringBootConfiguration
public class TestDisruptor {


    @Test
    public void testDisruptor() throws InterruptedException {
        BasicEventService basicEventService = new BasicEventServiceImpl();
        basicEventService.init();
        log.info("start a disruptor test");

        int count = 100;

        for (int i = 0; i < count; i++) {
            log.info("publish {}", i);
            basicEventService.publish(String.valueOf(i));
        }

        Thread.sleep(1000);

        Assert.assertEquals(count, basicEventService.eventCount());
    }
}
