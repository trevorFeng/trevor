package com.trevor.service;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.RechargeCard;
import com.trevor.bo.ResponseHelper;
import com.trevor.common.MessageCodeEnum;
import com.trevor.dao.PersonalCardMapper;
import com.trevor.dao.RechargeRecordMapper;
import com.trevor.domain.RechargeRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author trevor
 * @date 03/21/19 18:24
 */
@Service
@Slf4j
public class RechargeRecordServiceImpl implements RechargeRecordService{

    @Resource
    private RechargeRecordMapper rechargeRecordMapper;

    @Resource
    private PersonalCardMapper personalCardMapper;

    /**
     * 为玩家充值
     * @param rechargeCard
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JsonEntity<Object> rechargeCard(RechargeCard rechargeCard) {
        Long userId = rechargeCard.getUserId();
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUserId(userId);
        rechargeRecord.setRechargeCard(rechargeCard.getCardNum());
        rechargeRecord.setUnitPrice(rechargeCard.getUnitPrice());
        rechargeRecord.setTotalPrice(rechargeCard.getTotalPrice());
        rechargeRecord.setTime(System.currentTimeMillis());
        rechargeRecordMapper.insertOne(rechargeRecord);

        Integer hasCardNum = personalCardMapper.findCardNumByUserId(userId);
        personalCardMapper.updatePersonalCardNum(userId ,hasCardNum + rechargeCard.getCardNum());

        return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.HANDLER_SUCCESS);
    }
}
