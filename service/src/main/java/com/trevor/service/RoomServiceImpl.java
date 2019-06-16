package com.trevor.service;

import com.trevor.dao.RoomMapper;
import com.trevor.domain.Room;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author trevor
 * @date 2019/3/7 15:57
 */
@Service
public class RoomServiceImpl implements RoomService {

    @Resource
    private RoomMapper roomMapper;

    /**
     * 根据主键查询一条记录
     * @param id
     * @return
     */
    @Override
    public Room findOneById(Long id) {
        return roomMapper.findOneById(id);
    }


    /**
     * 根据开放记录id查询开房人的id
     * @param roomId
     * @return
     */
    @Override
    public Long findRoomAuthIdByRoomId(Long roomId) {
        return roomMapper.findRoomAuthIdByRoomId(roomId);
    }
}
