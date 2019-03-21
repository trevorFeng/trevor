package com.trevor.web.controller.login;

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

    @ApiOperation("浏览器登录并转发到微信登录页面")
    @RequestMapping(value = "/front/phone/login", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //用户临时凭证
        String uuid = RandomUtils.getRandomChars(40);
        //使用全局变量，微信授权成功后改变值
        TempUser tempUser = new TempUser(AuthEnum.NOT_AUTH.getCode() ,"" ,"");
        req.getServletContext().setAttribute(uuid ,tempUser);
        CookieUtils.add(WebKeys.UUID ,uuid ,resp);
        req.getRequestDispatcher("/view/phonelogin.html?uuid=" + uuid + "&reUrl=" + req.getParameter("reUrl")).forward(req,resp);
    }
}
