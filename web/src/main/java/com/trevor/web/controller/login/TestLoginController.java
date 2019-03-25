package com.trevor.web.controller.login;


import com.trevor.bo.JsonEntity;
import com.trevor.bo.ResponseHelper;
import com.trevor.bo.WebKeys;
import com.trevor.common.MessageCodeEnum;
import com.trevor.dao.PersonalCardMapper;
import com.trevor.domain.PersonalCard;
import com.trevor.domain.User;
import com.trevor.service.user.UserService;
import com.trevor.util.CookieUtils;
import com.trevor.util.RandomUtils;
import com.trevor.util.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

    @Resource
    private PersonalCardMapper personalCardMapper;

    @ApiOperation("只需点一下就可以登录了，转到/api/login/user获取用户信息")
    @RequestMapping(value = "/api/testLogin/login", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> weixinAuth(HttpServletRequest request, HttpServletResponse response){
        Long time = System.currentTimeMillis();
        String openid = time + "";
        String hash = RandomUtils.getRandomChars(10);

        Map<String, Object> claims = new HashMap<>(2<<4);
        claims.put("hash", hash);
        claims.put("openid", openid);
        claims.put("timestamp", time);

        String token = TokenUtil.generateToken(claims);
        CookieUtils.add(WebKeys.TOKEN ,token ,response);

        User user = new User();
        user.setOpenid(openid);
        user.setHash(hash);
        user.setAppName("登录测试名字");
        user.setAppPictureUrl("https://github.com/redHairChasingTheBeautifulYouth/Java-Guide/blob/master/imgs/20181101-1.jpg");
        user.setType(1);
        user.setFriendManageFlag(0);
        userService.insertOne(user);
        log.info("测试登录成功 ，hash值---------" + hash);

        PersonalCard personalCard = new PersonalCard();
        personalCard.setUserId(user.getId());
        personalCard.setRoomCardNum(0);

        personalCardMapper.insertOne(personalCard);

        return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.HANDLER_SUCCESS);
    }
}
