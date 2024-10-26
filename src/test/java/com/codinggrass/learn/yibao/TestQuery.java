package com.codinggrass.learn.yibao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codinggrass.learn.yibao.bo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author hao hao
 * @date : 2024/10/22
 **/
@Slf4j
public class TestQuery {
    public static final String GET_PSN_INSU_INFO = "http://10.85.200.97/mbs-gg/web/psnCommquery/getPsnInsuInfo";
    public static final String GET_EMP_BASIC_INFO = "http://10.85.200.97/mbs-gg/empComplexQueryController/getEmpBasicInfo";
    public static final String SAVE_CAN_BAO = "http://10.85.200.97/mbs-gg/web/rsdtInsuReg/saveJingBan";
    public static final String GET_PERSON_CANBAO_INFO = "http://10.85.200.97/mbs-gg/web/rsdtInsuReg/queryPersonByCardNo";
    public static final String SAVE_PAUSE_CLCT = "http://10.85.200.97/mbs-gg/web/urrPausClct/savePauseClct";
    public static final String GET_EMP_INSU_INFO = "http://10.85.200.97/mbs-gg/web/rsdtInsuReg/queryEmpInsureByID";
    public static final String get_PsnByCondition = "http://10.85.200.97/mbs-gg/web/commonquery/getPsnByCondition";
    public static final String COOKIE = "XSRF-TOKEN=7f7c6315-b0f0-45e2-a5f5-71ad91911f7e; SESSION=ODZkZjkwYmQtNDNmNC00NTlhLWI2ZDgtNDYwZGE0YjUzNzJm";
    public static final String TOKEN = "7f7c6315-b0f0-45e2-a5f5-71ad91911f7e";
    public static final String URR_INSU_INFO_SINGLE = "http://10.85.200.97/mbs-gg/web/urrPausClct/queryUrrInsuInfoSingle";

    private CloseableHttpClient httpClient = HttpClientBuilder.create().build();


    @Test
    // 开始参保，通过参保-输入.txt，循环执行参保
    public void canbao() throws IOException {
        String empNo = "41132800000000360896";
        readFileLines("参保-输入.txt").stream().forEach((certno) -> {
            try {
                saveCanBaoInfoByEmpNoAndCertNo(empNo, certno);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    //参保前检查
    @Test
    public void checkBeforCanbao() throws IOException {
        readFileLines("登记医保-检查-输入.txt").stream().forEach((certno) -> {
            try {
                //单位编号
                String empNo = "41132800000000360896";
                CanBaoInfoRsp canBaoInfoRsp = queryPersonCanBaoInfo(empNo, certno);
                if (canBaoInfoRsp != null) {
                    if (StringUtils.isNotBlank(canBaoInfoRsp.getData().getOthSfInsuMsg())) {
                        log.info("异常参保人员 {}  信息：{}", certno, canBaoInfoRsp.getData().getOthSfInsuMsg());
                        fileWrite("登记医保-检查-输出异常医保信息.txt", certno + "," + canBaoInfoRsp.getData().getOthSfInsuMsg(), true);
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }


    @Test
    // 根据身份证列表查询，是否已暂停参保
    public void queryPausePersion() throws IOException {
        readFileLines("检查暂停状态-输入.txt").stream().forEach((linesCards) -> {
            try {
                GetPsnByConditionRspBo getPsnByConditionRspBo = queryPersonInfoByCertno(linesCards);
                if (getPsnByConditionRspBo == null) {
                    fileWrite("检查暂停状态-未找人人员或者人员重复.txt", linesCards, true);
                    return;
                }
                if (getPsnByConditionRspBo.getPsnInsuStas().equals("2")) {
                    log.info("人员：{}, 目前是暂停状态", getPsnByConditionRspBo.getCertno());
                    fileWrite("检查暂停状态-输出结果.txt", getPsnByConditionRspBo.getCertno() + " " + getPsnByConditionRspBo.getEmpName(), true);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    //第一步查询条件
    @Test
    @Deprecated
    public void queryPersionCondition() throws IOException {
        String empNo = "41132800000000360896";
        String certno = "411328200912275522";

        GetPsnByConditionRspBo personInfo = queryPersonInfoByCertno(certno);

        // 第一步：检查不包含文峰，且医保状态正常；
        boolean isNotWenFeng = isSelfEmpNo(personInfo, empNo);
        if (!isNotWenFeng) {
            log.error("第一步：检查不包含文峰，且医保状态异常！");
            throw new RuntimeException("第一步：检查不包含文峰，且医保状态异常");
        }

    }

    @Test
    //第二步暂停参保
    public void testPauseInsu() throws IOException {

        PsnByConditionReqBo psnByConditionReqBo = PsnByConditionReqBo.giveMeDefaultBo();
        psnByConditionReqBo.setCertno("411328201009159423");
        String operaEmpNo = "41132800000000360896";
//        单位编号	计数
//        41132800000000360896		唐河县文峰
//        41132800000000361008	139	唐河县文峰郭庄
//        41132800000000361033	4465	唐河县文峰焦庄
//        41132800000000361354		唐河县文峰居民2
//        41132800000000361129	1583	唐河县文峰四里桥
//        41132800000000361289	2148	唐河县文峰文峰居民
//        41132800000000360909	3758	唐河县文峰新春
//        41132800000000361513	1438	唐河县文峰新晖社区
//        41132800000000361128	2731	唐河县文峰徐庄

        savePause(queryPersonInfo(psnByConditionReqBo), operaEmpNo);
    }

    @Test
    // step3- 校验人员是否有参保权限
    public void testQueryPersonCanBaoInfo() throws IOException {
        String empNo = "41132800000000360896";
        String certno = "411325198607264115";
        CanBaoInfoRsp canBaoInfoRsp = queryPersonCanBaoInfo(empNo, certno);
        if (StringUtils.isNotBlank(canBaoInfoRsp.getData().getOthSfInsuMsg())) {
            log.info("异常参保人员 {}  信息：{}", certno, canBaoInfoRsp.getData().getOthSfInsuMsg());
        }
    }

    //step3- 重新参保
    @Test
    public void saveCanBaoInfo() throws IOException {
        String empNo = "41132800000000360896";
        String certno = "411328201009159423";
        saveCanBaoInfoByEmpNoAndCertNo(empNo, certno);
    }


    @Test
    public void oneKeyDoneAll() throws IOException {
        //单位编号
        String selfEmpNo = "41132800000000361008";

        //先清理文件：
        fileWrite("一键修改-查询人员信息-异常.txt", "", false);

        fileWrite("人员重复列表.txt", "", false);
        fileWrite("一键参保-检查-异常信息.txt", "", false);
        fileWrite("一键参保-暂停参保-异常流水号.txt", "", false);
        fileWrite("一键参保-暂停参保-暂停医保成功记录.txt", "", false);
        fileWrite("登记医保-检查-存在异常流水号.txt", "", false);
        fileWrite("登记医保-检查-符合参保条件记录.txt", "", false);
        fileWrite("登记医保-检查-参保条件不符合异常记录.txt", "", false);
        fileWrite("一键参保-最终修改成功记录.txt", "", false);
        fileWrite("一键参保-参保成功但检查失败记录.txt", "", false);
        List<String> cardList = readFileLines("一键修改-输入.txt");
        for (String certno : cardList) {
            GetPsnByConditionRspBo personInfo = null;
            personInfo = queryPersonInfoByCertno(certno);

            if (personInfo == null) {
                fileWrite("一键修改-查询人员信息-异常.txt", certno, true);
                continue;
            }
            // 第一步：检查非本单位编号，且医保状态正常；
            boolean isSelfEmpNo = isSelfEmpNo(personInfo, selfEmpNo);
            boolean isStatusNormal = "1".equals(personInfo.getPsnInsuStas());
            if (isSelfEmpNo) {
                fileWrite("一键参保-检查-异常信息.txt", "'" + certno + " : 原因：本单位是" + selfEmpNo, true);
                log.info("{} 异常原因：本单位", certno);
                continue;
            }

            if ((!isStatusNormal)) {
                fileWrite("一键参保-检查-异常信息.txt", "'" + certno + " : 原因：医保状态异常", true);
                log.info("{} 异常原因：医保状态异常", certno);
                continue;
            }
            log.info("{} 医保状态正常", certno);


            //第二步：暂停前查询是否在住院
            if (queryIsHostipalByCerno(certno)) {
                fileWrite("一键参保-检查-异常信息.txt", "'" + certno + " : 原因：存在住院信息", true);
                log.info("{} 异常原因：存在住院信息", certno);
                continue;
            }

            //第三步：暂停
            SavePauseRsp savePauseRsp = savePauseByCertnoAndEmpNo(certno, selfEmpNo);
            if (savePauseRsp == null) {
                log.info("{} 暂停参保存在异常", certno);
                continue;
            }

            //第四步：检查是否有参保资格
            CanBaoInfoRsp canBaoInfoRsp = queryPersonCanBaoInfo(selfEmpNo, certno);
            if (canBaoInfoRsp == null) {
                log.info("{} 参保前检查存在异常", certno);
                continue;
            }
            if (canBaoInfoRsp.getType() == null) {
                log.info("{} 参保前检查存在异常", certno);
                continue;
            }

            if (!canBaoInfoRsp.getType().equals("success")) {
                log.info("{} 参保前检查存在异常", certno);
                continue;
            }

            if (canBaoInfoRsp.getData() == null) {
                log.info("{} 参保前检查存在异常", certno);
                continue;
            }
            if (canBaoInfoRsp.getData().getMessage() == null) {
                log.info("{} 参保前检查存在异常", certno);
                continue;
            }
            if (!canBaoInfoRsp.getData().getMessage().contains("该人员可以进行城乡居民参保登记")) {
                log.info("{} 参保前检查存在异常", certno);
                continue;
            }

            //第五步 执行参保
            CanBaoInfoRsp canBaoInfoRsp1 = saveCanBaoInfoByEmpNoAndCertNo(selfEmpNo, certno);
            if (canBaoInfoRsp1 == null) {
                log.info("{} 执行参保存在异常", certno);
                continue;
            }
            if (canBaoInfoRsp1.getCode() != null && canBaoInfoRsp1.getCode().equals("0") && canBaoInfoRsp1.getMessage() != null
                    && canBaoInfoRsp1.getMessage().contains("参保成功")) {
                //第六步 检查参保是否成功
                GetPsnByConditionRspBo lastPersionInfo = queryPersonInfoByCertno(certno);
                if (lastPersionInfo.getEmpNo().equals(selfEmpNo) && lastPersionInfo.getPsnInsuStas().equals("1")) {
                    log.info("一键参保成功：{}", certno);
                    fileWrite("一键参保-最终修改成功记录.txt",  certno, true);
                } else {

                    fileWrite("一键参保-参保成功但检查失败记录.txt", "'" + certno, true);
                }
            }

            log.info("一键参保结束！！！");

        }


//        if (!isSelfEmpNo) {
//            log.error("第一步：检查不包含文峰，且医保状态异常！");
//            throw new RuntimeException("第一步：检查不包含文峰，且医保状态异常");
//        }
//        // 第二步：进行暂停
//        if (isSelfEmpNo) {
//            SavePauseRsp savePauseRsp = savePauseByCertnoAndEmpNo(certno, selfEmpNo);
//            boolean isPauseSucess = savePauseRsp.getType().equals("success") && savePauseRsp.getMessage().contains("参保人员暂停缴费经办成功");
//            if (isPauseSucess) {
//                log.info("第二步：进行暂停医保成功.人员：{} 单位：{}", certno, selfEmpNo);
//            } else {
//                throw new RuntimeException("第二步：进行暂停医保失败");
//            }
//        }
//        // 第三步：检查是否具备重新医保登记条件，如果具备则登记
//        String rsp = queryPersonCanBaoInfoByCertno(selfEmpNo, certno);
//        if (rsp.contains("无法进行城乡居民参保") || rsp.contains("异常流水号")) {
//            log.info("第三步：不具备重新医保登记条件.人员：{} 单位：{}", certno, selfEmpNo);
//            throw new RuntimeException("第三步：不具备重新医保登记条件");
//        }
//        if (rsp.contains("success") && rsp.contains("成功") && rsp.contains("该人员可以进行城乡居民参保登记")) {
//            log.info("第三步：具备重新医保登记条件 人员：{} {} ", certno, selfEmpNo);
//        }
//        saveCanBaoInfoByEmpNoAndCertNo(selfEmpNo, certno);
//
//        // 第四步：检查是否已经更改医保单位为文峰，且医保状态为正常
//        if (isWenFeng(certno)) {
//            log.info("结束，coffee来一杯！");
//        }


    }

    @Test
    public void isHospital() throws IOException {
        String certno = "411328201109280052";
        org.junit.Assert.assertTrue(queryIsHostipalByCerno(certno));
        org.junit.Assert.assertFalse(queryIsHostipalByCerno("411328201708100308"));

    }

    private boolean queryIsHostipalByCerno(String certno) throws IOException {
        GetPsnByConditionRspBo psnByConditionRsp = queryPersonInfoByCertno(certno);
        return queryIsHospital(psnByConditionRsp.getPsnNo(), psnByConditionRsp.getInsuAdmdvs());
    }

    private boolean queryIsHospital(String psnNo, String insuAdmdvs) throws IOException {
        final HttpPost httpPost = new HttpPost(URR_INSU_INFO_SINGLE);
        setHeaders(httpPost);
        List<BasicNameValuePair> basicNameValuePairs = new ArrayList<>();
        basicNameValuePairs.add(new BasicNameValuePair("frontUrl", "http://10.85.200.97/mbs-gg-ui/N0104.html#/N010406"));
        basicNameValuePairs.add(new BasicNameValuePair("psnNo", psnNo));
        basicNameValuePairs.add(new BasicNameValuePair("insuAdmdvs", insuAdmdvs));
        basicNameValuePairs.add(new BasicNameValuePair("_modulePartId_", ""));

        final UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(basicNameValuePairs, StandardCharsets.UTF_8);
        httpPost.setEntity(urlEncodedFormEntity);
        CloseableHttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (IOException e) {
            log.error("{}", e);
            throw new RuntimeException(e);
        }
        HttpEntity responseEntity = httpResponse.getEntity();
        String rspString = EntityUtils.toString(responseEntity);
        log.info("{}: {} responseEntity-toString:{}", URR_INSU_INFO_SINGLE, basicNameValuePairs, rspString);
        JSONObject jsonObject = JSON.parseObject(rspString);
        return jsonObject.getJSONObject("data").getJSONObject("data").getJSONObject("data").getBoolean("isHospital");

    }

    private boolean isWenFeng(String certno) throws IOException {
        GetPsnByConditionRspBo personInfo = queryPersonInfoByCertno(certno);
        log.info("{}, {}, {},{}", personInfo.getCertno(), personInfo.getPsnName(), personInfo.getEmpName(), personInfo.getPsnInsuStas());
        if (personInfo.getEmpName().contains("文峰")) {
            log.info("单位不包含文峰");
            if (personInfo.getPsnInsuStas().equals("1")) {
                log.info("参保状态：正常参保");
                return true;
            }
            return false;
        }
        return false;

    }

    private boolean isSelfEmpNo(GetPsnByConditionRspBo person, String selfEmpno) throws IOException {
        if (person.getEmpNo() != null) {
            if (person.getEmpNo().equals(selfEmpno)) {
                return true;
            }
            return false;
        }
        return false;

    }

    private CanBaoInfoRsp saveCanBaoInfoByEmpNoAndCertNo(String empNo, String certno) throws IOException {
        final HttpPost httpPost = new HttpPost(SAVE_CAN_BAO);
        setHeaders(httpPost);

        httpPost.setEntity(giveMeSavaCanBaoEntity(queryPersonCanBaoInfo(empNo, certno), empNo));
//        httpPost.setEntity(giveMeSavaCanBaoEntity(certno,empNo));
        CloseableHttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (IOException e) {
            log.error("{}", e);
            throw new RuntimeException(e);
        }
        HttpEntity responseEntity = httpResponse.getEntity();
        String rspString = EntityUtils.toString(responseEntity);
        log.info("登记医保 {} responseEntity-toString:{}", certno, rspString);
        fileWrite("登记医保-请求返回报文记录.txt", certno + empNo + ": " + rspString, true);
        JSONObject jsonObject = JSON.parseObject(rspString);
        if (rspString.contains("异常流水号")) {
            log.error("{} 异常", certno + " : " + rspString);
            fileWrite("参保-异常流水号记录.txt", certno + " : " + rspString, true);
            return null;
        }
        String wrapperResponse = jsonObject.getJSONObject("data").getString("wrapperResponse");

        CanBaoInfoRsp canBaoInfoRsp = JSON.parseObject(wrapperResponse, CanBaoInfoRsp.class);

        if (canBaoInfoRsp.getCode().equals("0") && canBaoInfoRsp.getMessage().contains("参保成功")) {
            log.info("参保成功：{} 在单位：{} 参保成功", certno, empNo);
            fileWrite("参保-参保成功记录.txt", "'" + certno, true);
        } else {
            log.info("参保失败：{} 在单位：{} 请见文件", certno, empNo);
            fileWrite("参保-参保失败记录.txt", certno + " : " + rspString, true);
        }


        fileWrite("参保登记-参保成功记录.txt", certno + " : " + rspString, true);
        return
                canBaoInfoRsp;
    }

    private HttpEntity giveMeSavaCanBaoEntity(CanBaoInfoRsp canBaoInfoRsp, String empNo) throws IOException {
        List<BasicNameValuePair> basicNameValuePairs = new ArrayList<>();
        basicNameValuePairs.add(new BasicNameValuePair("frontUrl", "http://10.85.200.97/mbs-gg-ui/N0104.html#/N010401"));
        basicNameValuePairs.add(new BasicNameValuePair("_modulePartId_", ""));
        basicNameValuePairs.add(new BasicNameValuePair("crtInsuDate", getCurrentDate()));

        //单位信息
        EmpInsureInfo empInsureInfo = queryEmpInsureInfo(empNo);
        if (empInsureInfo == null) {
            throw new RuntimeException("未查询到此单位信息");
        }
        EmpBasicInfo empBasicInfo = queryEmpBasicInfo(empNo);
        if (empBasicInfo == null) {
            throw new RuntimeException("未查询到此单位基础信息");
        }
        basicNameValuePairs.add(new BasicNameValuePair("empNo", empInsureInfo.getEmpNo()));
        basicNameValuePairs.add(new BasicNameValuePair("empEnttCodg", empBasicInfo.getEmpEnttCodg()));
        basicNameValuePairs.add(new BasicNameValuePair("empMgtType", empBasicInfo.getEmpMgtType()));
        basicNameValuePairs.add(new BasicNameValuePair("prntEmpNo", empBasicInfo.getPrntEmpNo()));
        basicNameValuePairs.add(new BasicNameValuePair("asocLegentFlag", empBasicInfo.getAsocLegentFlag()));
        basicNameValuePairs.add(new BasicNameValuePair("empType", empBasicInfo.getEmpType()));
        basicNameValuePairs.add(new BasicNameValuePair("empName", empBasicInfo.getEmpName()));
        basicNameValuePairs.add(new BasicNameValuePair("regName", empBasicInfo.getRegName()));
        basicNameValuePairs.add(new BasicNameValuePair("locAdmdvs", empBasicInfo.getLocAdmdvs()));
        basicNameValuePairs.add(new BasicNameValuePair("conerName", empBasicInfo.getConerName()));
        basicNameValuePairs.add(new BasicNameValuePair("conerEmail", empBasicInfo.getConerEmail()));
        basicNameValuePairs.add(new BasicNameValuePair("tel", empBasicInfo.getTel()));
        basicNameValuePairs.add(new BasicNameValuePair("faxNo", empBasicInfo.getFaxNo()));
        basicNameValuePairs.add(new BasicNameValuePair("taxNo", empBasicInfo.getTaxNo()));
        basicNameValuePairs.add(new BasicNameValuePair("orgcode", empBasicInfo.getOrgcode()));
        basicNameValuePairs.add(new BasicNameValuePair("regno", empBasicInfo.getRegno()));
        basicNameValuePairs.add(new BasicNameValuePair("regnoCertType", empBasicInfo.getRegnoCertType()));
        basicNameValuePairs.add(new BasicNameValuePair("empAddr", empBasicInfo.getEmpAddr()));
        basicNameValuePairs.add(new BasicNameValuePair("poscode", empBasicInfo.getPoscode()));
        basicNameValuePairs.add(new BasicNameValuePair("aprvEstaDept", empBasicInfo.getAprvEstaDept()));
        basicNameValuePairs.add(new BasicNameValuePair("aprvEstaDate", empBasicInfo.getAprvEstaDate()));
        basicNameValuePairs.add(new BasicNameValuePair("aprvEstaDocno", empBasicInfo.getAprvEstaDocno()));
        basicNameValuePairs.add(new BasicNameValuePair("prntAdmdvs", empBasicInfo.getPrntAdmdvs()));
        basicNameValuePairs.add(new BasicNameValuePair("insuAdmdvs", empBasicInfo.getInsuAdmdvs()));
        basicNameValuePairs.add(new BasicNameValuePair("orgValiStas", empBasicInfo.getOrgValiStas()));
        basicNameValuePairs.add(new BasicNameValuePair("legrepTel", empBasicInfo.getLegrepTel()));
        basicNameValuePairs.add(new BasicNameValuePair("legrepName", empBasicInfo.getLegrepName()));
        basicNameValuePairs.add(new BasicNameValuePair("legrepCertType", empBasicInfo.getLegrepCertType()));
        basicNameValuePairs.add(new BasicNameValuePair("legrepCertno", empBasicInfo.getLegrepCertno()));
        basicNameValuePairs.add(new BasicNameValuePair("orgcodeIssuEmp", empBasicInfo.getOrgcodeIssuEmp()));
        basicNameValuePairs.add(new BasicNameValuePair("valiFlag", empBasicInfo.getValiFlag()));
        basicNameValuePairs.add(new BasicNameValuePair("rid", empBasicInfo.getRid()));
        basicNameValuePairs.add(new BasicNameValuePair("crteTime", empBasicInfo.getCrteTime()));
        basicNameValuePairs.add(new BasicNameValuePair("updtTime", empBasicInfo.getUpdtTime()));
        basicNameValuePairs.add(new BasicNameValuePair("crterName", empBasicInfo.getCrterName()));
        basicNameValuePairs.add(new BasicNameValuePair("opterName", empBasicInfo.getOpterName()));
        basicNameValuePairs.add(new BasicNameValuePair("optTime", empBasicInfo.getOptTime()));
        basicNameValuePairs.add(new BasicNameValuePair("crterId", empBasicInfo.getCrterId()));
        basicNameValuePairs.add(new BasicNameValuePair("crteOptinsNo", empBasicInfo.getCrteOptinsNo()));
        basicNameValuePairs.add(new BasicNameValuePair("opterId", empBasicInfo.getOpterId()));
        basicNameValuePairs.add(new BasicNameValuePair("optinsNo", empBasicInfo.getOptinsNo()));
        basicNameValuePairs.add(new BasicNameValuePair("poolareaNo", empBasicInfo.getPoolareaNo()));
        basicNameValuePairs.add(new BasicNameValuePair("ver", empBasicInfo.getVer()));
        basicNameValuePairs.add(new BasicNameValuePair("syncPrntFlag", empBasicInfo.getSyncPrntFlag()));
        basicNameValuePairs.add(new BasicNameValuePair("servMattInstId", empBasicInfo.getServMattInstId()));
        basicNameValuePairs.add(new BasicNameValuePair("evtInstId", empBasicInfo.getEvtInstId()));
        basicNameValuePairs.add(new BasicNameValuePair("sysCode", empBasicInfo.getSysCode()));
        basicNameValuePairs.add(new BasicNameValuePair("entpSpecFlag", empBasicInfo.getEntpSpecFlag()));
        basicNameValuePairs.add(new BasicNameValuePair("memo", empBasicInfo.getMemo()));
        basicNameValuePairs.add(new BasicNameValuePair("taxEmpNo", empBasicInfo.getTaxEmpNo()));


        //人员信息
        List<PsnInfo> psnAllInfoDTOList = new ArrayList<>();
        PsnInfo psnInfo = canBaoInfoRsp.getData().getPsnInfo();
        psnAllInfoDTOList.add(psnInfo);
        basicNameValuePairs.add(new BasicNameValuePair("psnAllInfoDTOListStr", JSON.toJSONString(psnAllInfoDTOList)));

        //企业参保信息
        List<EmpInsureInfo> empInsuDDTOList = new ArrayList<>();
        empInsuDDTOList.add(empInsureInfo);
        basicNameValuePairs.add(new BasicNameValuePair("empInsuDDTOListStr", JSON.toJSONString(empInsuDDTOList)));


        final UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(basicNameValuePairs, StandardCharsets.UTF_8);
        return urlEncodedFormEntity;
    }

    private EmpBasicInfo queryEmpBasicInfo(String empNo) throws IOException {
        List<BasicNameValuePair> basicNameValuePairs = new ArrayList<>();
        basicNameValuePairs.add(new BasicNameValuePair("frontUrl", "http://10.85.200.97/mbs-gg-ui/N0104.html#/N010401"));
        basicNameValuePairs.add(new BasicNameValuePair("_modulePartId_", ""));
        basicNameValuePairs.add(new BasicNameValuePair("projectNo", ""));
        basicNameValuePairs.add(new BasicNameValuePair("taskId", ""));

        JSONObject reqInfo = new JSONObject();
        reqInfo.put("empNo", empNo);
        reqInfo.put("pageNo", 1);
        reqInfo.put("pageSize", 10);
        reqInfo.put("valiFlag", 1);
        log.info("{}", reqInfo.toJSONString());
        StringEntity stringEntity = new StringEntity(reqInfo.toJSONString(), "UTF-8");
        stringEntity.setContentType("application/json");
        stringEntity.setContentEncoding("UTF-8");
        HttpPost httpPost = new HttpPost(GET_EMP_BASIC_INFO);
        setHeaders(httpPost);
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
        httpPost.setEntity(stringEntity);

        CloseableHttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (IOException e) {
            log.error("{}", e);
            throw new RuntimeException(e);
        }
        HttpEntity responseEntity = httpResponse.getEntity();
        String rspString = EntityUtils.toString(responseEntity);
        log.info("单位{} responseEntity-toString:{}", empNo, rspString);
        fileWrite("登记医保-查询单位信息-请求返回报文记录.txt", empNo + ": " + rspString, true);
        JSONObject jsonObject = JSON.parseObject(rspString);
        String jsonString = jsonObject.getString("data");
        if (StringUtils.isBlank(jsonString)) {
            throw new RuntimeException("无返回报文体");
        }
        List<EmpBasicInfo> empBasicInfoList = JSON.parseArray(jsonString, EmpBasicInfo.class);
        return empBasicInfoList.get(0);
    }


//    private HttpEntity giveMeSavaCanBaoEntity(CanBaoInfoRsp canBaoInfoRsp) {
//        List<BasicNameValuePair> basicNameValuePairs = new ArrayList<>();
//        basicNameValuePairs.add(new BasicNameValuePair("frontUrl", "http://10.85.200.97/mbs-gg-ui/N0104.html#/N010401"));
//        basicNameValuePairs.add(new BasicNameValuePair("_modulePartId_", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("projectNo", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("taskId", ""));
//
//        basicNameValuePairs.add(new BasicNameValuePair("psnNo", canBaoInfoRsp.getData().getPsnInfo().getPsnNo()));
//        basicNameValuePairs.add(new BasicNameValuePair("psnCertType", canBaoInfoRsp.getData().getPsnInfo().getPsnCertType()));
//        basicNameValuePairs.add(new BasicNameValuePair("psnName", canBaoInfoRsp.getData().getPsnInfo().getPsnName()));
//        basicNameValuePairs.add(new BasicNameValuePair("gend", canBaoInfoRsp.getData().getPsnInfo().getGend()));
//        basicNameValuePairs.add(new BasicNameValuePair("naty", canBaoInfoRsp.getData().getPsnInfo().getNaty()));
//        basicNameValuePairs.add(new BasicNameValuePair("brdy", canBaoInfoRsp.getData().getPsnInfo().getBrdy()));
//        basicNameValuePairs.add(new BasicNameValuePair("natRegnCode", canBaoInfoRsp.getData().getPsnInfo().getNatRegnCode()));
//        basicNameValuePairs.add(new BasicNameValuePair("mrgStas",  canBaoInfoRsp.getData().getPsnInfo().getMrgStas()));
//        basicNameValuePairs.add(new BasicNameValuePair("hlcon", canBaoInfoRsp.getData().getPsnInfo().getHlcon()));
//        basicNameValuePairs.add(new BasicNameValuePair("insuAdmdvs", canBaoInfoRsp.getData().getPsnInsuInfoFromPic().get(0).getInsuAdmdvs()));
//        basicNameValuePairs.add(new BasicNameValuePair("mob", canBaoInfoRsp.getData().getPsnInfo().getMob()));
//        basicNameValuePairs.add(new BasicNameValuePair("resdNatu", canBaoInfoRsp.getData().getPsnInfo().getResdNatu()));
//
//        basicNameValuePairs.add(new BasicNameValuePair("resdLocAdmdvs", canBaoInfoRsp.getData().getPsnInfo().getResdLocAdmdvs()));
//        basicNameValuePairs.add(new BasicNameValuePair("hsregAddr", canBaoInfoRsp.getData().getPsnInfo().getHsregAddr()));
//        basicNameValuePairs.add(new BasicNameValuePair("liveAdmdvs", canBaoInfoRsp.getData().getPsnInfo().getLiveAdmdvs()));
//        basicNameValuePairs.add(new BasicNameValuePair("liveAddr", canBaoInfoRsp.getData().getPsnInfo().getLiveAddr()));
//        basicNameValuePairs.add(new BasicNameValuePair("certno", canBaoInfoRsp.getData().getPsnInfo().getCertno()));
//        //TODO
//        basicNameValuePairs.add(new BasicNameValuePair("empNo", canBaoInfoRsp.getData().getPsnInsuInfoFromPic().get(0).getEmpNo()));
//        basicNameValuePairs.add(new BasicNameValuePair("empEnttCodg", ""));
//
//        //TODO 是否為查詢GetPsnByConditionRspBo
//        basicNameValuePairs.add(new BasicNameValuePair("empMgtType", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("prntEmpNo", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("asocLegentFlag", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("empType", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("empName", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("regName", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("locAdmdvs", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("conerName", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("conerEmail", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("tel", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("faxNo", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("taxNo", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("orgcode", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("regno", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("regnoCertType", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("empAddr", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("poscode", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("aprvEstaDept", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("aprvEstaDate", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("aprvEstaDocno", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("prntAdmdvs", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("orgValiStas", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("legrepTel", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("legrepName", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("legrepCertType", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("legrepCertno", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("orgcodeIssuEmp", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("valiFlag", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("rid", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("crteTime", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("updtTime", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("crterName", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("opterName", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("optTime", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("crterId", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("crteOptinsNo", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("opterId", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("optinsNo", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("poolareaNo", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("ver", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("syncPrntFlag", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("servMattInstId", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("evtInstId", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("sysCode", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("entpSpecFlag", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("memo", ""));
//        basicNameValuePairs.add(new BasicNameValuePair("taxEmpNo", ""));
//
//
//        final UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(basicNameValuePairs, StandardCharsets.UTF_8);
//        return urlEncodedFormEntity;
//    }


    private String queryPersonCanBaoInfoByCertno(String empNo, String certno) throws IOException {
        final HttpPost httpPost = new HttpPost(GET_PERSON_CANBAO_INFO);
        setHeaders(httpPost);

        httpPost.setEntity(giveMeAPersonCanBaoReq(empNo, certno));
        CloseableHttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (IOException e) {
            log.error("{}", e);
            throw new RuntimeException(e);
        }
        HttpEntity responseEntity = httpResponse.getEntity();
        String rspString = EntityUtils.toString(responseEntity);
        log.info("人员{} responseEntity-toString:{}", certno, rspString);
        return rspString;
    }

    private CanBaoInfoRsp queryPersonCanBaoInfo(String empNo, String certno) throws IOException {
        final HttpPost httpPost = new HttpPost(GET_PERSON_CANBAO_INFO);
        setHeaders(httpPost);

        httpPost.setEntity(giveMeAPersonCanBaoReq(empNo, certno));
        CloseableHttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (IOException e) {
            log.error("{}", e);
            throw new RuntimeException(e);
        }
        HttpEntity responseEntity = httpResponse.getEntity();
        String rspString = EntityUtils.toString(responseEntity);
        log.info("人员{} responseEntity-toString:{}", certno, rspString);
        fileWrite("登记医保-请求返回报文记录.txt", certno + ": " + rspString, true);
        if (rspString.contains("异常流水号")) {
            fileWrite("登记医保-检查-存在异常流水号.txt", "'" + certno, true);
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(rspString);
        String wrapperResponse = jsonObject.getJSONObject("data").getString("wrapperResponse");

        CanBaoInfoRsp canBaoInfoRsp = JSON.parseObject(wrapperResponse, CanBaoInfoRsp.class);

        if (canBaoInfoRsp.getType().equals("success") && canBaoInfoRsp.getMessage().contains("成功") && canBaoInfoRsp.getData().getMessage().contains("该人员可以进行城乡居民参保登记")) {
            log.info("人员：{} {} 具备参保条件", canBaoInfoRsp.getData().getPsnInfo().getCertno(), canBaoInfoRsp.getData().getPsnInfo().getPsnName());
            fileWrite("登记医保-检查-符合参保条件记录.txt", "'" + certno, true);
        } else {
            log.info("人员：{} {} 参保不符合条件，请见文件", canBaoInfoRsp.getData().getPsnInfo().getCertno(), canBaoInfoRsp.getData().getPsnInfo().getPsnName());
            fileWrite("登记医保-检查-参保条件不符合异常记录.txt", "'" + certno + " : " + rspString, true);
        }


        //  fileWrite("参保登记-参保条件达标记录bo.JSON", JSON.toJSONString(canBaoInfoRsp), true);
        return
                canBaoInfoRsp;
    }

    private HttpEntity giveMeAPersonCanBaoReq(String empNo, String certno) {
        List<BasicNameValuePair> basicNameValuePairs = new ArrayList<>();
        basicNameValuePairs.add(new BasicNameValuePair("frontUrl", "http://10.85.200.97/mbs-gg-ui/N0104.html#/N010401"));
        basicNameValuePairs.add(new BasicNameValuePair("certno", certno));
        basicNameValuePairs.add(new BasicNameValuePair("empNo", empNo));
        basicNameValuePairs.add(new BasicNameValuePair("certType", "01"));
        basicNameValuePairs.add(new BasicNameValuePair("insuAdmdvs", "411328"));
        basicNameValuePairs.add(new BasicNameValuePair("_modulePartId_", ""));

        final UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(basicNameValuePairs, StandardCharsets.UTF_8);
        return urlEncodedFormEntity;
    }


    public SavePauseRsp savePauseByCertnoAndEmpNo(String certno, String operaEmpNo) throws IOException {
        PsnByConditionReqBo psnByConditionReqBo = PsnByConditionReqBo.giveMeDefaultBo();
        psnByConditionReqBo.setCertno(certno);
        return savePause(queryPersonInfo(psnByConditionReqBo), operaEmpNo);
    }

    private SavePauseRsp savePause(GetPsnByConditionRspBo psnByConditionRspBo, String operaEmpNo) throws IOException {
        final HttpPost httpPost = new HttpPost(SAVE_PAUSE_CLCT);
        setHeaders(httpPost);
        httpPost.setEntity(giveMeAPauseReq(psnByConditionRspBo, operaEmpNo));
        CloseableHttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (IOException e) {
            log.error("{}", e);
            throw new RuntimeException(e);
        }
        HttpEntity responseEntity = httpResponse.getEntity();
        String rspString = EntityUtils.toString(responseEntity);
        log.info("人员{} responseEntity-toString:{}", psnByConditionRspBo.getCertno() + psnByConditionRspBo.getPsnName(), rspString);
        fileWrite("暂停医保-请求返回报文记录.txt", psnByConditionRspBo.getCertno() + ": " + rspString, true);
        JSONObject jsonObject = JSON.parseObject(rspString);
        if (rspString.contains("异常流水号")) {
            fileWrite("一键参保-暂停参保-异常流水号.txt", "'" + psnByConditionRspBo.getCertno() + ": " + rspString, true);
            return null;
        }
        String psnInsuListString = jsonObject.getJSONObject("data").getString("response");

        SavePauseRsp savePauseRsp = JSON.parseObject(psnInsuListString, SavePauseRsp.class);

        if (savePauseRsp.getType().equals("success") && savePauseRsp.getMessage().contains("参保人员暂停缴费经办成功")) {
            log.info("人员：{} {} 暂停医保成功", psnByConditionRspBo.getCertno(), psnByConditionRspBo.getPsnName());
            fileWrite("一键参保-暂停参保-暂停医保成功记录.txt", "'" + psnByConditionRspBo.getCertno(), true);
        } else {
            log.info("人员：{} {} 暂停医保出现异常，请见文件", psnByConditionRspBo.getCertno(), psnByConditionRspBo.getPsnName());
        }


        return savePauseRsp;
    }

    private HttpEntity giveMeAPauseReq(GetPsnByConditionRspBo psnInfo, String operaEmpNo) throws IOException {
        PsnInsuInfoRspBo psnInsuInfoRspBo = queryPsnInsuInfo(psnInfo);
        if (psnInsuInfoRspBo == null) {
            log.error("psnInsuInfoRspBo return null, {}", psnInfo.getPsnNo());
            throw new RuntimeException("psnInsuInfoRspBo return null");
        }
        EmpInsureInfo empInsureInfo = queryEmpInsureInfo(psnInfo.getEmpNo());
        if (empInsureInfo == null) {
            log.error("empInsureInfo return null, {} ", psnInfo.getEmpNo());
            throw new RuntimeException("empInsureInfo return null");
        }
        List<BasicNameValuePair> basicNameValuePairs = new ArrayList<>();

        basicNameValuePairs.add(new BasicNameValuePair("changeDate", getCurrentDate()));
        basicNameValuePairs.add(new BasicNameValuePair("changeReason", "7199"));
        basicNameValuePairs.add(new BasicNameValuePair("memo", "更改参保地"));
        basicNameValuePairs.add(new BasicNameValuePair("insuTypeList[0]", empInsureInfo.getInsutype()));
        basicNameValuePairs.add(new BasicNameValuePair("empNo", empInsureInfo.getEmpNo()));
        basicNameValuePairs.add(new BasicNameValuePair("psnNo", psnInfo.getPsnNo()));
        basicNameValuePairs.add(new BasicNameValuePair("psnName", psnInfo.getPsnName()));
        basicNameValuePairs.add(new BasicNameValuePair("taskId", ""));
        basicNameValuePairs.add(new BasicNameValuePair("insuAdmdvs", empInsureInfo.getInsuAdmdvs()));
        basicNameValuePairs.add(new BasicNameValuePair("poolareaNo", psnInfo.getPoolareaNo()));
        basicNameValuePairs.add(new BasicNameValuePair("_modulePartId_", ""));
        basicNameValuePairs.add(new BasicNameValuePair("frontUrl", "http://10.85.200.97/mbs-gg-ui/N0104.html#/N010406"));
        PsnInsuPauseDTO psnInsuPauseDTO = new PsnInsuPauseDTO();

        psnInsuPauseDTO.setPsnInsuRltsId(psnInsuInfoRspBo.getPsnInsuRltsId());
        psnInsuPauseDTO.setEmpNo(psnInfo.getEmpNo());
        psnInsuPauseDTO.setPsnNo(psnInfo.getPsnNo());
        psnInsuPauseDTO.setPsnCertType(psnInfo.getPsnCertType());
        psnInsuPauseDTO.setCertno(psnInfo.getCertno());
        psnInsuPauseDTO.setPsnName(psnInfo.getPsnName());
        psnInsuPauseDTO.setInsutype(psnInfo.getInsutype());
//        psnInsuPauseDTO.setInsutypeCHN(null);
        psnInsuPauseDTO.setPsnInsuStas(psnInfo.getPsnInsuStas());
        psnInsuPauseDTO.setPoolareaNo(psnInfo.getPoolareaNo());
        psnInsuPauseDTO.setCrtInsuDate(psnInsuInfoRspBo.getCrtInsuDate());
        psnInsuPauseDTO.setPsnInsuDate(psnInsuInfoRspBo.getPsnInsuDate());
        psnInsuPauseDTO.setFstInsuYm(psnInsuInfoRspBo.getFstInsuYm());
//        psnInsuPauseDTO.setCrtPatcJobDate(null);
        psnInsuPauseDTO.setAcctCrtnYm(psnInsuInfoRspBo.getAcctCrtnYm());
        psnInsuPauseDTO.setClctWay(psnInsuInfoRspBo.getClctWay());
        psnInsuPauseDTO.setHiType(psnInsuInfoRspBo.getHiType());
        psnInsuPauseDTO.setEmpFom(psnInsuInfoRspBo.getEmpFom());
        psnInsuPauseDTO.setInsutypeRetrFlag(psnInsuInfoRspBo.getInsutypeRetrFlag());
//        psnInsuPauseDTO.setBegnYm("");
//        psnInsuPauseDTO.setExpiYm("");
        psnInsuPauseDTO.setPsnType(psnInsuInfoRspBo.getPsnType());
//        psnInsuPauseDTO.setPsnTypeItem("");
        psnInsuPauseDTO.setQutsType(psnInsuInfoRspBo.getQutsType());
        psnInsuPauseDTO.setClctstdCrtfRuleCodg(empInsureInfo.getClctstdCrtfRuleCodg());
        psnInsuPauseDTO.setClctstdCrtfRuleCodgCHN(empInsureInfo.getClctStdRuleList().get(0).getRuleName());
        psnInsuPauseDTO.setClctRuleTypeCodg(empInsureInfo.getClctRuleTypeCodg());
        psnInsuPauseDTO.setClctRuleTypeCodgCHN(empInsureInfo.getClcRuleList().get(0).getRuleName());
        psnInsuPauseDTO.setMaxAcctprd(empInsureInfo.getMaxAcctprd());
        psnInsuPauseDTO.setInsuAdmdvs(empInsureInfo.getInsuAdmdvs());
        psnInsuPauseDTO.setRetrTrtBegnDate(psnInsuInfoRspBo.getRetrTrtBegnDate());
        psnInsuPauseDTO.setRetrTrtEnjymntFlag(psnInsuInfoRspBo.getRetrTrtEnjymntFlag());
        psnInsuPauseDTO.setBeHospitalized(empInsureInfo.getBeHospitalizedValue());
        //TODO


        List<PsnInsuPauseDTO> psnInsuPauseDTOList = new ArrayList<>();
        psnInsuPauseDTOList.add(psnInsuPauseDTO);
        basicNameValuePairs.add(new BasicNameValuePair("psnInsuPauseDTO", JSON.toJSONString(psnInsuPauseDTOList)));


        final UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(basicNameValuePairs, StandardCharsets.UTF_8);

        return urlEncodedFormEntity;

    }

    private String getCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String currentDate = simpleDateFormat.format(date);
        log.info("current date: {}", currentDate);
        return currentDate;
    }


    //第一步输入身份证，查询是否文峰单位
    @Test
    public void testGetPsnByCondition() throws IOException {
        readFileLines("人员医保信息-身份证列表-输入.txt").stream().forEach((linesCards) -> {
            try {
                readPsnInfoByCard(linesCards);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        ;

    }

    public GetPsnByConditionRspBo queryPersonInfoByCertno(String certno) throws IOException {
        PsnByConditionReqBo psnByConditionReqBo = PsnByConditionReqBo.giveMeDefaultBo();
        psnByConditionReqBo.setCertno(certno);
        return queryPersonInfo(psnByConditionReqBo);
    }

    private void readPsnInfoByCard(String certno) throws IOException {
        PsnByConditionReqBo psnByConditionReqBo = PsnByConditionReqBo.giveMeDefaultBo();
        psnByConditionReqBo.setCertno(certno);
        GetPsnByConditionRspBo psnByConditionRspBo = queryPersonInfo(psnByConditionReqBo);
        if (psnByConditionRspBo == null) {
            log.error("未查询到结果：{}, 记录在：人员医保信息-存在问题结果记录.txt", psnByConditionReqBo.getCertno());
            fileWrite("人员医保信息-存在问题结果记录.txt", psnByConditionReqBo.toString(), true);
            return;
        }
        if (!psnByConditionRspBo.getEmpName().contains("文峰")) {
            fileWrite("人员医保信息-非文峰单位记录.txt", psnByConditionRspBo.toString(), true);
        }
        fileWrite("人员医保信息-查询结果记录.txt", psnByConditionRspBo.toString(), true);
    }

    // 查询医保单位信息
    private EmpInsureInfo queryEmpInsureInfo(String empNo) throws IOException {
        final HttpPost httpPost = new HttpPost(GET_EMP_INSU_INFO);
        setHeaders(httpPost);
        httpPost.setEntity(giveMeAEmpInsuReqBo(empNo));
        CloseableHttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (IOException e) {
            log.error("{}", e);
            throw new RuntimeException(e);
        }
        HttpEntity responseEntity = httpResponse.getEntity();
        String rspString = EntityUtils.toString(responseEntity);
        log.info("reqUrl: {}", GET_EMP_INSU_INFO);
        log.info("responseEntity-toString:{}", rspString);
        JSONObject jsonObject = JSON.parseObject(rspString);
        String resultString = jsonObject.getJSONObject("data").getString("data");
        if (StringUtils.isBlank(resultString)) {

            return null;
        }
        List<EmpInsureInfo> empInsureInfos = JSON.parseArray(resultString, EmpInsureInfo.class);
        log.info("{}", empInsureInfos.get(0).toString());

        return empInsureInfos.get(0);
    }

    private HttpEntity giveMeAEmpInsuReqBo(String empNo) {
        List<BasicNameValuePair> basicNameValuePairs = new ArrayList<>();
        basicNameValuePairs.add(new BasicNameValuePair("frontUrl", "http://10.85.200.97/mbs-gg-ui/N0104.html#/N010401"));
        basicNameValuePairs.add(new BasicNameValuePair("empNo", empNo));
        basicNameValuePairs.add(new BasicNameValuePair("_modulePartId_", ""));

        final UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(basicNameValuePairs, StandardCharsets.UTF_8);
        return urlEncodedFormEntity;
    }

    // 查询人员参保信息,根据人员信息
    private PsnInsuInfoRspBo queryPsnInsuInfo(GetPsnByConditionRspBo psnByConditionRspBo) throws IOException {
        final HttpPost httpPost = new HttpPost(GET_PSN_INSU_INFO);
        setHeaders(httpPost);
        httpPost.setEntity(giveMeAGetPsnInsuReqBo(psnByConditionRspBo));
        CloseableHttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (IOException e) {
            log.error("{}", e);
            throw new RuntimeException(e);
        }
        HttpEntity responseEntity = httpResponse.getEntity();
        String rspString = EntityUtils.toString(responseEntity);
        log.info("responseEntity-toString:{}", rspString);
        JSONObject jsonObject = JSON.parseObject(rspString);
        String psnInsuListString = jsonObject.getJSONObject("data").getString("psnInsuList");
        if (StringUtils.isBlank(psnInsuListString)) {

            return null;
        }
        List<PsnInsuInfoRspBo> psnInsuInfoRspBoList = JSON.parseArray(psnInsuListString, PsnInsuInfoRspBo.class);
        log.info("{}", psnInsuInfoRspBoList.get(0).toString());

        return psnInsuInfoRspBoList.get(0);
    }

    private HttpEntity giveMeAGetPsnInsuReqBo(GetPsnByConditionRspBo psnByConditionRspBo) {
        List<BasicNameValuePair> basicNameValuePairs = new ArrayList<>();
        basicNameValuePairs.add(new BasicNameValuePair("frontUrl", "http://10.85.200.97/mbs-gg-ui/N1412-new.html#/N141201-new"));
        basicNameValuePairs.add(new BasicNameValuePair("psnNo", psnByConditionRspBo.getPsnNo()));
        basicNameValuePairs.add(new BasicNameValuePair("insuAdmdvs", psnByConditionRspBo.getInsuAdmdvs()));
        basicNameValuePairs.add(new BasicNameValuePair("_modulePartId_", ""));

        final UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(basicNameValuePairs, StandardCharsets.UTF_8);
        return urlEncodedFormEntity;
    }

    private GetPsnByConditionRspBo queryPersonInfo(PsnByConditionReqBo psnByConditionReqBo) throws IOException {
        final HttpPost httpPost = new HttpPost(get_PsnByCondition);

        setHeaders(httpPost);

        httpPost.setEntity(giveMeAGetPsnByConditionReqBo(psnByConditionReqBo));

        CloseableHttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (IOException e) {
            log.error("{}", e);
            throw new RuntimeException(e);
        }
        HttpEntity responseEntity = httpResponse.getEntity();
        String rspString = EntityUtils.toString(responseEntity);
        log.info("responseEntity-toString:{}", rspString);
        JSONObject jsonObject = JSON.parseObject(rspString);
        String psnInsuListString = jsonObject.getJSONObject("data").getString("list");
        if (StringUtils.isBlank(psnInsuListString)) {

            return null;
        }
        List<GetPsnByConditionRspBo> psnByConditionRspBoList = JSON.parseArray(psnInsuListString, GetPsnByConditionRspBo.class);
        if (psnByConditionRspBoList.size() > 1) {
            fileWrite("人员重复列表.txt", psnByConditionRspBoList.get(0).getCertno(), true);
            return null;
        }
        log.info("{}", psnByConditionRspBoList.get(0).toString());

        return psnByConditionRspBoList.get(0);
    }

    private HttpEntity giveMeAGetPsnByConditionReqBo(PsnByConditionReqBo psnByConditionReqBo) {

        List<BasicNameValuePair> basicNameValuePairs = new ArrayList<>();
        basicNameValuePairs.add(new BasicNameValuePair("frontUrl", psnByConditionReqBo.getFrontUrl()));
        basicNameValuePairs.add(new BasicNameValuePair("certno", psnByConditionReqBo.getCertno()));
        basicNameValuePairs.add(new BasicNameValuePair("psnCertType", psnByConditionReqBo.getPsnCertType()));
        basicNameValuePairs.add(new BasicNameValuePair("servMattNo", psnByConditionReqBo.getServMattNo()));
        basicNameValuePairs.add(new BasicNameValuePair("_modulePartId_", psnByConditionReqBo.get_modulePartId_()));
        basicNameValuePairs.add(new BasicNameValuePair("searchInsuEmp", psnByConditionReqBo.getSearchInsuEmp()));


        final StringEntity stringEntity =
                new StringEntity(
                        JSON.toJSONString(psnByConditionReqBo),
                        "UTF-8");
        final UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(basicNameValuePairs, StandardCharsets.UTF_8);
        return urlEncodedFormEntity;
    }

    @Test
    public void testQueryPsnInfo() throws IOException {
        //TODO url要改
        final HttpPost httpPost = new HttpPost(get_PsnByCondition);

        setHeaders(httpPost);

        httpPost.setEntity(giveMeAQueryPsnInfoReqEntity());

        CloseableHttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (IOException e) {
            log.error("{}", e);
            throw new RuntimeException(e);
        }
        HttpEntity responseEntity = httpResponse.getEntity();
        log.info("responseEntity-toString:{}", EntityUtils.toString(responseEntity));
        JSONObject jsonObject = JSON.parseObject(EntityUtils.toString(responseEntity));
        String psnInsuListString = jsonObject.getJSONObject("data").getString("psnInsuList");
        GetPsnInsuInfoReqBo getPsnInsuInfoReqBo = JSON.parseObject(psnInsuListString, GetPsnInsuInfoReqBo.class);
//        JSONArray jsonArray =
//                log.info("responseEntity-jsonObject:{}", jsonObject);


    }


    @Test
    public void testJson() {
        String testJson = "{\n" +
                "    \"code\": 0,\n" +
                "    \"type\": \"success\",\n" +
                "    \"message\": \"成功\",\n" +
                "    \"data\": {\n" +
                "        \"psnInsuList\": [\n" +
                "            {\n" +
                "                \"psnInsuMgtEid\": \"41132800000186275652\",\n" +
                "                \"psnInsuRltsId\": \"41132820000044386183\",\n" +
                "                \"empNo\": \"41132800000000360896\",\n" +
                "                \"empNoList\": null,\n" +
                "                \"psnNo\": \"41132820000000000044386183\",\n" +
                "                \"hiType\": null,\n" +
                "                \"insutype\": \"390\",\n" +
                "                \"psnInsuStas\": \"1\",\n" +
                "                \"acctCrtnYm\": \"202101\",\n" +
                "                \"fstInsuYm\": \"202101\",\n" +
                "                \"crtInsuDate\": \"2024-10-19 00:00:00\",\n" +
                "                \"psnInsuDate\": \"2024-10-19 00:00:00\",\n" +
                "                \"pausInsuDate\": null,\n" +
                "                \"insutypeRetrFlag\": \"0\",\n" +
                "                \"qutsType\": null,\n" +
                "                \"clctWay\": \"02\",\n" +
                "                \"empFom\": null,\n" +
                "                \"maxAcctprd\": \"202212\",\n" +
                "                \"clctRuleTypeCodg\": \"411300520200390101\",\n" +
                "                \"clctstdCrtfRuleCodg\": \"411300520200390201\",\n" +
                "                \"insuAdmdvs\": \"411328\",\n" +
                "                \"optinsNo\": \"S41132810207\",\n" +
                "                \"optTime\": \"2024-10-19 23:52:35\",\n" +
                "                \"opterName\": \"刘付科\",\n" +
                "                \"opterId\": \"1665369369827602755\",\n" +
                "                \"poolareaNo\": \"411300\",\n" +
                "                \"crteTime\": \"2024-10-19 23:52:37\",\n" +
                "                \"rid\": \"410000202410192352373596983816\",\n" +
                "                \"updtTime\": \"2024-10-19 23:52:37\",\n" +
                "                \"psnType\": \"1403\",\n" +
                "                \"crterId\": \"1665369369827602755\",\n" +
                "                \"crterName\": \"刘付科\",\n" +
                "                \"crteOptinsNo\": \"S41132810207\",\n" +
                "                \"optChnl\": null,\n" +
                "                \"retrTrtBegnDate\": null,\n" +
                "                \"retrTrtEnjymntFlag\": null,\n" +
                "                \"empName\": \"文峰\",\n" +
                "                \"empType\": \"9006\",\n" +
                "                \"empMgtType\": \"08\",\n" +
                "                \"clcRuleList\": null,\n" +
                "                \"clctStdRuleList\": null\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";
        JSONObject jsonObject = JSON.parseObject(testJson);
        String psnInsuListString = jsonObject.getJSONObject("data").getString("psnInsuList");
        List<PsnInsuInfoRspBo> psnInsuInfoRspBos = JSON.parseArray(psnInsuListString, PsnInsuInfoRspBo.class);
        log.info("{}", psnInsuInfoRspBos.get(0).toString());
    }


    private StringEntity giveMeAQueryPsnInfoReqEntity() {
        GetPsnInsuInfoReqBo reqBo = new GetPsnInsuInfoReqBo();
        reqBo.setPsnNo("41132820000000000044386183");
        reqBo.setInsuAdmdvs("411328");
        reqBo.set_modulePartId_("");
        reqBo.setFrontUrl("http://10.85.200.97/mbs-gg-ui/N1412-new.html#/N141201-new");
        final StringEntity stringEntity =
                new StringEntity(
                        JSON.toJSONString(reqBo),
                        "UTF-8");
        return stringEntity;
    }

    private void setHeaders(HttpPost httpPost) {
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.setHeader("Accept", "application/json, text/plain, */*");
        httpPost.setHeader("Accept-Encoding", "text/gzip, deflate");
        httpPost.setHeader("Accept-Language", "text/zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6,zh-TW;q=0.5");
        httpPost.setHeader("Connection", "keep-alive");
        httpPost.setHeader("Cookie", COOKIE);
        httpPost.setHeader("X-Xsrf-Token", TOKEN);
    }

    public void fileWrite(String filePath, String content, boolean isAdd) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(filePath, isAdd)) {
            byte[] bytes = content.getBytes();
            fileOutputStream.write(bytes);
            fileOutputStream.write("\r\n".getBytes());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> readFileLines(String fileName) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(fileName)));
        String line = null;
        List<String> stringList = new ArrayList<>();
        while ((line = bufferedReader.readLine()) != null) {
            stringList.add(line);
        }
        bufferedReader.close();
        return stringList;
    }
}
