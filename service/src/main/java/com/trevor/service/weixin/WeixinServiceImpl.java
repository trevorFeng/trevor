package com.trevor.service.weixin;

import com.trevor.bo.WebKeys;
import com.trevor.bo.JsonEntity;
import com.trevor.bo.SimpleUser;
import com.trevor.service.weixin.bo.WeixinToken;
import com.trevor.util.HttpUtil;
import com.trevor.util.WeixinAuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * @author trevor
 * @date 2019/3/4 11:38
 */
@Service
@Slf4j
public class WeixinServiceImpl implements WeixinService {

    @Override
    public JsonEntity<SimpleUser> queryWeixinUser(String code) throws IOException {
        //获取access_token
        Map<String, String> accessTokenMap = WeixinAuthUtils.getWeixinToken(code);
        //拉取用户信息
        Map<String, String> userinfoMap = WeixinAuthUtils.getUserInfo(accessTokenMap.get(WebKeys.ACCESS_TOKEN)
                ,accessTokenMap.get(WebKeys.OPEN_ID));
        //有可能access token以被使用
        if (userinfoMap.get(WebKeys.ERRCODE) != null) {
            log.error("拉取用户信息 失败啦,快来围观:------------------------------" + userinfoMap.get(WebKeys.ERRMSG));
            //刷新access_token
            Map<String, String> accessTokenByRefreshTokenMap = WeixinAuthUtils.getWeixinTokenByRefreshToken(accessTokenMap.get(WebKeys.REFRESH_TOKEN));
            // 再次拉取用户信息
            userinfoMap = WeixinAuthUtils.getUserInfo(accessTokenByRefreshTokenMap.get(WebKeys.ACCESS_TOKEN),
                    accessTokenByRefreshTokenMap.get(WebKeys.OPEN_ID));
        }

        return null;
    }
}
