package com.trevor.init;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.trevor.bo.RoomPoke;
import com.trevor.bo.UserPokesIndex;
import com.trevor.bo.UserScore;
import com.trevor.dao.RoomPokeInitMapper;
import com.trevor.domain.RoomPokeInit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author trevor
 * @date 05/14/19 17:58
 */
@Component
@Slf4j
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
            roomPoke.setUserPokes(JSON.parseObject(roomPokeInit.getUserPokes() ,new TypeReference<List<UserPokesIndex>>(){}));
            roomPoke.setUserScores(JSON.parseObject(roomPokeInit.getUserScores() ,new TypeReference<List<UserScore>>(){}));
            roomPoke.setRuningNum(roomPokeInit.getRuningNum());
            roomPoke.setTotalNum(roomPokeInit.getTotalNum());
            roomPokeMap.put(roomPokeInit.getRoomRecordId() ,roomPoke);

            sessionsMap.put(roomPokeInit.getRoomRecordId() ,new CopyOnWriteArrayList<>());
            log.info("初始化roomPokeMap和sessionsMap成功");
        }
    }


}
