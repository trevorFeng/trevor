package com.trevor.service.websocket;

import com.trevor.bo.RoomPoke;
import com.trevor.task.CountdownTask;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trevor
 * @date 2019/3/19 09:58
 */
@Service
public class NiuNiuDealReceMessage {

    /**
     * key为房间id，RoomPoke为每个房间的对局情况
     */
    private final Map<Long , RoomPoke> map = new ConcurrentHashMap<>(2<<15);

    @Resource(name = "niuniuRooms")
    private Map<Long , Set<Session>> niuniuRomms;

    @Resource
    private CountdownTask countdownTask;


    /**
     * 增加房间
     * @param roomRecordId
     * @param roomPoke
     */
    public void addRoomPoke(Long roomRecordId ,RoomPoke roomPoke){
        map.put(roomRecordId ,roomPoke);
    }

    /**
     * 处理准备的消息
     */
    public void dealReady(Long roomId) throws IOException, InterruptedException {
        RoomPoke roomPoke = map.get(roomId);
        if (Objects.equals(roomPoke.getIsReadyOver() ,true)) {
            return;
        }
        roomPoke.getLock().lock();
        roomPoke.setUserReadyNum(roomPoke.getUserReadyNum() + 1);
        //开始5s倒计时
        if (roomPoke.getUserReadyNum() == 2) {
            roomPoke.getLock().unlock();
            countdownTask.coundDown(niuniuRomms.get(roomId) ,roomPoke);
        }else {
            roomPoke.getLock().unlock();
        }
    }
}
