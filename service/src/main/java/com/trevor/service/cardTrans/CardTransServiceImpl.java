package com.trevor.service.cardTrans;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.ResponseHelper;
import com.trevor.bo.UserInfo;
import com.trevor.common.BizKeys;
import com.trevor.common.MessageCodeEnum;
import com.trevor.dao.CardTransMapper;
import com.trevor.dao.PersonalCardMapper;
import com.trevor.domain.CardTrans;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author trevor
 * @date 2019/3/8 17:05
 */
@Service
public class CardTransServiceImpl implements CardTransService{

    @Resource
    private CardTransMapper cardTransMapper;

    @Resource
    private PersonalCardMapper personalCardMapper;

    /**
     * 生成房卡包
     * @param cardNum
     * @param userInfo
     * @return
     */
    @Override
    public JsonEntity<String> createCardPackage(Integer cardNum ,UserInfo userInfo) {
        //判断玩家房卡数量是否大于交易的房卡数
        Integer cardNumByUserId = personalCardMapper.findCardNumByUserId(userInfo.getId());
        if (cardNumByUserId < cardNum) {
            return ResponseHelper.withErrorInstance(MessageCodeEnum.USER_ROOMCARD_NOT_ENOUGH);
        }
        //生成房卡交易，插入数据库
        CardTrans cardTrans = new CardTrans();
        cardTrans.generateCardTransBase(userInfo ,cardNum);
        cardTransMapper.insertOne(cardTrans);
        //减去玩家拥有的房卡数量
        personalCardMapper.updatePersonalCardNum(userInfo.getId() ,cardNumByUserId - cardNum);
        return ResponseHelper.createInstance(cardTrans.getTransNum() ,MessageCodeEnum.CREATE_SUCCESS);
    }

    /**
     * 领取房卡包
     * @param transNum
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JsonEntity<Object> receiveCardPackage(String transNum ,UserInfo userInfo) {
        //将交易关闭
        Long termNum = cardTransMapper.closeTrans(System.currentTimeMillis(), userInfo.getId());
        if (!Objects.equals(BizKeys.ONE_UPDATE ,termNum)) {
            return ResponseHelper.withErrorInstance(MessageCodeEnum.TRANS_CLOSE);
        }
        //更新玩家房卡
        CardTrans oneByTransNo = cardTransMapper.findOneByTransNo(transNum);
        Integer cardNumByUserId = personalCardMapper.findCardNumByUserId(userInfo.getId());
        personalCardMapper.updatePersonalCardNum(userInfo.getId() ,cardNumByUserId + oneByTransNo.getCardNum());
        return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.RECEIVE_SUCCESS);
    }

    /**
     * 查询收到的房卡
     * @param userInfo
     * @return
     */
    @Override
    public JsonEntity<Object> findSendCardRecord(UserInfo userInfo) {

        return null;
    }
}
