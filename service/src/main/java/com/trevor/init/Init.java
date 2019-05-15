package com.trevor.init;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.trevor.bo.UserPoke;
import com.trevor.dao.RoomPokeInitMapper;
import com.trevor.bo.RoomPoke;
import com.trevor.domain.RoomPokeInit;
import com.trevor.util.ByteToBlobUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author trevor
 * @date 05/14/19 17:58
 */
@Component
public class Init implements ApplicationRunner {

    @Resource(name = "roomPokeMap")
    private Map<Long , RoomPoke> roomPokeMap;

    @Resource
    private RoomPokeInitMapper roomPokeInitMapper;

    @Resource(name = "sessionsMap")
    private Map<Long , CopyOnWriteArrayList<Session>> sessionsMap;

    /**
     * 初始化roomPoke到roomPokeMap中,初始化sessionsMap
     * @param args
     */
    @Override
    public void run(ApplicationArguments args) {
        List<RoomPokeInit> roomPokeInits = roomPokeInitMapper.findStatus_0();
        for (RoomPokeInit roomPokeInit : roomPokeInits) {
            RoomPoke roomPoke = new RoomPoke();
            roomPoke.setRoomRecordId(roomPokeInit.getRoomRecordId());
            roomPoke.setUserPokes(JSON.parseObject(roomPokeInit.getUserPokes() ,new TypeReference<List<Map<Long ,UserPoke>>>(){}));
            roomPoke.setScoreMap(JSON.parseObject(roomPokeInit.getScoreMap() ,new TypeReference<Map<Long ,Integer>>(){}));
            roomPoke.setRuningNum(roomPokeInit.getRuningNum());
            roomPoke.setTotalNum(roomPokeInit.getTotalNum());
            roomPokeMap.put(roomPokeInit.getRoomRecordId() ,roomPoke);

            sessionsMap.put(roomPokeInit.getRoomRecordId() ,new CopyOnWriteArrayList<>());
        }
    }


}
