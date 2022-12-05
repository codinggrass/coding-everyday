package com.codinggrass.learn.date;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Locale;

/**
 * @author hao hao
 * @date : 2022/12/5
 **/
@Slf4j
public class SimpleDateFormaterTest {
    @Test
    public void firstDateFormater() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Instant instant = Instant.now();
        java.util.Date from = Date.from(instant);
        DateFormat dateInstance = DateFormat.getDateInstance();
        log.info("DateFormat.getDateInstance {}", dateInstance.toString());

        log.info("from Date {}", from);
//        "Mon Dec 05 21:45:02 CST 2022";
//
        String format = simpleDateFormat.format(from);
        log.info("format {}", format);
        java.util.Date parse = simpleDateFormat.parse("2022-02-03");
        log.info("parse {}", parse);

        log.info("{}", Locale.getDefault(Locale.Category.FORMAT).toString());

    }
}
