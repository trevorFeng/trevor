package com.trevor.service.cache;

import com.trevor.domain.RoomRecord;

/**
 * @author trevor
 * @date 2019/3/7 15:57
 */
public interface RoomRecordCacheService {

    /**
     * 根据主键查询一条记录(全部信息)
     * @param id
     * @return
     */
    RoomRecord findOneById(Long id);

}
