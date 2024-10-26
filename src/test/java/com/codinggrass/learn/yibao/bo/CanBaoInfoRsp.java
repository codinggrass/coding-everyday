package com.codinggrass.learn.yibao.bo;

import lombok.Data;

@Data
public class CanBaoInfoRsp {
    String code; //  0,
    String type; //  success
    String message; //  成功
    CanBaoInfo data; //  成功
}
