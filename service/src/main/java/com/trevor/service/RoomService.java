package com.trevor.service;

import com.trevor.domain.Room;

/**
 * @author trevor
 * @date 2019/3/7 15:57
 */
public interface RoomService {

    /**
     * 根据主键查询一条记录(全部信息)
     * @param id
     * @return
     */
    Room findOneById(Long id);

    /**
     * 根据房间记录id查询开房人的id
     * @param roomId
     * @return
     */
    Long findRoomAuthIdByRoomId(Long roomId);

}
