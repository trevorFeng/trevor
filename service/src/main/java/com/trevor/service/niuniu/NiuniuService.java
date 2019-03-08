package com.trevor.service.niuniu;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.UserInfo;
import com.trevor.service.niuniu.bo.NiuniuRoomParameter;

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
    JsonEntity<Object> onOpenCheck(String roomId);


    /**
     * 创建房间
     * @param niuniuRoomParameter
     * @return
     */
    JsonEntity createRoom(NiuniuRoomParameter niuniuRoomParameter , UserInfo userInfo);

}
