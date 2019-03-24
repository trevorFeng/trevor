package com.trevor.web.controller.login;


import com.alibaba.fastjson.JSON;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.trevor.bo.JsonEntity;
import com.trevor.bo.ResponseHelper;
import com.trevor.bo.WebSessionUser;
import com.trevor.bo.WebKeys;
import com.trevor.common.MessageCodeEnum;
import com.trevor.domain.User;
import com.trevor.service.user.UserService;
import com.trevor.util.CookieUtils;
import com.trevor.util.RandomUtils;
import com.trevor.util.SessionUtil;
import com.trevor.util.TokenUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-14 0:56
 **/
@Api(value = "测试用暂时登录" ,description = "测试用暂时登录")
@RestController
@Slf4j
public class TestLoginController {

    @Resource
    private UserService userService;

    @RequestMapping(value = "/api/testLogin/login", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> weixinAuth(HttpServletRequest request, HttpServletResponse response){
        String token;
        Map<String, Object> claims = new HashMap<>(2<<4);
        Boolean existByOpnenId = userService.isExistByOpnenId("1");
        if (!existByOpnenId) {
            String hash = RandomUtils.getRandomChars(10);
            claims.put("hash", hash);
            claims.put("openid", "1");
            claims.put("timestamp", System.currentTimeMillis());
            token = TokenUtil.generateToken(claims);

            User user = new User();
            user.setOpenid("1");
            user.setHash(hash);
            user.setAppName("name");
            user.setAppPictureUrl("tupianlianjie");
            user.setType(1);
            user.setFriendManageFlag(0);
            userService.insertOne(user);
            log.info("测试登录成功 ，hash值---------" + hash);
        }else {
            User user = userService.findUserByOpenidContainOpenidAndHash("1");
            claims.put("hash", user.getHash());
            claims.put("openid", "1");
            claims.put("timestamp", System.currentTimeMillis());
            token = TokenUtil.generateToken(claims);
            log.info("测试登录成功 ，hash值---------" + user.getHash());
        }
        CookieUtils.add(WebKeys.TOKEN ,token ,response);
        return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.HANDLER_SUCCESS);
    }
}
