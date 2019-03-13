package com.trevor.service.niuniu;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.SimpleUser;
import com.trevor.bo.UserInfo;

import javax.websocket.Session;
import java.io.IOException;

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
    JsonEntity<SimpleUser> onOpenCheck(String roomId , UserInfo userInfo) throws IOException;

    /**
     * 根据消息来处理
     * @return
     */
    JsonEntity<SimpleUser> dealReceiveMessage(String message ,SimpleUser simpleUser);



}
