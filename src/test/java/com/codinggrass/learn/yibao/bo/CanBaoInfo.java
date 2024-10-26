package com.codinggrass.learn.yibao.bo;

import lombok.Data;

import java.util.List;

@Data
public class CanBaoInfo {
    PsnInfo psnInfo;
    List<PsnInsuInfoFromPic> psnInsuInfoFromPic;
    List<BankInfo> bankInfo;
    List<PsnInsuExtInfo> psnInsuExtInfo;
    String othSfInsuMsg;

    List<PsnInsuInfoFromUsc> psnInsuInfoFromUsc;
    String message;//该人员可以进行城乡居民参保登记！"
}
