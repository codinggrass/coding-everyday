package com.codinggrass.learn.yibao;

import com.codinggrass.learn.yibao.bo.Rule;
import lombok.Data;

import java.util.List;

/**
 * @author hao hao
 * @date : 2024/10/22
 **/
@Data
public class PsnInsuInfoRspBo {

    final String psnInsuMgtEid;
    final String psnInsuRltsId;
    final String empNo;
    final String empNoList;
    final String psnNo;
    final String hiType;
    final String insutype;
    final String psnInsuStas;
    final String acctCrtnYm;
    final String fstInsuYm;
    final String crtInsuDate;
    final String psnInsuDate;
    final String pausInsuDate;
    final String insutypeRetrFlag;
    final String qutsType;
    final String clctWay;
    final String empFom;
    final String maxAcctprd;
    final String clctRuleTypeCodg;
    final String clctstdCrtfRuleCodg;
    final String insuAdmdvs;
    final String optinsNo;
    final String optTime;
    final String opterName;
    final String opterId;
    final String poolareaNo;
    final String crteTime;
    final String rid;
    final String updtTime;
    final String psnType;
    final String crterId;
    final String crterName;
    final String crteOptinsNo;
    final String optChnl;
    final String retrTrtBegnDate;
    final String retrTrtEnjymntFlag;
    final String empName;
    final String empType;
    final String empMgtType;
    final List<Rule> clcRuleList;
    final List<Rule> clctStdRuleList;
}
