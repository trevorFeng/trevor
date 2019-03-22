package com.trevor.web.controller.login;

import com.alibaba.fastjson.JSON;
import com.trevor.bo.JsonEntity;
import com.trevor.bo.ResponseHelper;
import com.trevor.bo.WebKeys;
import com.trevor.bo.WebSessionUser;
import com.trevor.common.MessageCodeEnum;
import com.trevor.service.BrowserLogin.BrowserLoginService;
import com.trevor.util.CookieUtils;
import com.trevor.web.controller.login.bo.PhoneCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Pattern;
import java.util.Objects;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-14 0:56
 **/
@Api("浏览器登录相关")
@RestController
@Validated
public class BrowserLoginController {

    @Resource
    private BrowserLoginService browserLoginService;

    @Resource
    private HttpServletRequest request;

    @Resource
    private HttpServletResponse response;

    @ApiOperation("生成验证码,给用户发送验证码")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "path", name = "phoneNum", dataType = "string", required = true, value = "phoneNum")})
    @RequestMapping(value = "/front/phone/send/{phoneNum}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<String> sendCode(@PathVariable("phoneNum") @Pattern (regexp = "^[0-9]{11}$" ,message = "手机号格式不正确") String phoneNum){
        JsonEntity<String> stringJsonEntity = browserLoginService.generatePhoneCode(phoneNum);
        if (stringJsonEntity.getCode() < 0) {
            return stringJsonEntity;
        }
        String code = stringJsonEntity.getData();
        request.getServletContext().setAttribute(phoneNum ,code);
        return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.CREATE_SUCCESS);
    }

    @ApiOperation("校验用户的验证码是否正确")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "body", name = "phoneCode", dataType = "PhoneCode", required = true, value = "phoneCode")})
    @RequestMapping(value = "/front/phone/submit", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<String> submit(@RequestBody @Validated PhoneCode phoneCode){
        //校验验证码是否正确
        String code = (String) request.getServletContext().getAttribute(phoneCode.getPhoneNum());
        if (Objects.equals(code ,phoneCode.getCode())) {
            JsonEntity<WebSessionUser> result = browserLoginService.getWebSessionUserByPhoneNum(phoneCode.getPhoneNum());
            CookieUtils.add(WebKeys.COOKIE_USER_INFO , JSON.toJSONString(result.getData()) ,response);
            return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.CREATE_SUCCESS);
        }else {
            return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.CODE_ERROR);
        }
    }
}
