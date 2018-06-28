package com.pay.extended;


import com.pay.common.PayAccount;
import com.pay.common.PayParam;
import com.pay.common.PayResult;

import java.util.Map;

public interface PayApi {

    String pay(PayAccount account, PayParam param);

    PayResult query(PayAccount account, PayParam param);

    PayResult refund(PayAccount account, PayParam param);

    PayResult<Map<String, String>> notify(PayAccount account, Object notifyParam);


}
