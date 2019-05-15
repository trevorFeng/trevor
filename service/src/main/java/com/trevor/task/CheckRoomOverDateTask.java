package com.trevor.task;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.trevor.bo.ReturnCard;
import com.trevor.bo.RoomPoke;
import com.trevor.dao.CardConsumRecordMapper;
import com.trevor.dao.PersonalCardMapper;
import com.trevor.dao.RoomPokeMapper;
import com.trevor.dao.RoomRecordMapper;
import com.trevor.domain.RoomRecord;
import com.trevor.service.createRoom.bo.NiuniuRoomParameter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

/**
 * @author trevor
 * @date 05/14/19 17:24
 */
@Component
public class CheckRoomOverDateTask implements ApplicationRunner {

    @Resource(name = "executor")
    private Executor executor;

    @Resource
    private RoomPokeMapper roomPokeMapper;

    @Resource
    private RoomRecordMapper roomRecordMapper;

    @Resource
    private CardConsumRecordMapper cardConsumRecordMapper;

    @Resource
    private PersonalCardMapper personalCardMapper;

    @Resource(name = "roomPokeMap")
    private Map<Long , RoomPoke> roomPokeMap;


    /**
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args){
        executor.execute(() -> {
            while (true) {
                //房间半小时内未使用会被关闭
                checkRoomRecord();
            }
        });

    }

    private void checkRoomRecord(){
        Long currentTime = System.currentTimeMillis();
        Long halfHourBefore = currentTime - 1000 * 60 * 30;
        List<Long> overDayRoomRecordIds = roomRecordMapper.findByGetRoomTime(halfHourBefore);
        List<Long> needToUpdate = Lists.newArrayList();
        if (overDayRoomRecordIds.isEmpty()) {
            try {
                Thread.sleep(1000 * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            List<Long> hasUseredRoomRecordIds = roomPokeMapper.findByByRoomRecordId(overDayRoomRecordIds);
            for (Long id : overDayRoomRecordIds) {
                if (!hasUseredRoomRecordIds.contains(id)) {
                    needToUpdate.add(id);
                }
            }
        }
        if (!needToUpdate.isEmpty()) {
            roomRecordMapper.updateState(needToUpdate);
        }
        //返回房卡
        List<RoomRecord> roomRecords = roomRecordMapper.findByIds(needToUpdate);
        //删除房卡消费记录
        cardConsumRecordMapper.deleteByRoomRecordIds(needToUpdate);
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
