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
public interface RoomPokeInitMapper {

    void insertOne(@Param("roomPokeInit") RoomPokeInit roomPokeInit);

    List<RoomPokeInit> findStatus_0();

    List<Long> findRoomRecordIdsStatus_0AndRoomRecordIds(List<Long> roomRecordIds);

    void updateStatus_3(List<Long> roomRecordIds);

    List<Long> findByByRoomRecordId(List<Long> ids);

    void deleteByRoomRecordId(@Param("roomRecordId") Long roomRecordId);

    void updateByRoomRecordId(@Param("roomRecordId") Long roomRecordId);
}
