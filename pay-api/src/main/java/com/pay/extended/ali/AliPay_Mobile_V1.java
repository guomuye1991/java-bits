package com.pay.extended.ali;


public class AliPay_Mobile_V1 extends AliPay {

    @Override
    String getProductCode() {
        return "QUICK_MSECURITY_PAY";
    }
}
