package com.trevor.web.controller.api;

import com.trevor.bo.*;
import com.trevor.service.weixin.WeixinService;
import com.trevor.util.CookieUtils;
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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-14 0:56
 **/
@Api(value = "微信授权" ,description = "微信授权")
@RestController
@Slf4j
public class WeixinAuthController {

    @Resource
    private WeixinService weixinService;

    @ApiOperation("微信授权回调地址")
    @RequestMapping(value = "/api/weixin/auth", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void weixinAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = request.getParameter(WebKeys.CODE);
        String uuid = request.getParameter(WebKeys.UUID);
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        if (uuid == null) {
            return;
        }
        JsonEntity<Map<String, Object>> jsonEntity = weixinService.weixinAuth(code);

        if(jsonEntity.getCode() < 0){
            log.error(jsonEntity.getMessage());
        }else {
            //生成token
            Map<String, Object> map = jsonEntity.getData();
            String token = TokenUtil.generateToken(map);
            TempUser tempUser = new TempUser("1" ,token ,(String) map.get(WebKeys.OPEN_ID));
            request.getServletContext().setAttribute(uuid,tempUser);
        }
    }
}
