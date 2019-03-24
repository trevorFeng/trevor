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
public class TestLoginController {

    @Resource
    private UserService userService;

    @RequestMapping(value = "/api/testLogin/login", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> weixinAuth(HttpServletRequest request, HttpServletResponse response){
        String hash = RandomUtils.getRandomChars(10);
        Map<String, Object> claims = new HashMap<>(2<<4);
        claims.put("hash", hash);
        claims.put("openid", "1");
        claims.put("timestamp", System.currentTimeMillis());
        String token = TokenUtil.generateToken(claims);

        Boolean existByOpnenId = userService.isExistByOpnenId("1");
        User user;
        if (!existByOpnenId) {
            user = new User();
            user.setOpenid("1");
            user.setHash(hash);
            user.setAppName("name");
            user.setAppPictureUrl("tupianlianjie");
            user.setType(1);
            user.setFriendManageFlag(0);
            userService.insertOne(user);
        }

        user = userService.findUserByOpenIdContainIdAndAppNameAndPicture("1");

        WebSessionUser webSessionUser = new WebSessionUser(user);

        CookieUtils.add(WebKeys.TOKEN ,token ,response);
        CookieUtils.add(WebKeys.COOKIE_USER_INFO , JSON.toJSONString(webSessionUser) ,response);

        return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.HANDLER_SUCCESS);

    }
}
