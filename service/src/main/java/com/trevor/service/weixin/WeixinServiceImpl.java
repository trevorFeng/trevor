package com.trevor.service.weixin;

import com.trevor.service.bo.JsonEntity;
import com.trevor.service.bo.SimpleUser;
import com.trevor.service.weixin.bo.WebKeys;
import com.trevor.service.weixin.bo.WeixinToken;
import com.trevor.service.util.HttpUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author trevor
 * @date 2019/3/4 11:38
 */
@Service
public class WeixinServiceImpl implements WeixinService {
    /**
     * 请求微信token的基本url
     */
    private final static String ACCESS_TOKEN_BASE_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
            + WebKeys.APPID + "&secret=" + WebKeys.APP_SECRET + "&grant_type"+WebKeys.GRANT_TYPE;

    @Override
    public JsonEntity<SimpleUser> queryWeixinUser(String code) throws IOException {
        String url = ACCESS_TOKEN_BASE_URL + "&code=" + code;
        WeixinToken weixinToken = HttpUtil.httpGet(url , WeixinToken.class);

        return null;
    }
}
