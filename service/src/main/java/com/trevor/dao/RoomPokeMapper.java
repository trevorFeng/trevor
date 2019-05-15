package com.trevor.dao;

import com.trevor.domain.RoomPokeInit;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author
 * @date 05/14/19 18:05
 */
@Repository
public interface RoomPokeMapper {

    void insertOne(@Param("roomPokeInit") RoomPokeInit roomPokeInit);

    List<RoomPokeInit> findAll();

    List<Long> findByByRoomRecordId(List<Long> ids);

    void deleteByRoomRecordId(@Param("roomRecordId") Long roomRecordId);

    void updateByRoomRecordId(@Param("roomRecordId") Long roomRecordId);
}
