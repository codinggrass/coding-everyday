package com.codinggrass.learn.junit5;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.platform.commons.util.StringUtils;
import sun.util.calendar.BaseCalendar;

import java.io.PrintStream;
import java.sql.Date;
import java.time.Instant;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

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
        Properties properties = getOneProperties();

        Enumeration<?> enumeration = properties.propertyNames();
        Object element = enumeration.nextElement();
        assertEquals(properties.getProperty((String) element), "code");

        assertEquals(properties.getOrDefault("date", Date.from(Instant.now()).toString()), "11-21");
    }

    private Properties getOneProperties() {
        Properties properties = new Properties();
        properties.setProperty("name", "hao");
        properties.setProperty("date", "11-21");
        properties.setProperty("purpose", "code");
        return properties;
    }

    @Test
    void testOptional() {
        Properties oneProperties = getOneProperties();
        assertNotNull(Optional.ofNullable(oneProperties.getProperty("name")));
        assertNull(null);
        Optional<Object> empty = Optional.empty();
        log.info(Optional.ofNullable(oneProperties.getProperty("name")).toString());
        log.info(Optional.ofNullable(oneProperties.getProperty("name")).map(String::toUpperCase).toString());
        log.info(Optional.ofNullable(oneProperties.getProperty("time")).map(String::toUpperCase).toString());
        log.info(Optional.ofNullable(oneProperties.getProperty("name")).get());
        log.info("{}", Optional.ofNullable(oneProperties.getProperty("time")).orElse(null));
        assertThrows(NoSuchElementException.class, () -> Optional.ofNullable(oneProperties.getProperty("time")).get());
        ;
    }

    @Test
    void testBoolean() {
        /*parseBoolean 不为“true”都为false*/
        assertFalse(Boolean.parseBoolean("false"));
        assertTrue(Boolean.parseBoolean("true"));
        assertTrue(Boolean.parseBoolean("TRUE"));
        assertTrue(Boolean.parseBoolean("TRuE"));
        assertFalse(Boolean.parseBoolean("0"));
        assertFalse(Boolean.parseBoolean("1"));
        assertFalse(Boolean.parseBoolean("123"));
        assertFalse(Boolean.parseBoolean("abc"));
    }

    @Test
    void testOptionalElseGet() {
        Optional<Object> optionalBool = Optional.empty();
        assertNotNull(optionalBool.orElseGet(() -> "A"));
        Object a = Optional.ofNullable(null).orElse("a");
        log.info("{} {}", a.getClass(), a);
    }

    @Test
    void testLeftAndRightMove() {
        int initValue = 1024;
        // 1024/2
        int value = initValue >> 1;
        assertEquals(512, value);
        // 512/2
        assertEquals(512 / 2, value >> 1);
        // 1024 * 4
        assertEquals(initValue * 4, initValue << 2);
        // 1024 /
        log.info("{} {}", Integer.toHexString(initValue), initValue >> 10);
    }

    @Test
    void testProcessNumber() {
        int number = Runtime.getRuntime().availableProcessors();
        log.info("current process number: {}", number);
        log.info("current free memory: {} byte", Runtime.getRuntime().freeMemory());
    }
}
