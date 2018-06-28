package com.pay;


import com.pay.common.dict.PayMethod;
import com.pay.extended.PayApi;
import com.pay.extended.ali.AliPay_Mobile_V1;
import com.pay.extended.ali.AliPay_Wap_V1;
import com.pay.extended.wx.WxPay_Mobile_V1;
import com.pay.extended.wx.WxPay_Public_V1;

public class PayUtil {


    public static PayApi getApi(Integer payMethod) {
        switch (payMethod) {
            case PayMethod.ALI_MOBILE_V1:
                return new AliPay_Mobile_V1();
            case PayMethod.ALI_WAP_V1:
                return new AliPay_Wap_V1();
            case PayMethod.WX_MOBILE_V1:
                return new WxPay_Mobile_V1();
            case PayMethod.WX_PUBLIC_V1:
                return new WxPay_Public_V1();
            default:
                throw new RuntimeException(String.format("未知的支付方式[%d]", payMethod));
        }
    }


}
