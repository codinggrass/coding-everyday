package com.codinggrass.learn.disruptor;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author hao hao
 * @date : 2022/12/10
 **/
@Data
@ToString
@NoArgsConstructor
public class StringEvent {
    private String value;
}
