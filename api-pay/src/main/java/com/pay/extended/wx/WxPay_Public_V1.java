package com.pay.extended.wx;

import com.common.json.JSONUtil;
import com.github.wxpay.sdk.WXPayConfig;
import com.github.wxpay.sdk.WXPayUtil;
import com.pay.common.PayAccount;
import com.pay.common.PayParam;

import java.util.HashMap;
import java.util.Map;

public class WxPay_Public_V1 extends WxPay {


    @Override
    public String pay(PayAccount account, PayParam param) {
        WXPayConfig config = buildConfig(account);
        Map<String, String> payResults = pay(buildConfig(account), buildPayMap(param), "JSAPI");
        Map<String, String> appResults = new HashMap<>();
        appResults.put("appId", config.getAppID());
        appResults.put("timeStamp", timeStamp());
        appResults.put("nonceStr", WXPayUtil.generateNonceStr());
        appResults.put("package", "prepay_id=" + payResults.get("prepay_id"));
        appResults.put("signType", "MD5");
        appResults.put("paySign", sign(appResults, config.getKey()));
        return JSONUtil.toStr(appResults);

    }


}
