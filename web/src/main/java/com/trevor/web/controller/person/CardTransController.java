package com.trevor.web.controller.person;

import com.trevor.bo.JsonEntity;
import com.trevor.service.cardTrans.CardTransService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author trevor
 * @date 2019/3/8 16:50
 */
@RestController("/admin")
public class CardTransController {

    @Resource
    private CardTransService cardTransService;

    /**
     * 创建房卡包
     * @param cardNum
     * @return
     */
    @RequestMapping(value = "/api/cardTrans/create/package", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<String> createCardPackage(Integer cardNum ){
       return cardTransService.createCardPackage(cardNum ,null);
    }

    /**
     * 领取房卡包
     * @param transNo
     * @return
     */
    @RequestMapping(value = "/api/cardTrans/receive/package", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> createCardPackage(String transNo ){
        return cardTransService.receiveCardPackage(transNo ,null);
    }

    /**
     * 查询发出的房卡
     * @param transNo
     * @return
     */
    @RequestMapping(value = "/api/cardTrans/send/package", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> findSendCardRecord(String transNo){
        return cardTransService.receiveCardPackage(transNo ,null);
    }


    /**
     * 查询收到的房卡
     * @param transNo
     * @return
     */
    @RequestMapping(value = "/api/cardTrans/receive/package", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<Object> findSenRecevedCardRecord(String transNo ){
        return cardTransService.receiveCardPackage(transNo ,null);
    }


}
