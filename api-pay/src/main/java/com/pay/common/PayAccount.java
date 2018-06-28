package com.pay.common;

import lombok.Data;

import java.io.InputStream;

@Data
public class PayAccount {
    //公共
    private String charset;

    private String notifyUrl;
    //支付宝
    private String aliGateWay;

    private String aliAppId;

    private String aliAppPrivateKey;

    private String aliAppPublicKey;

    private String aliPublicKey;

    private String aliDataType;

    private String aliSignType;
    //微信
    private String wxAppID;

    private String wxMchID;

    private String wxKey;

    private int httpConnectTimeoutMs = 8000;
    private int httpReadTimeoutMs = 10000;

    private InputStream certStream;

    @Override
    protected void finalize() throws Throwable {
        try {
            super.finalize();
        } finally {
            if (certStream != null) {
                certStream.close();
            }
        }

    }
}
