package com.trevor.task;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.trevor.bo.ReturnCard;
import com.trevor.bo.RoomPoke;
import com.trevor.dao.CardConsumRecordMapper;
import com.trevor.dao.PersonalCardMapper;
import com.trevor.dao.RoomPokeInitMapper;
import com.trevor.dao.RoomRecordMapper;
import com.trevor.domain.RoomRecord;
import com.trevor.service.createRoom.bo.NiuniuRoomParameter;
import com.trevor.util.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

/**
 * @author trevor
 * @date 05/14/19 17:24
 */
@Component
@Slf4j
public class CheckRoomOverDateTask implements ApplicationRunner {

    @Resource(name = "executor")
    private Executor executor;

    @Resource
    private RoomPokeInitMapper roomPokeInitMapper;

    @Resource
    private RoomRecordMapper roomRecordMapper;

    @Resource
    private CardConsumRecordMapper cardConsumRecordMapper;

    @Resource
    private PersonalCardMapper personalCardMapper;

    @Resource(name = "roomPokeMap")
    private Map<Long , RoomPoke> roomPokeMap;

    @Resource(name = "sessionsMap")
    private Map<Long , CopyOnWriteArrayList<Session>> sessionsMap;


    @Override
    public void run(ApplicationArguments args){
        executor.execute(() -> {
            while (true) {
                try {
                    Thread.sleep(1000 * 60 * 15);
                } catch (InterruptedException e) {
                    log.error(e.toString());
                }
                //房间半小时内未使用会被关闭
                checkRoomRecord();
            }
        });

    }

    private void checkRoomRecord(){
        Long currentTime = System.currentTimeMillis();
        Long halfHourBefore = currentTime - 1000 * 60 * 30;
        //超过半小时未使用的房间ids
        List<Long> overDayRoomRecordIds = roomRecordMapper.findByGetRoomTimeAndState_1(halfHourBefore);
        if (overDayRoomRecordIds.isEmpty()) {
            return;
        }
        //超过半小时并且还没有激活的roomPoke的roomRecord的ids
        // todo 如何保证这个代码在执行时一定在开始打牌任务前执行
        List<Long> overDayAndStatus_0RoomRecordIds = roomPokeInitMapper.findRoomRecordIdsStatus_0AndRoomRecordIds(overDayRoomRecordIds);
        if (overDayAndStatus_0RoomRecordIds.isEmpty()) {
            return;
        }
        //删除roomPokeMap中的roomPoke
        MapUtil.removeEntries(roomPokeMap ,overDayAndStatus_0RoomRecordIds);
        //删除sessionsMap的Sessions
        MapUtil.removeEntries(sessionsMap ,overDayAndStatus_0RoomRecordIds);
        //关闭房间
        roomRecordMapper.updateState_0(overDayAndStatus_0RoomRecordIds);
        //将roomPokeInit的status置为3
        roomPokeInitMapper.updateStatus_3(overDayRoomRecordIds);
        //返回房卡
        List<RoomRecord> roomRecords = roomRecordMapper.findByIds(overDayAndStatus_0RoomRecordIds);
        //删除房卡消费记录
        cardConsumRecordMapper.deleteByRoomRecordIds(overDayAndStatus_0RoomRecordIds);
        //返回房卡
        List<ReturnCard> returnCards = Lists.newArrayList();
        roomRecords.forEach(roomRecord -> {
            ReturnCard returnCard = new ReturnCard();
            returnCard.setUserId(roomRecord.getId());
            NiuniuRoomParameter niuniuRoomParameter = JSON.parseObject(roomRecord.getRoomConfig() ,NiuniuRoomParameter.class);
            if (Objects.equals(niuniuRoomParameter.getConsumCardNum() ,1)) {
                returnCard.setReturnCardNum(3);
            }else {
                returnCard.setReturnCardNum(6);
            }
            returnCards.add(returnCard);
        });
        personalCardMapper.updatePersonalCardNumByUserIds(returnCards);
    }
}
