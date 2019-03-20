package com.trevor.service.createRoom;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.WebSessionUser;
import com.trevor.service.createRoom.bo.NiuniuRoomParameter;

/**
 * @author trevor
 * @date 2019/3/8 16:52
 */
public interface CreateRoomService {

    /**
     * 创建牛牛房间
     * @param niuniuRoomParameter
     * @return
     */
    JsonEntity<Long> createRoom(NiuniuRoomParameter niuniuRoomParameter , WebSessionUser webSessionUser);
}
