package com.trevor.web.controller.person;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.WebSessionUser;
import com.trevor.domain.CardTrans;
import com.trevor.service.user.UserService;
import com.trevor.service.cardTrans.CardTransService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author trevor
 * @date 2019/3/8 16:50
 */
@Api(value = "房卡相关接口" ,description = "房卡相关接口(领取，创建，查询收到或发出)")
@RestController("/admin")
public class CardTransController {

    @Resource
    private CardTransService cardTransService;

    @Resource
    private UserService userService;

    @Resource
    private HttpServletRequest request;

    /**
     * 创建房卡包
     * @param cardNum
     * @return
     */
    @ApiOperation(value = "创建房卡包")
    @ApiImplicitParam(name = "cardNum" ,value = "房卡的数量" , required = true ,paramType = "body" ,dataType = "Integer")
    @RequestMapping(value = "/api/cardTrans/create/package", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<String> createCardPackage(Integer cardNum){
        WebSessionUser webSessionUser = userService.getUserByCookie(request);
        return cardTransService.createCardPackage(cardNum , webSessionUser);
    }

    /**
     * 领取房卡包
     * @param transNo
     * @return
     */
    @ApiOperation(value = "领取房卡包")
    @ApiImplicitParam(name = "cardNum" ,value = "交易号" , required = true ,paramType = "body" ,dataType = "string")
    @RequestMapping(value = "/api/cardTrans/receive/package", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> createCardPackage(String transNo){
        WebSessionUser webSessionUser = userService.getUserByCookie(request);
        return cardTransService.receiveCardPackage(transNo , webSessionUser);
    }

    /**
     * 查询发出的房卡
     * @return
     */
    @ApiOperation(value = "查询发出的房卡")
    @RequestMapping(value = "/api/cardTrans/send/package", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<List<CardTrans>> findSendCardRecord(){
        WebSessionUser webSessionUser = userService.getUserByCookie(request);
        return cardTransService.findSendCardRecord(webSessionUser);
    }

    /**
     * 查询收到的房卡
     * @return
     */
    @ApiOperation(value = "查询收到的房卡")
    @RequestMapping(value = "/api/cardTrans/query/package", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<List<CardTrans>> findRecevedCardRecord(){
        WebSessionUser webSessionUser = userService.getUserByCookie(request);
        return cardTransService.findRecevedCardRecord(webSessionUser);
    }

}
