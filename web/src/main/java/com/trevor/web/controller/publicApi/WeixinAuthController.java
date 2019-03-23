package com.trevor.web.controller.publicApi;

import com.trevor.bo.*;
import com.trevor.common.AuthEnum;
import com.trevor.service.weixin.WeixinService;
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
import java.io.IOException;
import java.util.Map;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-14 0:56
 **/
@Api(value = "微信回调" ,description = "微信回调")
@RestController
@Slf4j
public class WeixinAuthController {

    @Resource
    private WeixinService weixinService;

    @Resource
    private HttpServletRequest request;

    @ApiOperation("微信授权回调地址")
    @RequestMapping(value = "/public/api/weixin/auth", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void weixinAuth() throws IOException {
        String code = request.getParameter(WebKeys.CODE);
        String uuid = request.getParameter(WebKeys.UUID);
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
            TempUser tempUser = new TempUser(AuthEnum.IS_AUTH.getCode() ,token ,(String) map.get(WebKeys.OPEN_ID));
            request.getServletContext().setAttribute(uuid,tempUser);
        }
    }
}
