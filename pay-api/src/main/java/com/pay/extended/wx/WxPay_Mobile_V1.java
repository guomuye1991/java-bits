package com.pay.extended.wx;

import com.common.json.JSONUtil;
import com.github.wxpay.sdk.WXPayConfig;
import com.github.wxpay.sdk.WXPayUtil;
import com.pay.common.PayAccount;
import com.pay.common.PayParam;

import java.util.HashMap;
import java.util.Map;

public class WxPay_Mobile_V1 extends WxPay {


    @Override
    public String pay(PayAccount account, PayParam param) {
        WXPayConfig config = buildConfig(account);
        Map<String, String> payResults = pay(buildConfig(account), buildPayMap(param), "APP");
        Map<String, String> appResults = new HashMap<>();
        appResults.put("appid", config.getAppID());
        appResults.put("partnerid", config.getMchID());
        appResults.put("prepayid", payResults.get("prepay_id"));
        appResults.put("package", "Sign=WXPay");
        appResults.put("noncestr", WXPayUtil.generateNonceStr());
        appResults.put("timestamp", timeStamp());
        appResults.put("sign", sign(appResults, config.getKey()));
        return JSONUtil.toStr(appResults);

    }


}
