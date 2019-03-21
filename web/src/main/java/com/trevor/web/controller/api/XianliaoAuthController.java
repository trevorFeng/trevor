package com.trevor.web.controller.api;


import com.trevor.bo.JsonEntity;
import com.trevor.bo.TempUser;
import com.trevor.bo.WebKeys;
import com.trevor.common.AuthEnum;
import com.trevor.service.weixin.WeixinService;
import com.trevor.util.TokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-14 0:56
 **/
@Api(value = "闲聊回调的地址" ,description = "闲聊回调的地址")
@RestController
public class XianliaoAuthController {

    @Resource
    private WeixinService weixinService;

    @Resource
    private HttpServletRequest request;

    @ApiOperation("闲聊回调的地址")
    @RequestMapping(value = "/api/weixin/auth", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void weixinAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }
}
