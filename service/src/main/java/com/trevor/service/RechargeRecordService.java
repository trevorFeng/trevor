package com.trevor.service;


import com.trevor.bo.JsonEntity;
import com.trevor.bo.RechargeCard;

/**
 * @author trevor
 * @date 03/21/19 18:24
 */
public interface RechargeRecordService {

    /**
     * 为玩家充值
     * @param rechargeCard
     * @return
     */
    JsonEntity<Object> rechargeCard(RechargeCard rechargeCard);
}
