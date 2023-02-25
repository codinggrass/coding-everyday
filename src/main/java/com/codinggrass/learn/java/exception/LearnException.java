package com.codinggrass.learn.java.exception;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * @author hao hao
 * @date : 2023/2/4
 **/
@Slf4j
public class LearnException {

    /**
     * Java规定：
     * 必须捕获的异常，包括Exception及其子类，但不包括RuntimeException及其子类，这种类型的异常称为Checked Exception。
     * 不需要捕获的异常，包括Error及其子类，RuntimeException及其子类。
     *
     * @param args
     */
    public static void main(String[] args) {
//        String abc = "abc";
//        Integer integer = Integer.valueOf(abc);

        /**
         *
         *                      ┌───────────┐
         *                      │  Object   │
         *                      └───────────┘
         *                            ▲
         *                            │
         *                      ┌───────────┐
         *                      │ Throwable │
         *                      └───────────┘
         *                            ▲
         *                  ┌─────────┴─────────┐
         *                  │                   │
         *            ┌───────────┐       ┌───────────┐
         *            │   Error   │       │ Exception │
         *            └───────────┘       └───────────┘
         *                  ▲                   ▲
         *          ┌───────┘              ┌────┴──────────┐
         *          │                      │               │
         * ┌─────────────────┐    ┌─────────────────┐┌───────────┐
         * │OutOfMemoryError │... │RuntimeException ││IOException│...
         * └─────────────────┘    └─────────────────┘└───────────┘
         *                                 ▲
         *                     ┌───────────┴─────────────┐
         *                     │                         │
         *          ┌─────────────────────┐ ┌─────────────────────────┐
         *          │NullPointerException │ │IllegalArgumentException │...
         *          └─────────────────────┘ └─────────────────────────┘
         */

        byte[] bytes = toGBK("世界有那么多人");
        log.info("{}", bytes);
        log.info("{}", Arrays.toString(bytes));
    }

    private static byte[] toGBK(String string) {
        try {
            return string.getBytes("GBK1");
        } catch (UnsupportedEncodingException e) {
            // IOException 是受检异常，如果不catch会编译报错
            log.warn(e.getMessage(), e);
            e.printStackTrace();
            return string.getBytes();
        }
    }
}
