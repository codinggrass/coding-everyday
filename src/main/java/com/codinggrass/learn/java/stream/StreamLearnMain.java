package com.codinggrass.learn.java.stream;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * 做什么而非怎么做
 *
 * @author hao hao
 * @date : 2022/11/27
 **/
@Slf4j
public class StreamLearnMain {
    public static void main(String[] args) throws IOException {
        List<String> words = extractWordsList();
        countLongWordsUseFor(words);
        countLongWordsUseStream(words);
        countLongWordsUseParallelStream(words);
    }

    private static void countLongWordsUseParallelStream(List<String> words) {
        long count = words.parallelStream()
                .filter(word -> word.length() > 8)
                .count();
        log.info("words counts in parallel stream method {}", count);
    }

    private static void countLongWordsUseStream(List<String> words) {

        // stram.filter是保留符合条件的数据
        long count = words.stream().filter(word -> word.length() > 10).count();
        CreatingStream.show("countLongWordsUseStream", words.stream().filter(word -> word.length() > 10));
        log.info("words counts in stream method {}", count);
    }

    private static void countLongWordsUseFor(List<String> words) {

        long count = 0;
        for (String word : words) {
            if (word.length() > 8) {
                count++;
            }
        }
        log.info("long words counts use for :{}", count);
    }

    private static List<String> extractWordsList() throws IOException {
        // 读取一本书中的所有长单词进行计数
        String contents = new String(Files.readAllBytes(
                Paths.get("E:\\workspace\\coding-everyday\\src\\main\\resources\\bookdemo.txt")), StandardCharsets.UTF_8);
        log.info("contents book: {}", contents);

        //TODO 查PL做的作用
        return Arrays.asList(contents.split("\\PL+"));
    }


}
