package com.codinggrass.learn.queue;

import com.codinggrass.learn.spring.annotion.TestFunctionInterfaceAnnotion;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
public class QueueTest {
    @Test
    void testFirstQueue() throws InterruptedException {
        BlockingQueue blockingQueue = new ArrayBlockingQueue(10);

        Assertions.assertThrows(NullPointerException.class, () -> blockingQueue.add(null));
        Assertions.assertThrows(NoSuchElementException.class, () -> blockingQueue.remove());
        Assertions.assertThrows(NullPointerException.class, () -> blockingQueue.offer(null));
        Assertions.assertEquals(0, blockingQueue.size());
        Assertions.assertDoesNotThrow(() -> blockingQueue.poll());

        newAIntList().stream().forEach((e) -> blockingQueue.add(e));
        Assertions.assertEquals(10, blockingQueue.size());
        Assertions.assertThrows(IllegalStateException.class, () -> blockingQueue.add(11));

        Object poll = blockingQueue.poll(10, TimeUnit.SECONDS);
        log.info("{} {}", poll.getClass().getSimpleName(), poll);
        log.info("blockingQueue.poll(10, TimeUnit.SECONDS) end !");
        ArrayList<@Nullable Object> objects = Lists.newArrayList();
        blockingQueue.drainTo(objects, 20);
        objects.stream().forEach((e) -> log.info("{}", e));
    }

    @Test
    void testPriorityBlockingQueue() throws InterruptedException {
        PriorityBlockingQueue priorityBlockingQueue = new PriorityBlockingQueue();
        priorityBlockingQueue.add(10);
        priorityBlockingQueue.add(1);
        priorityBlockingQueue.add(2);
        Assertions.assertEquals(1, priorityBlockingQueue.take());
        Assertions.assertEquals(2, priorityBlockingQueue.take());
        Assertions.assertEquals(10, priorityBlockingQueue.take());

        PriorityBlockingQueue<Integer> integerPriorityBlockingQueue = new PriorityBlockingQueue<>(10, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                // 大数在前
                return o2 - o1;
            }
        });
        integerPriorityBlockingQueue.add(1);
        integerPriorityBlockingQueue.add(2);
        integerPriorityBlockingQueue.add(3);
        log.info(integerPriorityBlockingQueue.toString());
        Assertions.assertEquals(3, integerPriorityBlockingQueue.take());
        log.info(integerPriorityBlockingQueue.toString());
        Assertions.assertEquals(2, integerPriorityBlockingQueue.take());
    }

    private class MyCompare implements Comparator<Integer> {


        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 < o2 ? -1 : o1 == o2 ? 0 : 1;
        }
    }

    @Test
        /*使用函数接口，自定义方法内容*/
    void testFunctionInterfaceUse() {
        TestFunctionInterfaceAnnotion testFunctionInterfaceAnnotion = (a) -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < a; i++) {
                stringBuilder.append("a");
                stringBuilder.append("+");
            }
            log.info(stringBuilder.toString());
            return stringBuilder.toString();
        };

        testFunctionInterfaceAnnotion.test(10);
    }

    private ArrayList<Integer> newAIntList() {
        return Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    void quickNewListMethod() {
        // 1. normal
        List<String> a = new ArrayList<>();
        a.add("1");
        a.add("2");
        // 2.
        List<String> strings = Arrays.asList("1", "2");
        Assertions.assertThrows(UnsupportedOperationException.class, () -> strings.add("3"));

        // 3. stream
        List<String> collect = Stream.of("1", "2").collect(Collectors.toList());
        collect.add(String.valueOf(2));
        Assertions.assertEquals(3, collect.size());

        // 4.
        ArrayList<String> strings1 = Lists.newArrayList("1", "2", "3");
        strings1.add(String.valueOf(4));
        Assertions.assertEquals(4, strings1.size());
    }


    @Test
    void testReentrantLock() {
        ReentrantLock lock = new ReentrantLock();
        log.info("{}", lock.getHoldCount());

    }
}
