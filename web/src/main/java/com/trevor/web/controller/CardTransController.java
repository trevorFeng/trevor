package com.trevor.web.controller;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.WebSessionUser;
import com.trevor.domain.CardTrans;
import com.trevor.service.user.UserService;
import com.trevor.service.cardTrans.CardTransService;
import com.trevor.util.CookieUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author trevor
 * @date 2019/3/8 16:50
 */
@Api(value = "房卡相关接口" ,description = "房卡相关接口(领取，创建，查询收到或发出)")
@RestController("/admin")
@Validated
public class CardTransController {

    @Resource
    private CardTransService cardTransService;

    @Resource
    private UserService userService;

    @Resource
    private HttpServletRequest request;


    @ApiOperation(value = "创建房卡包")
    @ApiImplicitParam(name = "cardNum" ,value = "房卡的数量" , required = true ,paramType = "path" ,dataType = "int")
    @RequestMapping(value = "/api/cardTrans/create/package/{cardNum}", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<String> createCardPackage(@PathVariable("cardNum") @Min(value = 1 ,message = "最小值为1") Integer cardNum){
        String opendi = CookieUtils.getOpenid(request);
        WebSessionUser webSessionUser = userService.getWebSessionUserByOpneid(opendi);
        return cardTransService.createCardPackage(cardNum , webSessionUser);
    }

    @ApiOperation(value = "领取房卡包")
    @ApiImplicitParam(name = "transNo" ,value = "交易号" , required = true ,paramType = "path" ,dataType = "string")
    @RequestMapping(value = "/api/cardTrans/receive/package/{transNo}", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> createCardPackage(@PathVariable("transNo") @NotBlank(message = "交易号不能为空") String transNo){
        String opendi = CookieUtils.getOpenid(request);
        WebSessionUser webSessionUser = userService.getWebSessionUserByOpneid(opendi);
        return cardTransService.receiveCardPackage(transNo , webSessionUser);
    }

    @ApiOperation(value = "查询发出的房卡")
    @RequestMapping(value = "/api/cardTrans/send/package", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<List<CardTrans>> findSendCardRecord(){
        String opendi = CookieUtils.getOpenid(request);
        WebSessionUser webSessionUser = userService.getWebSessionUserByOpneid(opendi);
        return cardTransService.findSendCardRecord(webSessionUser);
    }

    @ApiOperation(value = "查询收到的房卡")
    @RequestMapping(value = "/api/cardTrans/query/package", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<List<CardTrans>> findRecevedCardRecord(){
        String opendi = CookieUtils.getOpenid(request);
        WebSessionUser webSessionUser = userService.getWebSessionUserByOpneid(opendi);
        return cardTransService.findRecevedCardRecord(webSessionUser);
    }

}
