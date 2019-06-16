package com.trevor.service.createRoom;

import com.alibaba.fastjson.JSON;
import com.trevor.BizException;
import com.trevor.bo.JsonEntity;
import com.trevor.bo.ResponseHelper;
import com.trevor.bo.RoomPoke;
import com.trevor.enums.ConsumCardEnum;
import com.trevor.enums.MessageCodeEnum;
import com.trevor.dao.CardConsumRecordMapper;
import com.trevor.dao.PersonalCardMapper;
import com.trevor.dao.RoomPokeInitMapper;
import com.trevor.dao.RoomMapper;
import com.trevor.domain.CardConsumRecord;
import com.trevor.domain.RoomPokeInit;
import com.trevor.domain.Room;
import com.trevor.domain.User;
import com.trevor.service.createRoom.bo.NiuniuRoomParameter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.*;

/**
 * @author trevor
 * @date 2019/3/8 16:53
 */
@Service
public class CreateRoomServiceImpl implements CreateRoomService{

    @Resource(name = "roomPokeMap")
    private Map<Long , RoomPoke> roomPokeMap;

    @Resource(name = "sessionsMap")
    private Map<Long ,Set<Session>> sessionsMap;

    @Resource
    private RoomMapper roomMapper;

    @Resource
    private PersonalCardMapper personalCardMapper;

    @Resource
    private CardConsumRecordMapper cardConsumRecordMapper;

    @Resource
    private RoomPokeInitMapper roomPokeInitMapper;

    /**
     * 创建一个房间,返回主键,将房间放入Map中
     * @param niuniuRoomParameter
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JsonEntity<Long> createRoom(NiuniuRoomParameter niuniuRoomParameter , User user) {
        checkParm(niuniuRoomParameter);
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
        Room room = new Room();
        room.generateRoomRecordBase(niuniuRoomParameter.getRoomType() ,niuniuRoomParameter , user.getId() ,currentTime);
        room.setState(1);
        roomMapper.insertOne(room);

        //生成roomPoke放入roomPokeMap
        RoomPoke roomPoke = new RoomPoke();
        roomPoke.setRoomRecordId(room.getId());
        if (niuniuRoomParameter.getConsumCardNum() == 1) {
            roomPoke.setTotalNum(12);
        }else if (niuniuRoomParameter.getConsumCardNum() == 2) {
            roomPoke.setTotalNum(24);
        }
        roomPokeMap.put(room.getId() ,roomPoke);

        //放入sesionsMap
        sessionsMap.put(room.getId() ,new HashSet<>());

        //生成roomPokeInit插入数据库
        RoomPokeInit roomPokeInit = new RoomPokeInit();
        roomPokeInit.setRoomRecordId(room.getId());
        roomPokeInit.setUserPokes(JSON.toJSONString(roomPoke.getUserPokes()));
        roomPokeInit.setUserScores(JSON.toJSONString(roomPoke.getUserScores()));
        roomPokeInit.setRuningNum(0);
        roomPokeInit.setTotalNum(roomPoke.getTotalNum());
        roomPokeInit.setStatus(0);
        roomPokeInit.setEntryDate(currentTime);
        roomPokeInitMapper.insertOne(roomPokeInit);

        //生成房卡消费记录
        CardConsumRecord cardConsumRecord = new CardConsumRecord();
        cardConsumRecord.generateCardConsumRecordBase(room.getId() , user.getId() ,consumCardNum);
        cardConsumRecordMapper.insertOne(cardConsumRecord);

        //更新玩家的房卡数量信息
        personalCardMapper.updatePersonalCardNum(user.getId() ,cardNumByUserId - consumCardNum);
        return ResponseHelper.createInstance(room.getId() , MessageCodeEnum.CREATE_SUCCESS);
    }

    private void checkParm(NiuniuRoomParameter niuniuRoomParameter){
        Integer roomType = niuniuRoomParameter.getRoomType();
        if (!Objects.equals(roomType ,1) && !Objects.equals(roomType ,2) && !Objects.equals(roomType ,3)) {
            throw new BizException(-200 ,"roomType 错误");
        }
        Integer robZhuangType = niuniuRoomParameter.getRobZhuangType();
        if (!Objects.equals(robZhuangType ,1) && !Objects.equals(robZhuangType ,2) &&
                !Objects.equals(robZhuangType ,3) && !Objects.equals(robZhuangType ,4)) {
            throw new BizException(-200 ,"robZhuangType 错误");
        }
        Integer basePoint = niuniuRoomParameter.getBasePoint();


    }


}
