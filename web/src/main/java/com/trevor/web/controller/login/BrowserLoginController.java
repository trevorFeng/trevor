package com.trevor.web.controller.login;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.TempUser;
import com.trevor.bo.WebKeys;
import com.trevor.common.AuthEnum;
import com.trevor.dao.UserMapper;
import com.trevor.util.CookieUtils;
import com.trevor.util.RandomUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-14 0:56
 **/
@Api("浏览器登录相关")
@RestController
public class BrowserLoginController {

    @Resource
    private UserMapper userMapper;

    @ApiOperation("生成验证码,给用户发送验证码")
    @RequestMapping(value = "/front/phone/login", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> login(String phoneNum){

        return null;
    }
}
