package com.trevor.service.weixin;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.ResponseHelper;
import com.trevor.bo.WebKeys;
import com.trevor.common.MessageCodeEnum;
import com.trevor.dao.PersonalCardMapper;
import com.trevor.domain.PersonalCard;
import com.trevor.domain.User;
import com.trevor.service.user.UserService;
import com.trevor.util.RandomUtils;
import com.trevor.util.WeixinAuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author trevor
 * @date 2019/3/4 11:38
 */
@Service
@Slf4j
public class WeixinServiceImpl implements WeixinService {

    @Resource
    private UserService userService;

    @Resource
    private PersonalCardMapper personalCardMapper;

    @Override
    public JsonEntity<Map<String, Object>> weixinAuth(String code) throws IOException {
        //获取access_token
        Map<String, String> accessTokenMap = WeixinAuthUtils.getWeixinToken(code);
        //拉取用户信息
        Map<String, String> userInfoMap = WeixinAuthUtils.getUserInfo(accessTokenMap.get(WebKeys.ACCESS_TOKEN)
                ,accessTokenMap.get(WebKeys.OPEN_ID));
        //有可能access token以被使用
        if (userInfoMap.get(WebKeys.ERRCODE) != null) {
            log.error("拉取用户信息 失败啦,快来围观:-----------------" + userInfoMap.get(WebKeys.ERRMSG));
            //刷新access_token
            Map<String, String> accessTokenByRefreshTokenMap = WeixinAuthUtils.getWeixinTokenByRefreshToken(accessTokenMap.get(WebKeys.REFRESH_TOKEN));
            // 再次拉取用户信息
            userInfoMap = WeixinAuthUtils.getUserInfo(accessTokenByRefreshTokenMap.get(WebKeys.ACCESS_TOKEN),
                    accessTokenByRefreshTokenMap.get(WebKeys.OPEN_ID));
        }
        String openid = userInfoMap.get(WebKeys.OPEN_ID);
        if (openid == null) {
            return ResponseHelper.withErrorInstance(MessageCodeEnum.AUTH_FAILED);
        } else {
            //生成10位hash
            String hash = RandomUtils.getRandomChars(10);
            Map<String, Object> claims = new HashMap<>(2<<4);
            claims.put("hash", hash);
            claims.put("openid", openid);
            claims.put("timestamp", System.currentTimeMillis());
            //判断用户是否存在
            Boolean isExist = userService.isExistByOpnenId(openid);
            if (!isExist) {
                //新增用户
                User user = generateUser(hash ,userInfoMap);
                userService.insertOne(user);
                //新增用户房卡记录
                PersonalCard personalCard = new PersonalCard();
                personalCard.setUserId(user.getId());
                personalCard.setRoomCardNum(0);
                personalCardMapper.insertOne(personalCard);
            } else {
                //更新hash
                userService.updateHash(hash ,openid);
            }
            return ResponseHelper.createInstance(claims ,MessageCodeEnum.AUTH_SUCCESS);
        }
    }

    /**
     * 生成一个user
     * @return
     */
    private User generateUser(String hash ,Map<String, String> userInfoMap){
        User user = new User();
        user.setOpenid(userInfoMap.get("openid"));
        user.setAppName(userInfoMap.get("nickname"));
        //用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空
        user.setAppPictureUrl(userInfoMap.get("headimgurl"));
        user.setHash(hash);
        user.setType(0);
        user.setFriendManageFlag(0);
        return user;
    }
}
