package com.trevor.web.controller.login;

import com.trevor.bo.WebKeys;
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
@Api(value = "微信回调的地址" ,description = "微信回调的地址")
@RestController
public class WeixinAuthController {

    @RequestMapping(value = "/api/weixin/auth", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void weixinAuth(HttpServletRequest request, HttpServletResponse response){
        String code = request.getParameter(WebKeys.CODE);

    }
}
