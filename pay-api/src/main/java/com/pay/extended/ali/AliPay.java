package com.pay.extended.ali;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.common.datetime.DateTimeUtil;
import com.pay.common.PayAccount;
import com.pay.common.dict.PayStatus;
import com.pay.extended.PayApi;
import com.pay.common.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public abstract class AliPay implements PayApi {

    abstract String getProductCode();//决定具体的支付方式

    private AlipayClient buildClient(PayAccount account) {
        return new DefaultAlipayClient(account.getAliGateWay(), account.getAliAppId(), account.getAliAppPrivateKey(),
                account.getAliDataType(), account.getCharset(), account.getAliPublicKey(), account.getAliSignType()); //获得初始化的AlipayClient
    }


    @Override
    public String pay(PayAccount account, PayParam param) {
        AlipayClient alipayClient = buildClient(account); //获得初始化的AlipayClient
        AlipayTradeAppPayRequest alipayRequest = new AlipayTradeAppPayRequest();
        alipayRequest.setNotifyUrl(account.getNotifyUrl());
        alipayRequest.setBizModel(toBizMode(param));
        try {
            return alipayClient.sdkExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            throw new RuntimeException("支付异常");
        }
    }


    private AlipayTradeWapPayModel toBizMode(PayParam param) {
        AlipayTradeWapPayModel payModel = new AlipayTradeWapPayModel();
        payModel.setSubject(param.getSubject());
        payModel.setBody(param.getDesc());
        payModel.setOutTradeNo(param.getOutTradeNo());
        LocalDateTime timeExpire = param.getTimeExpire();
        if (timeExpire != null) {
            payModel.setTimeoutExpress(LocalDateTime.now().until(timeExpire, ChronoUnit.MINUTES) + "m");
        }
        payModel.setTotalAmount(param.getMoney().toPlainString());
        payModel.setProductCode(getProductCode());
        return payModel;
    }

    @Override
    public PayResult query(PayAccount account, PayParam param) {
        PayResult<String> result = new PayResult<>();
        AlipayClient alipayClient = buildClient(account); //获得初始化的AlipayClient
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();

        AlipayTradeQueryModel queryModel = new AlipayTradeQueryModel();
        queryModel.setTradeNo(param.getTradeNo());
        queryModel.setOutTradeNo(param.getOutTradeNo());
        request.setBizModel(queryModel);//设置业务参数
        AlipayTradeQueryResponse response;
        try {
            response = alipayClient.execute(request);
            if ("1000".equals(response.getCode()) && ("TRADE_SUCCESS".equals(response.getTradeStatus()) ||
                    "TRADE_FINISHED".equals(response.getTradeStatus()))) {
                result.setPayTime(LocalDateTime.ofInstant(response.getSendPayDate().toInstant(), ZoneId.systemDefault()));
                result.setDesc("第三方支付成功");
                result.setStatus(PayStatus.SUCCESS);
                result.setTradeNo(response.getTradeNo());
                result.setOrderCode(response.getOutTradeNo());
                result.setMoney(new BigDecimal(response.getTotalAmount()));
            } else {
                result.setStatus(PayStatus.PROCESS);
            }
        } catch (AlipayApiException e) {
            throw new RuntimeException("查询异常");
        }
        return result;
    }

    @Override
    public PayResult refund(PayAccount account, PayParam param) {
        AlipayClient alipayClient = buildClient(account); //获得初始化的AlipayClient

        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel refundModel = new AlipayTradeRefundModel();
        refundModel.setTradeNo(param.getTradeNo());
        refundModel.setOutRequestNo(param.getOutTradeNo());
        refundModel.setRefundAmount(param.getRefundAmount().toPlainString());
        refundModel.setOutRequestNo(param.getRefundNo());
        refundModel.setRefundReason(param.getRefundReason());
        request.setBizModel(refundModel);//设置业务参数
        AlipayTradeRefundResponse response;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            throw new RuntimeException("退款异常");
        }
        PayResult payResult = new PayResult();
        if ("1000".equals(response.getCode())) {
            payResult.setStatus(PayStatus.SUCCESS);
        } else {
            payResult.setStatus(PayStatus.FAIL);
            payResult.setDesc(String.format("退款申请失败->状态[%s] 原因[%s]", response.getCode(), response.getMsg()));
        }
        payResult.setPayTime(LocalDateTime.now());
        return payResult;
    }

    @Override
    public PayResult<Map<String, String>> notify(PayAccount account, Object notifyParam) {
        boolean check;
        //noinspection unchecked
        Map<String, String> notifyMap = (Map<String, String>) notifyParam;
        try {
            check = AlipaySignature.rsaCheckV1(notifyMap, account.getAliPublicKey(),
                    account.getCharset(), account.getAliSignType()); //调用SDK验证签名
        } catch (AlipayApiException e) {
            throw new RuntimeException("回调参数校验异常");
        }

        PayResult<Map<String, String>> payResult = new PayResult<>();
        payResult.setRawData(notifyMap);
        String resultCode = notifyMap.get("trade_status");
        payResult.setDesc(resultCode);
        payResult.setMoney(new BigDecimal(notifyMap.get("total_amount")));
        payResult.setPayTime(DateTimeUtil.parse(notifyMap.get("gmt_payment"), "yyyy-MM-dd HH:mm:ss"));
        payResult.setOrderCode(notifyMap.get("out_trade_no"));
        payResult.setTradeNo(notifyMap.get("trade_no"));
        payResult.setCheck(check);

        if ("TRADE_SUCCESS".equals(resultCode) || "TRADE_FINISHED".equals(resultCode)) {
            payResult.setStatus(PayStatus.SUCCESS);
        } else if ("TRADE_CLOSED".equals(resultCode)) {
            payResult.setStatus(PayStatus.CLOSE);
        } else {
            payResult.setStatus(PayStatus.PROCESS);
        }
        return payResult;
    }


}
