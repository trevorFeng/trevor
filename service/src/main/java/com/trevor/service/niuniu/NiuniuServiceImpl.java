package com.trevor.service.niuniu;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.ResponseHelper;
import com.trevor.bo.SimpleUser;
import com.trevor.bo.UserInfo;
import com.trevor.common.BizKeys;
import com.trevor.dao.RoomRecordMapper;
import com.trevor.domain.RoomRecord;
import com.trevor.service.cache.RoomRecordCacheService;
import com.trevor.service.niuniu.bo.NiuniuRoomParameter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-06 22:28
 **/
@Service
public class NiuniuServiceImpl implements NiuniuService{

    /**
     * 使用map来收集session，key为房间id，value为同一个房间的用户集合
     */
    private static final Map<Long, Set<SimpleUser>> rooms = new ConcurrentHashMap(2<<15);

    @Resource
    private RoomRecordCacheService roomRecordCacheService;

    @Resource
    private RoomRecordMapper roomRecordMapper;

    /**
     * 在websocket连接时检查房间是否存在以及房间人数是否已满
     * @param roomId
     * @return
     */
    @Override
    public JsonEntity<Object> onOpenCheck(String roomId) {
        RoomRecord oneById = roomRecordCacheService.findOneById(Long.valueOf(roomId));
        if (oneById == null) {
            return null;
        }
        Long differenceTime = System.currentTimeMillis() - oneById.getGetRoomTime();
        if (differenceTime < BizKeys.ROOM_EXPIRATION_DATE) {
            //oneById.getRoomConfig()
        }
        return null;
    }

    /**
     * 创建一个房间,返回主键
     * @param niuniuRoomParameter
     * @return
     */
    @Override
    public JsonEntity<Long> createRoom(NiuniuRoomParameter niuniuRoomParameter ,UserInfo userInfo) {
        RoomRecord roomRecord = new RoomRecord();
        roomRecord.generateRoomRecordBase(niuniuRoomParameter.getRoomType() ,niuniuRoomParameter);
        Long roomRecordId = roomRecordMapper.insertOne(roomRecord);
        Set<SimpleUser> set = new HashSet<>(2<<4);
        SimpleUser simpleUser = new SimpleUser(userInfo);
        set.add(simpleUser);
        rooms.put(roomRecordId ,set);
        return ResponseHelper.insertSuccess(roomRecordId);
    }
}
