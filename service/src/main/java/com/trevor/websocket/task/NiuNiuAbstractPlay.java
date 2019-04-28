package com.trevor.websocket.task;

import com.trevor.bo.RoomPoke;
import com.trevor.util.WebsocketUtil;
import com.trevor.websocket.bo.ReturnMessage;

import javax.annotation.Resource;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Auther: trevor
 * @Date: 2019\4\22 0022 23:07
 * @Description:
 */
public abstract class NiuNiuAbstractPlay {

    @Resource(name = "niuniuRooms")
    private Map<Long ,CopyOnWriteArrayList<Session>> niuniuRooms;

    @Resource(name = "niuniuRoomPoke")
    private Map<Long , RoomPoke> roomPokeMap;



    public final void playPoke(Long rommId) throws InterruptedException, EncodeException, IOException {
        CopyOnWriteArrayList<Session> sessions = niuniuRooms.get(rommId);
        //准备倒计时
        countDown(sessions ,roomPokeMap.get(rommId));
    }


    /**
     * 倒计时
     */
    protected void countDown(CopyOnWriteArrayList<Session> sessions , RoomPoke roomPoke) throws InterruptedException, IOException, EncodeException {
        for (int i = 5; i > 0 ; i--) {
            ReturnMessage<Integer> returnMessage = new ReturnMessage<>(i ,3);
            WebsocketUtil.sendAllBasicMessage(sessions , returnMessage);
            Thread.sleep(1000);
        }
        roomPoke.setIsReadyOver(true);
    }

    /**
     * 准备结束发消息给客户端
     */
    protected void readyOver(CopyOnWriteArrayList<Session> sessions , RoomPoke roomPoke) throws InterruptedException, IOException, EncodeException {
        for (int i = 5; i > 0 ; i--) {
            ReturnMessage<Integer> returnMessage = new ReturnMessage<>(i ,3);
            WebsocketUtil.sendAllBasicMessage(sessions , returnMessage);
            Thread.sleep(1000);
        }
        roomPoke.setIsReadyOver(true);
    }



    /**
     * 发牌
     */
    protected void fapai(){

    }
}
