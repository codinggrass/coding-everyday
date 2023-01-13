package com.codinggrass.learn.java.stream;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/**
 * @author hao hao
 * @date : 2022/12/30
 **/
@Slf4j
public class StreamLearnMainTest {

    @Test
    void create_a_stream() {
        List<String> stringList = Arrays.asList("a", "b", "c");
        long count = stringList.stream().parallel().count();
        List<String> collect = stringList.stream().map(member -> member + "+").collect(Collectors.toList());
        log.info(collect.toString());

        //直接使用of
        Stream<String> a = Stream.of("a", "b", "c");

        //通过一个数组进行
        int[] array = {1, 2, 3};
        IntStream stream = Arrays.stream(array, 1, 2);

        //创建无限流的方法
        Stream<String> hello = Stream.generate(() -> "hello");

        Stream<Double> generate = Stream.generate(Math::random);


    }

}