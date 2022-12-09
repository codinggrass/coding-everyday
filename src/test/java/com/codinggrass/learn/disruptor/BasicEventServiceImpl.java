package com.codinggrass.learn.disruptor;

import com.lmax.disruptor.dsl.Disruptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * @author hao hao
 * @date : 2022/12/10
 **/
@Service
@Slf4j
public class BasicEventServiceImpl implements BasicEventService {

    private static final int BUFFER_SIZE = 16;

    private Disruptor<StringEvent> disruptor;

    private StringEventProducer stringEventProducer;

    public final AtomicLong eventCount = new AtomicLong();

    //TODO
    @PostConstruct
    public void init() {
        Executor executor = Executors.newCachedThreadPool();

        disruptor = new Disruptor<>(new StringEventFactory(),
                BUFFER_SIZE,
                new CustomizableThreadFactory("event-handle-"));
        Consumer<?> eventCountPrinter = (Consumer<Object>) o -> {
            long count = eventCount.incrementAndGet();
            log.info("receive [{}]", count);
        };

        disruptor.handleEventsWith(new StringEventHandler(eventCountPrinter));

        disruptor.start();

        stringEventProducer = new StringEventProducer(disruptor.getRingBuffer());
    }

    @Override
    public void publish(String value) {
        stringEventProducer.onDate(value);

    }

    @Override
    public long eventCount() {
        return eventCount.get();
    }
}
