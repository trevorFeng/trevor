package com.trevor.web.controller.login;


import com.trevor.bo.TempUser;
import com.trevor.util.CookieUtils;
import com.trevor.util.RandomUtils;
import domain.Result;
import io.swagger.annotations.Api;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Api("微信登陆相关")
@RestController
public class WeixinLoginController{

    @RequestMapping(value = "/front/weixin/login", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //用户临时凭证
        String uuid = RandomUtils.getRandomChars(40);
        //使用全局变量
        TempUser tempUser = new TempUser("0" ,"");
        req.getServletContext().setAttribute(uuid ,tempUser);
        CookieUtils.add("uuid" ,uuid ,resp);
        req.getRequestDispatcher("/view/weixinlogin.html?uuid=" + uuid + "&reUrl=" + req.getParameter("reUrl")).forward(req,resp);
    }

    @RequestMapping(value = "/front/weixin/login/check", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void checkAuth(HttpServletRequest request, HttpServletResponse response)  {
        Map<String,Object> map = new HashMap<String,Object>(2<<4);
        String uuid = request.getParameter("uuid");
        //判断微信是否已经授权
        TempUser tempUser = (TempUser) request.getServletContext().getAttribute(uuid);

        if("1".equals(info.get("isAuth"))){
            map.put("isAuth",true);
            CookieUtils.add("token",info.get("token"),response);
            Result.genSuccessResult(map,response);
        }else {
            map.put("isAuth",false);
            Result.genSuccessResult(map,response);
        }
        if (Objects.equals("1" ,tempUser.getIsAuth())) {

        }
    }
}
