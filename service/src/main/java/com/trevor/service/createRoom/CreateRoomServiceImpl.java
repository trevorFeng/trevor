package com.trevor.service.createRoom;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.ResponseHelper;
import com.trevor.bo.RoomPoke;
import com.trevor.common.ConsumCardEnum;
import com.trevor.common.MessageCodeEnum;
import com.trevor.dao.CardConsumRecordMapper;
import com.trevor.dao.PersonalCardMapper;
import com.trevor.dao.RoomPokeMapper;
import com.trevor.dao.RoomRecordMapper;
import com.trevor.domain.CardConsumRecord;
import com.trevor.domain.RoomPokeInit;
import com.trevor.domain.RoomRecord;
import com.trevor.domain.User;
import com.trevor.service.createRoom.bo.NiuniuRoomParameter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author trevor
 * @date 2019/3/8 16:53
 */
@Service
public class CreateRoomServiceImpl implements CreateRoomService{

    @Resource(name = "roomPokeMap")
    private Map<Long , RoomPoke> roomPokeMap;

    @Resource(name = "sessionsMap")
    private Map<Long ,CopyOnWriteArrayList<Session>> sessionsMap;

    @Resource
    private RoomRecordMapper roomRecordMapper;

    @Resource
    private PersonalCardMapper personalCardMapper;

    @Resource
    private CardConsumRecordMapper cardConsumRecordMapper;

    @Resource
    private RoomPokeMapper roomPokeMapper;

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
        Long currentTime = System.currentTimeMillis();
        RoomRecord roomRecord = new RoomRecord();
        roomRecord.generateRoomRecordBase(niuniuRoomParameter.getRoomType() ,niuniuRoomParameter , user.getId() ,currentTime);
        roomRecord.setState(1);
        roomRecordMapper.insertOne(roomRecord);

        RoomPoke roomPoke = new RoomPoke();
        roomPoke.setRoomRecordId(roomRecord.getId());
        if (niuniuRoomParameter.getConsumCardNum() == 1) {
            roomPoke.setTotalNum(12);
        }else if (niuniuRoomParameter.getConsumCardNum() == 2) {
            roomPoke.setTotalNum(24);
        }
        roomPoke.setLock(new ReentrantLock());

        RoomPokeInit roomPokeInit = new RoomPokeInit();
        roomPokeInit.setStatus(0);
        roomPokeInit.setEntryDate(currentTime);
        roomPokeInit.setRoomPoke();





        roomPokeMap.put(roomRecord.getId() ,roomPoke);

        //niuniuRooms.put(roomRecord.getId() ,new CopyOnWriteArrayList<>());
        //生成房卡消费记录
        CardConsumRecord cardConsumRecord = new CardConsumRecord();
        cardConsumRecord.generateCardConsumRecordBase(roomRecord.getId() , user.getId() ,consumCardNum);
        cardConsumRecordMapper.insertOne(cardConsumRecord);
        //更新玩家的房卡数量信息
        personalCardMapper.updatePersonalCardNum(user.getId() ,cardNumByUserId - consumCardNum);
        return ResponseHelper.createInstance(roomRecord.getId() , MessageCodeEnum.CREATE_SUCCESS);
    }
}
