package com.trevor.web.controller.login;


import com.trevor.bo.JsonEntity;
import com.trevor.bo.ResponseHelper;
import com.trevor.bo.WebKeys;
import com.trevor.common.MessageCodeEnum;
import com.trevor.domain.User;
import com.trevor.service.user.UserService;
import com.trevor.util.CookieUtils;
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
    private HttpServletResponse response;

    @Resource
    private HttpServletRequest request;

    @Resource
    private UserService userService;

    @ApiOperation("获取登录用户")
    @RequestMapping(value = "/api/login/user", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    protected JsonEntity<User> getLoginUser() {
        String opendi = CookieUtils.getOpenid(request);
        User webSessionUser = userService.findUserByOpenid(opendi);
        return ResponseHelper.createInstance(webSessionUser ,MessageCodeEnum.HANDLER_SUCCESS);
    }

    @ApiOperation("退出登录")
    @RequestMapping(value = "/api/login/out", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    protected JsonEntity<Object> loginOut() {
        //删除cookie即可
        CookieUtils.delete(WebKeys.TOKEN ,response);
        return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.LOGIN_OUT_SUCCESS);
    }
}
