package com.trevor.websocket.niuniu;

import com.trevor.bo.RoomPoke;
import com.trevor.domain.Room;
import com.trevor.domain.User;

import java.io.IOException;

import com.trevor.websocket.bo.ReceiveMessage;
import com.trevor.websocket.bo.ReturnMessage;
import com.trevor.websocket.bo.SocketUser;

import javax.websocket.EncodeException;
import javax.websocket.Session;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-04 22:42
 **/

public interface NiuniuService {

    /**
     * 在websocket连接时检查房间是否存在以及房间人数是否已满
     * @param room
     * @return
     */
    ReturnMessage<SocketUser> onOpenCheck(Room room , User user) throws IOException;

    /**
     * 根据准备的消息
     * @return
     */
    void dealReadyMessage(Session session ,Long userId ,Long roomId) throws InterruptedException, EncodeException, IOException;

    /**
     * 处理抢庄的消息
     * @param roomId
     * @param receiveMessage
     */
    void dealQiangZhuangMessage(Session mySession ,Long userId , Long roomId , ReceiveMessage receiveMessage) throws IOException, EncodeException;


    /**
     * 处理闲家下注的消息
     * @param roomId
     * @param receiveMessage
     */
    void dealXianJiaXiaZhuMessage(Session session ,Long userId , Long roomId , ReceiveMessage receiveMessage) throws IOException, EncodeException;

    void dealTanPaiMessage(Session mySession ,Long userId , Long roomId) throws IOException, EncodeException;
}
