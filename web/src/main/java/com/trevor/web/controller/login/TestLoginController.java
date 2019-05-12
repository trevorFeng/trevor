package com.trevor.web.controller.login;


import com.google.common.collect.Maps;
import com.trevor.bo.JsonEntity;
import com.trevor.bo.ResponseHelper;
import com.trevor.common.MessageCodeEnum;
import com.trevor.dao.PersonalCardMapper;
import com.trevor.domain.PersonalCard;
import com.trevor.domain.User;
import com.trevor.service.user.UserService;
import com.trevor.util.RandomUtils;
import com.trevor.util.SessionUtil;
import com.trevor.util.TokenUtil;
import com.trevor.web.controller.login.bo.TestLogin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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

    @Resource
    private PersonalCardMapper personalCardMapper;

    @ApiOperation("只需点一下就可以登录了，转到/api/login/user获取用户信息")
    @RequestMapping(value = "/api/testLogin/login", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<TestLogin> weixinAuth(){
        String openid = System.currentTimeMillis() + "";
        String hash = RandomUtils.getRandomChars(20);

        User user = new User();
        user.setOpenid(openid);
        user.setHash(hash);
        user.setAppName("登录测试名字");
        user.setAppPictureUrl("https://raw.githubusercontent.com/redHairChasingTheBeautifulYouth/Java-Guide/master/imgs/20181101-1.jpg");
        user.setType(1);
        user.setFriendManageFlag(0);
        userService.insertOne(user);
        log.info("测试登录成功 ，hash值---------" + hash);

        PersonalCard personalCard = new PersonalCard();
        personalCard.setUserId(user.getId());
        personalCard.setRoomCardNum(0);

        personalCardMapper.insertOne(personalCard);


        Map<String, Object> claims = Maps.newHashMap();
        claims.put("openid" ,openid);
        claims.put("hash" ,hash);
        claims.put("timestamp" ,System.currentTimeMillis());
        String token = TokenUtil.generateToken(claims);

        TestLogin testLogin = new TestLogin();
        testLogin.setToken(token);
        testLogin.setUserId(user.getId());
        return ResponseHelper.createInstance(testLogin ,MessageCodeEnum.HANDLER_SUCCESS);
    }
}
