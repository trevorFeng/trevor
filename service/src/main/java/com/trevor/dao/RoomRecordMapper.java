package com.trevor.dao;

import com.trevor.domain.RoomRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author trevor
 * @date 2019/3/7 12:50
 */
@Repository
public interface RoomRecordMapper {

    /**
     * 根据主键查询一条记录
     * @param id
     * @return
     */
    RoomRecord findOneById(@Param("id") Long id);

    /**
     * 插入一条记录并返回主键
     * @param roomRecord
     * @return
     */
    Long insertOne(@Param("roomRecord") RoomRecord roomRecord);
}
