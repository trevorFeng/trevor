package com.trevor.web.controller.login;


import com.trevor.bo.JsonEntity;
import com.trevor.bo.LoginUser;
import com.trevor.bo.ResponseHelper;
import com.trevor.bo.WebKeys;
import com.trevor.common.MessageCodeEnum;
import com.trevor.domain.User;
import com.trevor.service.PersonalCardService;
import com.trevor.service.user.UserService;
import com.trevor.util.CookieUtils;
import com.trevor.util.ThreadLocalUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-14 0:56
 **/
@Api(value = "登出和获取登录用户" ,description = "登出和获取登录用户")
@RestController
public class LoginController {

    @Resource
    private UserService userService;

    @Resource
    private PersonalCardService personalCardService;

    @ApiOperation("获取登录用户")
    @RequestMapping(value = "/api/login/user", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    protected JsonEntity<LoginUser> getLoginUser() {
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        Integer cardNum = personalCardService.findCardNumByUserId(user.getId());

        LoginUser loginUser = new LoginUser();
        loginUser.setId(user.getId());
        loginUser.setAppName(user.getAppName());
        loginUser.setAppPictureUrl(user.getAppPictureUrl());
        loginUser.setFriendManageFlag(user.getFriendManageFlag());
        loginUser.setCardNum(cardNum);

        JsonEntity<LoginUser> jsonEntity = ResponseHelper.createInstance(loginUser ,MessageCodeEnum.HANDLER_SUCCESS);
        ThreadLocalUtil.getInstance().remove();
        return jsonEntity;
    }

    @ApiOperation("退出登录")
    @RequestMapping(value = "/api/login/out", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    protected JsonEntity<Object> loginOut() {
        //删除hash即可
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        userService.loginOut(user.getId());
        JsonEntity<Object> jsonEntity = ResponseHelper.createInstanceWithOutData(MessageCodeEnum.LOGIN_OUT_SUCCESS);
        ThreadLocalUtil.getInstance().remove();
        return jsonEntity;
    }
}
