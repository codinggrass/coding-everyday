package com.codinggrass.learn.java.cache.guava;

import com.google.common.cache.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author hao hao
 * @date : 2023/1/13
 **/
@Slf4j
public class LearnGuava {
    public static void main(String[] args) throws NoSuchMethodException, InterruptedException, ExecutionException {
        //LoadingCache创建时需要指定一个CacheLoader覆写刷新机制
        LoadingCache<String, String> cacheLoader = CacheBuilder.newBuilder().maximumSize(2).recordStats().build(new CacheLoader<String, String>() {
            @Override
            public String load(String key) throws Exception {
                log.info("begin load value from CacheLoader");
                Thread.sleep(1000);
                return key + "value from cache loader";
            }
        });
        log.info("{}", cacheLoader.get("1"));
        log.info("{}", cacheLoader.get("2"));
        log.info("{}", cacheLoader.get("3"));
        extracted();
    }

    private static void extracted() throws NoSuchMethodException, InterruptedException {
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


        Cache<String, String> maxNumberRemoveListenCache = CacheBuilder.newBuilder().recordStats().maximumSize(2).removalListener(removalListener).build();
        maxNumberRemoveListenCache.put("1", "上海");
        maxNumberRemoveListenCache.put("2", "深圳");
        maxNumberRemoveListenCache.put("3", "郑州");
        maxNumberRemoveListenCache.put("4", "官洲");

        //get方法有两个参数，key,Callable ,当key值找不到value时，调用Callabe线程方法获取返回值。
        new Thread(new Runnable() {

            @Override
            public void run() {
                log.info("thread 1 begin");
                try {
                    log.info("get key value :{}",
                            maxNumberRemoveListenCache.get("key", (Callable<String>) () -> {
                                log.info("begin load value in thread 1");
                                Thread.sleep(1000);
                                return "auto load values 1";
                            }));
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        // 如果不同线程使用相同cache加载未存在的key时，多个线程中按顺序只有第一个Calleable会被执行，执行后返回值在其他线程中也可使用。
        new Thread(() -> {
            log.info("thread 2 begin");
            try {
                log.info("get key value :{}", maxNumberRemoveListenCache.get("key", () -> {
                    log.info("begin load value in thread 2");
                    Thread.sleep(1000);
                    return "auto load values 2";
                }));
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).start();
        Thread.sleep(2000);

        //TODO 查看统计信息
        log.info("{}", maxNumberRemoveListenCache.stats());
        maxNumberRemoveListenCache.invalidateAll();
        log.info("{}", maxNumberRemoveListenCache.size());

        maxNumberRemoveListenCache.put("1", "2");
        log.info("{}", maxNumberRemoveListenCache.size());
        maxNumberRemoveListenCache.put("3", "2");
        maxNumberRemoveListenCache.put("2", "2");
        maxNumberRemoveListenCache.cleanUp();
        log.info("{}", maxNumberRemoveListenCache.size());
        log.info("{}", maxNumberRemoveListenCache.getIfPresent(2));
        log.info("{}", maxNumberRemoveListenCache.size());
    }
}
