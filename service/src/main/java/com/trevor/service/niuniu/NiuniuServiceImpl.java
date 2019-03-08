package com.trevor.service.niuniu;

import com.alibaba.fastjson.JSON;
import com.trevor.bo.JsonEntity;
import com.trevor.bo.SimpleUser;
import com.trevor.common.BizKeys;
import com.trevor.dao.CardConsumRecordMapper;
import com.trevor.dao.PersonalCardMapper;
import com.trevor.dao.RoomRecordMapper;
import com.trevor.domain.RoomRecord;
import com.trevor.service.cache.RoomRecordCacheService;
import com.trevor.service.createRoom.bo.NiuniuRoomParameter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-06 22:28
 **/
@Service
public class NiuniuServiceImpl implements NiuniuService{

    @Resource(name = "niuniuRooms")
    private Map<Long ,Set<SimpleUser>> niuniuRooms;

    @Resource
    private RoomRecordCacheService roomRecordCacheService;

    @Resource
    private RoomRecordMapper roomRecordMapper;

    @Resource
    private PersonalCardMapper personalCardMapper;

    @Resource
    private CardConsumRecordMapper cardConsumRecordMapper;

    /**
     * 在websocket连接时检查房间是否存在以及房间人数是否已满
     * @param roomId
     * @return
     */
    @Override
    public JsonEntity<Object> onOpenCheck(String roomId) {
        //判断是否是房间主人的好友

        RoomRecord oneById = roomRecordCacheService.findOneById(Long.valueOf(roomId));
        NiuniuRoomParameter niuniuRoomParameter = JSON.parseObject(oneById.getRoomConfig() ,NiuniuRoomParameter.class);
        if (niuniuRoomParameter.getSpecial().contains(2)) {

        }
        if (niuniuRooms.containsKey(roomId)) {

        }
        if (oneById == null) {
            return null;
        }
        Long differenceTime = System.currentTimeMillis() - oneById.getGetRoomTime();
        if (differenceTime < BizKeys.ROOM_EXPIRATION_DATE) {
            //oneById.getRoomConfig()
        }
        return null;
    }


}
