package com.trevor.web.controller.login;


import com.trevor.bo.WebSessionUser;
import com.trevor.bo.WebKeys;
import com.trevor.util.SessionUtil;
import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-14 0:56
 **/
@Api(value = "测试用暂时登录" ,description = "测试用暂时登录")
@RestController
public class TestLoginController {

    @RequestMapping(value = "/api/testLogin/login", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void weixinAuth(HttpServletRequest request, HttpServletResponse response){
        WebSessionUser webSessionUser = new WebSessionUser();
        webSessionUser.setId(1L);
        webSessionUser.setName("test_name");
        SessionUtil.getSession().setAttribute(WebKeys.SESSION_USER_KEY , webSessionUser);
    }
}
