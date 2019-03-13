package com.trevor.service.cardTrans;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.UserInfo;
import com.trevor.domain.CardTrans;

import java.util.List;

/**
 * @author trevor
 * @date 2019/3/8 17:03
 */
public interface CardTransService {

    /**
     * 生成房卡包
     * @param cardNum
     * @param userInfo
     * @return
     */
    JsonEntity<String> createCardPackage(Integer cardNum ,UserInfo userInfo);

    /**
     * 领取房卡包
     * @param transNum
     * @return
     */
    JsonEntity<Object> receiveCardPackage(String transNum ,UserInfo userInfo);

    /**
     * 查询发出的房卡
     * @return
     */
    JsonEntity<List<CardTrans>> findSendCardRecord(UserInfo userInfo);
}
