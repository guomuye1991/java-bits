package com.sms;

import lombok.Data;

import java.util.Map;
@Data
public class SmsProperties {

    private VerificationCode verificationCode;

    private Ali ali;

    @Data
    public static class Ali {

        private String product;
        private String domain;
        private String accessKeyId;
        private String accessKeySecret;
        private String signName;

        private Map<String,String> template;
    }

    @Data
    public static class VerificationCode {
        private byte count;
        private long time;
        private byte bit;
    }

}
