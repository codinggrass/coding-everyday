package com.codinggrass.learn.java.exception;

import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author hao hao
 * @date : 2023/2/4
 **/
@Slf4j
public class LearnExceptionTest {
    @Test
    void list_exceptions() {
        String t = Files.getFileExtension("C:\\abc.txt");
        log.info("{}", t);
    }


}