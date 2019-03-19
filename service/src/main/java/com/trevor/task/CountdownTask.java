package com.trevor.task;

import com.trevor.bo.RoomPoke;
import com.trevor.util.WebsocketUtil;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Set;

/**
 * @author trevor
 * @date 2019/3/13 19:20
 */
@Service
public class CountdownTask {

    /**
     * 开启5秒倒计时
     * @param sessions
     */
    public void coundDown(Set<Session> sessions , RoomPoke roomPoke) throws IOException, InterruptedException {
        for (int i = 5; i > 0 ; i--) {
            Thread.sleep(1000);
            WebsocketUtil.sendAllBasicMessage(sessions ,String.valueOf(i));
        }
        roomPoke.setIsReadyOver(true);

    }
}
