package com.codinggrass.learn.yibao;

import lombok.Data;

@Data
public class PsnByConditionReqBo {
    String psnCertType;
    String certno;
    String servMattNo;
    String _modulePartId_;
    String frontUrl;
    String searchInsuEmp;

    public static PsnByConditionReqBo giveMeDefaultBo() {
        PsnByConditionReqBo psnByConditionReqBo = new PsnByConditionReqBo();
        psnByConditionReqBo.setFrontUrl("http://10.85.200.97/mbs-gg-ui/N1412-new.html#/N141201-new");
        psnByConditionReqBo.setCertno("411328201708100308");
        psnByConditionReqBo.setPsnCertType("01");
        psnByConditionReqBo.setServMattNo("N41201-gg");
        psnByConditionReqBo.set_modulePartId_("");
        psnByConditionReqBo.setSearchInsuEmp("true");
        return psnByConditionReqBo;
    }
}
