package com.wechat;

import com.common.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class PageAuthUtil {


    private static final String BASE_URL = "https://api.weixin.qq.com/sns/";
    private static final String OAUTH2_URL = BASE_URL + "oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
    private static final String USERINFO_URL = BASE_URL + "userinfo?access_token=%s&openid=%s";

    public static WXUserInfo getUserInfo(String appid, String secret, String code) {
        String responseText;
        String url = String.format(OAUTH2_URL, appid, secret, code);
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            WXUserInfo user = new WXUserInfo();
            HttpEntity entity = httpclient.execute(new HttpGet(url)).getEntity();
            if (entity != null) {
                responseText = EntityUtils.toString(entity, "UTF-8");
                JsonNode jsonNode = JSONUtil.getObjectMapper().readTree(responseText);
                user.setAccessToken(jsonNode.get("access_token") == null ? null : jsonNode.get("access_token").textValue());
                user.setOpenId(jsonNode.get("openid") == null ? null : jsonNode.get("openid").textValue());
            }
            if (user.getUnionId() != null) {
                url = String.format(USERINFO_URL, user.getAccessToken(), user.getOpenId());
                entity = httpclient.execute(new HttpGet(url)).getEntity();
                if (entity != null) {
                    responseText = EntityUtils.toString(entity, "UTF-8");
                    JsonNode jsonNode = JSONUtil.getObjectMapper().readTree(responseText);
                    user.setNickName(jsonNode.get("nickname") == null ? null : jsonNode.get("nickname").textValue());
                    user.setUnionId(jsonNode.get("unionid") == null ? null : jsonNode.get("unionid").textValue());
                }
            }
            return user;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Data
    public static class WXUserInfo {
        private String accessToken;
        private String openId;
        private String unionId;
        private String nickName;
    }
}
