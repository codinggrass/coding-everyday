package com.codinggrass.learn.java.concurrent.disruptor;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * @author hao hao
 * @Date : 2022/12/3
 **/
@Slf4j
public class MyEventMain {
    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        for (int i = 0; i < 8; i++) {
//            byteBuffer.putChar(0, (char) i);
            byteBuffer.putLong(0, i);
        }

        log.info("{},{},{}", byteBuffer.getLong(0), byteBuffer.getLong(0), byteBuffer.getLong(0));
    }
}
