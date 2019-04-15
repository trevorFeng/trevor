package com.trevor.service.createRoom;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.ResponseHelper;
import com.trevor.common.ConsumCardEnum;
import com.trevor.common.MessageCodeEnum;
import com.trevor.dao.CardConsumRecordMapper;
import com.trevor.dao.PersonalCardMapper;
import com.trevor.dao.RoomRecordMapper;
import com.trevor.domain.CardConsumRecord;
import com.trevor.domain.RoomRecord;
import com.trevor.domain.User;
import com.trevor.service.createRoom.bo.NiuniuRoomParameter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author trevor
 * @date 2019/3/8 16:53
 */
@Service
public class CreateRoomServiceImpl implements CreateRoomService{

    @Resource(name = "niuniuRooms")
    private Map<Long , Set<Session>> niuniuRooms;

    @Resource
    private RoomRecordMapper roomRecordMapper;

    @Resource
    private PersonalCardMapper personalCardMapper;

    @Resource
    private CardConsumRecordMapper cardConsumRecordMapper;

    /**
     * 创建一个房间,返回主键,将房间放入Map中
     * @param niuniuRoomParameter
     * @return
     */
    @Override
    public JsonEntity<Long> createRoom(NiuniuRoomParameter niuniuRoomParameter , User user) {
        //判断玩家拥有的房卡数量是否超过消耗的房卡数
        Integer cardNumByUserId = personalCardMapper.findCardNumByUserId(user.getId());
        Integer consumCardNum;
        if (Objects.equals(niuniuRoomParameter.getConsumCardNum() , ConsumCardEnum.GAME_NUM_12_CARD_3.getCode())) {
            consumCardNum = ConsumCardEnum.GAME_NUM_12_CARD_3.getConsumCardNum();
            if (cardNumByUserId < ConsumCardEnum.GAME_NUM_12_CARD_3.getConsumCardNum()) {
                return ResponseHelper.withErrorInstance(MessageCodeEnum.USER_ROOMCARD_NOT_ENOUGH);
            }
        }else {
            consumCardNum = ConsumCardEnum.GAME_NUM_24_CARD_6.getConsumCardNum();
            if (cardNumByUserId < ConsumCardEnum.GAME_NUM_24_CARD_6.getConsumCardNum()) {
                return ResponseHelper.withErrorInstance(MessageCodeEnum.USER_ROOMCARD_NOT_ENOUGH);
            }
        }
        //生成房间，将房间信息存入数据库
        RoomRecord roomRecord = new RoomRecord();
        roomRecord.generateRoomRecordBase(niuniuRoomParameter.getRoomType() ,niuniuRoomParameter , user.getId());
        Long roomRecordId = roomRecordMapper.insertOne(roomRecord);
        //将房间放入map中
        niuniuRooms.put(roomRecordId ,new HashSet<>(2<<4));
        //生成房卡消费记录
        CardConsumRecord cardConsumRecord = new CardConsumRecord();
        cardConsumRecord.generateCardConsumRecordBase(roomRecordId , user.getId() ,consumCardNum);
        cardConsumRecordMapper.insertOne(cardConsumRecord);
        //更新玩家的房卡数量信息
        personalCardMapper.updatePersonalCardNum(user.getId() ,cardNumByUserId - consumCardNum);
        return ResponseHelper.createInstance(roomRecordId , MessageCodeEnum.CREATE_SUCCESS);
    }
}
