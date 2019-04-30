package com.trevor.websocket.niuniu;

import com.trevor.domain.User;

import java.io.IOException;

import com.trevor.websocket.bo.ReturnMessage;
import com.trevor.websocket.bo.SocketUser;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-04 22:42
 **/

public interface NiuniuService {

    /**
     * 在websocket连接时检查房间是否存在以及房间人数是否已满
     * @param roomId
     * @return
     */
    ReturnMessage<SocketUser> onOpenCheck(String roomId , User user) throws IOException;

    /**
     * 根据准备的消息
     * @return
     */
    ReturnMessage<SocketUser> dealReadyMessage(SocketUser socketUser ,Long roomId);



}
