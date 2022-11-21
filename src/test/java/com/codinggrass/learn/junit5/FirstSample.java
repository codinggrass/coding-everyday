package com.codinggrass.learn.junit5;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class FirstSample {

    @Test
    void init_junit5_jar() {
        log.info("开始使用junit5");

        assertEquals(10, 2 * 5);
    }
}
