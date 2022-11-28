package com.codinggrass.learn.java.stream;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hao hao
 * @date : 2022/11/28
 **/
@Slf4j
public class CreatingStream {
    public static <T> void show(String title, Stream<T> stream) {
        final int SIZE = 10;
        List<T> firstTenElement = stream.limit(SIZE + 1).collect(Collectors.toList());
        for (T t : firstTenElement) {
            System.out.print(t);

            if (firstTenElement.lastIndexOf(t) == firstTenElement.size() - 1) {

                System.out.println("...");
            } else {
                System.out.print(",");
            }

        }
    }
}
