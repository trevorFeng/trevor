package com.trevor.init;

import com.trevor.dao.RoomPokeMapper;
import com.trevor.bo.RoomPoke;
import com.trevor.domain.RoomPokeInit;
import com.trevor.util.ByteToBlobUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.util.List;
import java.util.Map;

/**
 * @author trevor
 * @date 05/14/19 17:58
 */
@Component
public class Init implements ApplicationRunner {

    @Resource(name = "roomPokeMap")
    private Map<Long , RoomPoke> roomPokeMap;

    @Resource
    private RoomPokeMapper roomPokeMapper;

    @Override
    public void run(ApplicationArguments args) {
        //init roomPoke
        List<RoomPokeInit> roomPokeInits = roomPokeMapper.findAll();
        for (RoomPokeInit roomPokeInit : roomPokeInits) {
            ByteArrayInputStream byteInt = new ByteArrayInputStream(ByteToBlobUtil.blobToBytes(roomPokeInit.getRoomPoke()));
            ObjectInputStream objInt= null;
            try {
                objInt = new ObjectInputStream(byteInt);
                RoomPoke roomPoke = (RoomPoke) objInt.readObject();
                roomPokeMap.put(roomPokeInit.getRoomRecordId() ,roomPoke);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    byteInt.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }


}
