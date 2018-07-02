package com.wechat;

import com.common.json.JSONUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PushMsgUtil {

    /**
     * 微信推送
     *
     * @param accessToken
     * @param openId
     * @param templateId
     * @param toUrl
     * @param data
     */
    public static void pushMessageToUser(String accessToken, String openId, String templateId, String toUrl, Map<String, Object> data) {


        String responseText;
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("touser", openId);
        jsonData.put("template_id", templateId);
        jsonData.put("url", toUrl);
        jsonData.put("data", data);
        httppost.setEntity(new ByteArrayEntity(JSONUtil.toJson(jsonData).getBytes(), ContentType.APPLICATION_JSON));
        CloseableHttpResponse response;
        try {
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseText = EntityUtils.toString(entity, "UTF-8");
                LOGGER.info("推送用户消息: {}", responseText);
            }
        } catch (IOException e) {
            LOGGER.error("", e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        }
    }

    public static class MsgItem{

        private String value;
        private String color;

        public MsgItem() {
        }

        public MsgItem(String value, String color) {
            this.value = value;
            this.color = color;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

}
