package com.codinggrass.learn.java.cache.guava;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author hao hao
 * @date : 2023/1/13
 **/
@Slf4j
public class LearnGuava {
    public static void main(String[] args) throws NoSuchMethodException, InterruptedException {
        Cache<String, String> cache = CacheBuilder.newBuilder().build();
        cache.put("hello", "Google Guava Cache");
        log.info("{}", cache.getIfPresent("hello"));

        Cache<Method, String> cacheMapMap = CacheBuilder.newBuilder().build();
        Method main = LearnGuava.class.getDeclaredMethod("main", String[].class);
        cacheMapMap.put(main, "method 1");
        log.info("{}", cacheMapMap.getIfPresent(main));

        //可以设置最大记录数，超过最大记录数时，将之前的记录删除。
        Cache<Integer, String> cacheMaxNumber = CacheBuilder.newBuilder()
                .maximumSize(2).build();
        cacheMaxNumber.put(3, "3");
        cacheMaxNumber.put(4, "4");
        cacheMaxNumber.put(1, "1");
        cacheMaxNumber.put(2, "2");
        log.info("cacheMaxNumber {}", cacheMaxNumber.getIfPresent(1));
        log.info("cacheMaxNumber {}", cacheMaxNumber.getIfPresent(2));
        log.info("cacheMaxNumber {}", cacheMaxNumber.getIfPresent(3));
        log.info("cacheMaxNumber {}", cacheMaxNumber.getIfPresent(4));

        //可以设置最长保留时间
        Cache<String, String> cacheAfterWhrite = CacheBuilder.newBuilder().maximumSize(4).expireAfterWrite(3, TimeUnit.SECONDS).build();
        cacheAfterWhrite.put("1", "1");
        int times = 5;
        while (times-- != 0) {
            log.info("{}", cacheAfterWhrite.getIfPresent("1"));
            Thread.sleep(1000);
        }

        //可以设置最长保留时间
        Cache<String, String> cacheAfterAccess = CacheBuilder.newBuilder().maximumSize(4).expireAfterAccess(3, TimeUnit.SECONDS).build();
        cacheAfterAccess.put("1", "1");
        log.info("{}", cacheAfterAccess.getIfPresent("1"));
        Thread.sleep(3000);
        log.info("{}", cacheAfterAccess.getIfPresent("1"));

        //设置缓存被移除时的监听事件
        //TODO 测试下来，过期失效的不会触发监听事件
        RemovalListener<String, String> removalListener = notification -> {
            log.info("已移除 key:{} value:{}", notification.getKey(), notification.getValue());
        };
        Cache<String, String> listenerCache = CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.SECONDS).removalListener(removalListener).build();
        listenerCache.put("1", "上海");
        listenerCache.put("2", "深圳");
        Thread.sleep(3000);
        log.info("测试有移除实体监听事件的缓存-start");
        log.info("首先访问1，然后sleep 3秒 ,{}", listenerCache.getIfPresent("1"));
        log.info("访问2，然后sleep 3秒 ,{}", listenerCache.getIfPresent("2"));
        log.info("测试有移除实体监听事件的缓存-end");
        log.info("{} {}", listenerCache.getIfPresent("1"), listenerCache.getIfPresent("2"));


        Cache<String, String> maxNumberRemoveListenCache = CacheBuilder.newBuilder().maximumSize(2).removalListener(removalListener).build();
        maxNumberRemoveListenCache.put("1", "上海");
        maxNumberRemoveListenCache.put("2", "深圳");
        maxNumberRemoveListenCache.put("3", "郑州");
        maxNumberRemoveListenCache.put("4", "官洲");
    }
}
