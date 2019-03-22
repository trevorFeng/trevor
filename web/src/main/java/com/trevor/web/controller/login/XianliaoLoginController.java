package com.trevor.web.controller.login;


import com.alibaba.fastjson.JSON;
import com.trevor.bo.*;
import com.trevor.common.AuthEnum;
import com.trevor.common.MessageCodeEnum;
import com.trevor.domain.User;
import com.trevor.service.user.UserService;
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
@Api("闲聊登陆相关")
@RestController
public class XianliaoLoginController {

    @Resource
    private UserService userService;

    @Resource
    private HttpServletRequest request;

    @Resource
    private HttpServletResponse response;

    @ApiOperation("闲聊登录并转发到微信登录页面")
    @RequestMapping(value = "/front/xianliao/login", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void login() throws ServletException, IOException {
        //用户临时凭证
        String uuid = RandomUtils.getRandomChars(40);
        //使用全局变量，闲聊授权成功后改变值
        TempUser tempUser = new TempUser(AuthEnum.NOT_AUTH.getCode() ,"" ,"");
        request.getServletContext().setAttribute(uuid ,tempUser);
        CookieUtils.add(WebKeys.UUID ,uuid ,response);
        request.getRequestDispatcher("/view/xianliao.html?uuid=" + uuid + "&reUrl=" + request.getParameter("reUrl"))
                .forward(request,response);
    }

    @ApiOperation("检查闲聊是否授权")
    @RequestMapping(value = "/front/xianliao/login/check", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<WebSessionUser> checkAuth()  {
        String uuid = request.getParameter(WebKeys.UUID);
        //判断闲聊是否已经授权
        TempUser tempUser = (TempUser) request.getServletContext().getAttribute(uuid);
        if(AuthEnum.IS_AUTH.getCode().equals(tempUser.getIsAuth())){
            User user = userService.findUserByOpenIdContainIdAndAppNameAndPicture(tempUser.getOpenid());
            WebSessionUser webSessionUser = new WebSessionUser(user);
            webSessionUser.setId(user.getId());
            webSessionUser.setName(user.getAppName());
            webSessionUser.setPictureUrl(user.getAppPictureUrl());
            //存入cookie
            CookieUtils.add(WebKeys.TOKEN ,tempUser.getToken() ,response);
            CookieUtils.add(WebKeys.COOKIE_USER_INFO , JSON.toJSONString(webSessionUser) ,response);
            return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.AUTH_SUCCESS);
        }else {
            return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.AUTH_FAILED);
        }
    }

}
