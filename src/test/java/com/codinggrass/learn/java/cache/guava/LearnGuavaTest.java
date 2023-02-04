package com.codinggrass.learn.java.cache.guava;


import com.codinggrass.learn.java.concurrent.ballsimple.Ball;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.util.RamUsageEstimator;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author hao hao
 * @date : 2023/2/4
 **/
@Slf4j
public class LearnGuavaTest {

    @Test
    void test_caulate_cache_ram_size() {
        Cache<String, Object> cache = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .recordStats()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();

        log.info("Single object size {}", RamUsageEstimator.humanSizeOf(new Ball()));
        for (int i = 0; i < 1000; i++) {
            cache.put(String.valueOf(i), new Ball());
            log.info("ram size {}", RamUsageEstimator.humanSizeOf(cache));
        }
        log.info("++++++++++++++++++++++++++++");
        for (int i = 0; i < 100; i++) {
            cache.getIfPresent(String.valueOf(i));
            log.info("ram size {}", RamUsageEstimator.humanSizeOf(cache));
        }
    }

}