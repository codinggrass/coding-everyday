package com.codinggrass.learn.junit5;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import sun.util.calendar.BaseCalendar;

import java.io.PrintStream;
import java.sql.Date;
import java.time.Instant;
import java.util.Enumeration;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

class FirstSample {

    @Test
    void init_junit5_jar() {
        log.info("开始使用junit5");
        assertEquals(10, 2 * 5);
    }

    @Test
    @Order(2)
    void testMethodOrderIs2() {
        log.info("order test method is 2");
    }

    @Test
    void testPropertyUse() {
        Properties properties = new Properties();
        properties.setProperty("name", "hao");
        properties.setProperty("date", "11-21");
        properties.setProperty("purpose", "code");

        Enumeration<?> enumeration = properties.propertyNames();
        Object element = enumeration.nextElement();
        assertEquals(properties.getProperty((String) element), "code");

        assertEquals(properties.getOrDefault("date", Date.from(Instant.now()).toString()),"11-21");
    }


}
