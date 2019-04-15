package com.trevor.service.cardTrans;

import com.trevor.bo.JsonEntity;
import com.trevor.domain.CardTrans;
import com.trevor.domain.User;

import java.util.List;

/**
 * @author trevor
 * @date 2019/3/8 17:03
 */
public interface CardTransService {

    /**
     * 生成房卡包
     * @param cardNum
     * @param webSessionUser
     * @return
     */
    JsonEntity<String> createCardPackage(Integer cardNum , User webSessionUser);

    /**
     * 领取房卡包
     * @param transNum
     * @return
     */
    JsonEntity<Object> receiveCardPackage(String transNum , User webSessionUser);

    /**
     * 查询发出的房卡
     * @return
     */
    JsonEntity<List<CardTrans>> findSendCardRecord(User webSessionUser);

    /**
     * 查询收到的房卡
     * @param webSessionUser
     * @return
     */
    JsonEntity<List<CardTrans>> findRecevedCardRecord(User webSessionUser);
}
