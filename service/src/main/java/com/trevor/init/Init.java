package com.trevor.init;

import com.trevor.dao.RoomPokeMapper;
import com.trevor.bo.RoomPoke;
import com.trevor.domain.RoomPokeInit;
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
        List<RoomPokeInit> roomPokeInits = roomPokeMapper.findAll();
        for (RoomPokeInit roomPokeInit : roomPokeInits) {
            ByteArrayInputStream byteInt = new ByteArrayInputStream(blobToBytes(roomPokeInit.getRoomPoke()));
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

    private static byte[] blobToBytes(Blob blob) {
        BufferedInputStream is = null;
        try {
            is = new BufferedInputStream(blob.getBinaryStream());
            byte[] bytes = new byte[(int) blob.length()];
            int len = bytes.length;
            int offset = 0;
            int read = 0;
            while (offset < len && (read = is.read(bytes, offset, len - offset)) >= 0) {
                offset += read;
            }
            return bytes;
        } catch (Exception e) {
            try {
                is.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            is = null;
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                return null;
            }
        }
    }
}
