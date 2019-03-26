package com.trevor.service.xiaoliao;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.ResponseHelper;
import com.trevor.bo.WebKeys;
import com.trevor.common.MessageCodeEnum;
import com.trevor.dao.PersonalCardMapper;
import com.trevor.domain.PersonalCard;
import com.trevor.domain.User;
import com.trevor.service.user.UserService;
import com.trevor.util.RandomUtils;
import com.trevor.util.XianliaoAuthUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author trevor
 * @date 03/21/19 18:24
 */
@Service
@Slf4j
public class XianliaoServiceImpl implements XianliaoService{

    @Resource
    private UserService userService;

    @Resource
    private PersonalCardMapper personalCardMapper;

    /**
     * 根据code获取闲聊用户基本信息
     * @return
     */
    @Override
    public JsonEntity<Map<String, Object>> weixinAuth(String code) throws IOException {
        //获取access_token
        Map<String, String> accessTokenMap = XianliaoAuthUtils.getXianliaoToken(code);
        //拉取用户信息
        Map<String, String> userInfoMap = XianliaoAuthUtils.getUserInfo(accessTokenMap.get(WebKeys.ACCESS_TOKEN));
        //有可能access token以被使用
        if (Objects.equals(WebKeys.SUCCESS ,userInfoMap.get(WebKeys.ERRMSG))) {
            log.error("拉取用户信息 失败啦,快来围观:-----------------" + userInfoMap.get(WebKeys.ERRMSG));
            //刷新access_token
            Map<String, String> accessTokenByRefreshTokenMap = XianliaoAuthUtils.getXianliaoTokenByRefreshToken(accessTokenMap.get(WebKeys.REFRESH_TOKEN));
            // 再次拉取用户信息
            userInfoMap = XianliaoAuthUtils.getUserInfo(accessTokenByRefreshTokenMap.get(WebKeys.ACCESS_TOKEN));
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
                //新增
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
    private User generateUser(String hash , Map<String, String> userInfoMap){
        User user = new User();
        user.setOpenid(userInfoMap.get(WebKeys.OPEN_ID));
        user.setAppName(userInfoMap.get("nickName"));
        user.setAppPictureUrl(userInfoMap.get("smallAvatar"));
        user.setHash(hash);
        user.setType(1);
        user.setFriendManageFlag(0);
        return user;
    }
}
