package com.pay.extended.wx;

import com.common.collection.CollectionUtil;
import com.common.datetime.DateTimeUtil;
import com.common.number.NumberUtil;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConfig;
import com.github.wxpay.sdk.WXPayUtil;
import com.pay.common.PayAccount;
import com.pay.common.PayParam;
import com.pay.common.PayResult;
import com.pay.common.dict.PayStatus;
import com.pay.extended.PayApi;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public abstract class WxPay implements PayApi {


    protected static WXPayConfig buildConfig(PayAccount account) {
        WxConfig config = new WxConfig();
        config.setAppID(account.getWxAppID());
        config.setMchID(account.getWxMchID());
        config.setKey(account.getWxKey());
        config.setHttpConnectTimeoutMs(8000);
        config.setHttpReadTimeoutMs(10000);
        return config;
    }

    protected static Map<String, String> buildPayMap(PayParam param) {
        Map<String, String> body = new HashMap<>();
        CollectionUtil.putIfNotNull(body, "nonce_str", WXPayUtil.generateNonceStr());
        CollectionUtil.putIfNotNull(body, "attach", param.getSubject());
        CollectionUtil.putIfNotNull(body, "body", param.getDesc());
        CollectionUtil.putIfNotNull(body, "out_trade_no", param.getOutTradeNo());
        CollectionUtil.putIfNotNull(body, "total_fee", NumberUtil.yuan2fen(param.getMoney()));
        CollectionUtil.putIfNotNull(body, "spbill_create_ip", param.getClientIp());
        CollectionUtil.putIfNotNull(body, "time_expire", param.getTimeExpire().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        CollectionUtil.putIfNotNull(body, "notify_url", param.getNotifyUrl());
        CollectionUtil.putIfNotNull(body, "openid", param.getOpenId());
        CollectionUtil.putIfNotNull(body, "product_id", param.getProductId());
        return body;
    }


    protected static Map<String, String> pay(WXPayConfig config, Map<String, String> body, String tradeType) {
        body.put("trade_type", tradeType);
        WXPay wxpay = new WXPay(config);
        Map<String, String> resp;
        try {
            resp = wxpay.unifiedOrder(body);
            if (!"SUCCESS".equals(resp.get("return_code"))) {
                throw new RuntimeException(String.format("%s支付发起失败:%s", tradeType, resp.get("return_msg")));
            } else {
                if (!"SUCCESS".equals(resp.get("result_code"))) {
                    throw new RuntimeException(String.format("%s支付发起成功,处理失败:%s", tradeType, resp.get("err_code_des")));
                }
            }
            return resp;
        } catch (Exception e) {
            throw new RuntimeException(String.format("%s支付下单异常:%s", tradeType, e.getMessage()));
        }
    }


    protected static String timeStamp() {
        long ms = System.currentTimeMillis();
        return String.valueOf(ms / 1000L);
    }

    protected static String sign(Map<String, String> map, String key) {
        try {
            return WXPayUtil.generateSignature(map, key);
        } catch (Exception e) {
            throw new RuntimeException(String.format("签名异常:%s", e.getMessage()));
        }

    }

    protected static Map<String, String> buildQueryMap(PayParam payParam) {
        Map<String, String> body = new HashMap<>();
        CollectionUtil.putIfNotNull(body, "nonce_str", WXPayUtil.generateNonceStr());
        CollectionUtil.putIfNotNull(body, "out_trade_no", payParam.getOutTradeNo());
        CollectionUtil.putIfNotNull(body, "transaction_id", payParam.getTradeNo());
        return body;
    }

    public static Map<String, String> buildRefundMap(PayParam payParam) {
        Map<String, String> body = new HashMap<>();
        CollectionUtil.putIfNotNull(body, "nonce_str", WXPayUtil.generateNonceStr());
        CollectionUtil.putIfNotNull(body, "transaction_id", payParam.getTradeNo());
        CollectionUtil.putIfNotNull(body, "out_trade_no", payParam.getOutTradeNo());
        CollectionUtil.putIfNotNull(body, "out_refund_no", payParam.getRefundNo());
        CollectionUtil.putIfNotNull(body, "total_fee", NumberUtil.yuan2fen(payParam.getMoney()));
        CollectionUtil.putIfNotNull(body, "refund_fee", NumberUtil.yuan2fen(payParam.getRefundAmount()));
        CollectionUtil.putIfNotNull(body, "refund_desc", payParam.getRefundReason());
        return body;
    }

    @Override
    public PayResult query(PayAccount account, PayParam param) {

        PayResult<Map<String, String>> queryResult = new PayResult<>();
        WXPay wxpay = new WXPay(buildConfig(account));
        Map<String, String> resp;
        try {
            resp = wxpay.orderQuery(buildQueryMap(param));
            if ("SUCCESS".equals(resp.get("return_code"))
                    && "SUCCESS".equals(resp.get("result_code"))
                    && "SUCCESS".equals(resp.get("trade_state"))) {
                queryResult.setPayTime(DateTimeUtil.parse(resp.get("time_end"), "yyyyMMddHHmmss"));
                queryResult.setDesc("主动查单-第三方支付成功");
                queryResult.setStatus(PayStatus.SUCCESS);
                queryResult.setTradeNo(resp.get("transaction_id"));
                queryResult.setOrderCode(resp.get("out_trade_no"));
                queryResult.setMoney(new BigDecimal(resp.get("total_fee")).divide(new BigDecimal("100"), 2));
                queryResult.setRawData(resp);
            } else queryResult.setStatus(PayStatus.PROCESS);
            return queryResult;
        } catch (Exception e) {
            throw new RuntimeException(String.format("查询异常:%s", e.getMessage()));
        }
    }

    @Override
    public PayResult refund(PayAccount account, PayParam param) {
        WXPay wxpay = new WXPay(buildConfig(account));
        Map<String, String> resp;
        try {
            resp = wxpay.refund(buildRefundMap(param));

            PayResult payResult = new PayResult();
            if ("SUCCESS".equals(resp.get("return_code")) && "SUCCESS".equals(resp.get("result_code"))) {
                payResult.setStatus(PayStatus.SUCCESS);
            } else {
                payResult.setStatus(PayStatus.FAIL);
                payResult.setDesc(String.format("退款申请失败->状态[%s] 原因[%s]", resp.get("return_code"),
                        resp.get("return_msg")));
            }
            payResult.setPayTime(LocalDateTime.now());
            return payResult;
        } catch (Exception e) {
            throw new RuntimeException(String.format("退款异常:%s", e.getMessage()));
        }
    }

    @Override
    public PayResult<Map<String, String>> notify(PayAccount account, Object notifyParam) {
        WXPay wxpay = new WXPay(buildConfig(account));
        Map<String, String> rawData;
        boolean check;
        try {
            rawData = WXPayUtil.xmlToMap((String) notifyParam);
            check = wxpay.isPayResultNotifySignatureValid(rawData);
        } catch (Exception e) {
            throw new RuntimeException(String.format("回调参数校验异常:%s", e.getMessage()));
        }
        //处理返回结果
        PayResult<Map<String, String>> payResult = new PayResult<>();
        payResult.setRawData(rawData);
        payResult.setCheck(check);
        String resultCode = rawData.get("result_code");
        if ("SUCCESS".equals(resultCode) || "".equals(resultCode)) {
            payResult.setStatus(PayStatus.SUCCESS);
            payResult.setOrderCode(rawData.get("out_trade_no"));
            String timeEnd = rawData.get("time_end");
            //组织支付时间
            payResult.setPayTime(DateTimeUtil.parse(timeEnd, "yyyyMMddHHmmss"));
            payResult.setTradeNo(rawData.get("transaction_id"));
            payResult.setDesc("支付成功");
            //组织支付金额，是分
            payResult.setMoney(new BigDecimal(rawData.get("total_fee")).divide(new BigDecimal("100"), 2));
        } else if ("FAIL".equals(resultCode)) {
            payResult.setStatus(PayStatus.FAIL);
            payResult.setDesc(String.format("支付错误码[%s] 错误信息[%s]", rawData.get("err_code"), rawData.get("err_code_des")));
        } else {
            throw new RuntimeException(String.format("未知的支付resultCode:%s", resultCode));
        }
        return payResult;
    }
}
