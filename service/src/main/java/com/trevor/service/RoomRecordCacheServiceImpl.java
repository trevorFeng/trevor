package com.trevor.service;

import com.trevor.dao.RoomRecordMapper;
import com.trevor.domain.RoomRecord;
import com.trevor.service.RoomRecordCacheService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author trevor
 * @date 2019/3/7 15:57
 */
@Service
public class RoomRecordCacheServiceImpl implements RoomRecordCacheService {

    @Resource
    private RoomRecordMapper roomRecordMapper;

    /**
     * 根据主键查询一条记录
     * @param id
     * @return
     */
    @Override
    public RoomRecord findOneById(Long id) {
        return roomRecordMapper.findOneById(id);
    }


    /**
     * 根据开放记录id查询开房人的id
     * @param roomId
     * @return
     */
    @Override
    public Long findRoomAuthIdByRoomId(Long roomId) {
        return roomRecordMapper.findRoomAuthIdByRoomId(roomId);
    }
}
