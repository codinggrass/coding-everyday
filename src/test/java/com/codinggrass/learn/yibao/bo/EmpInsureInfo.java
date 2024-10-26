package com.codinggrass.learn.yibao.bo;

import lombok.Data;

import java.util.List;

/**
 * 企业单位医保信息
 */
@Data
public class EmpInsureInfo {

    String empInsuRltsId;  // 00000000411328250663 
    String empNo;  // 41132800000000360896 
    String empType; // null,
    String insutype;  // 390 
    String hiType; // null,
    String maxAcctprd;  //null,
    String clctRuleTypeCodg; //  411300520200390101 
    String clctWay;  // 02 
    String clctstdCrtfRuleCodg;  // 411300520200390201 
    String empInsuStas;  // 2 
    String empInsuDate;  // 2013-09-25 00:00:00 
    String memo; // null,
    String insuAdmdvs;  // 411328 
    String poolareaNo;  // 411300 
    String taxBegnClctYm;  //null,
    String crterId;  // -- 
    String crterName;  // 居民转入 
    String crteOptinsNo;  // -- 
    String crteTime;  // 2021-11-23 21:17:08 
    String updtTime;  // 2024-04-07 16:08:20 
    String optinsNo;  // -- 
    String optTime;  // 2021-11-23 21:17:08 
    String opterName;  // 居民转入 
    String opterId;  // -- 
    String rid;  // 411328202111232117203589709229 
    String insuEmpMgtEid;  // 411328254943 
    String optChnl; // null,
    String beHospitalizedValue; // null,
    List<Rule> clcRuleList;
    List<Rule> clctStdRuleList;
    String empMgtType;  //null,
    String empName;  //null
}
