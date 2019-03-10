package com.trevor.service.niuniu;

import com.alibaba.fastjson.JSON;
import com.trevor.bo.JsonEntity;
import com.trevor.bo.SimpleUser;
import com.trevor.bo.UserInfo;
import com.trevor.common.BizKeys;
import com.trevor.common.FriendManageEnum;
import com.trevor.common.RoomTypeEnum;
import com.trevor.common.SpecialEnum;
import com.trevor.dao.*;
import com.trevor.domain.RoomRecord;
import com.trevor.service.cache.RoomRecordCacheService;
import com.trevor.service.createRoom.bo.NiuniuRoomParameter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
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

    @Resource
    private FriendManageMapper friendManageMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 在websocket连接时检查房间是否存在以及房间人数是否已满
     * @param roomId
     * @return
     */
    @Override
    public void onOpenCheck(Session session , String roomId , UserInfo userInfo ,String userPicture) throws IOException {
        //查出房间配置
        RoomRecord oneById = roomRecordCacheService.findOneById(Long.valueOf(roomId));
        if (oneById == null) {
            session.close();
        }
        //房间不存在
        if (niuniuRooms.get(Long.valueOf(roomId)) == null) {
            session.close();
        }
        //房间是否已过期

        NiuniuRoomParameter niuniuRoomParameter = JSON.parseObject(oneById.getRoomConfig() ,NiuniuRoomParameter.class);
        //房主是否开启好友管理功能
        Boolean isFriendManage = Objects.equals(userMapper.findUserFriendManage(oneById.getRoomAuth()) , FriendManageEnum.YES.getCode());
        //开通
        if (isFriendManage) {
            isFriendManage(niuniuRoomParameter ,oneById ,userInfo ,session ,roomId ,userPicture);
        // 未开通
        }else {

        }
        if (niuniuRooms.containsKey(roomId)) {

        }
    }


    private void isFriendManage(NiuniuRoomParameter niuniuRoomParameter ,RoomRecord oneById ,
                                UserInfo userInfo ,Session session ,String roomId ,String userPicture) throws IOException {
        //配置仅限好友
        if (niuniuRoomParameter.getSpecial().contains(SpecialEnum.JUST_FRIENDS.getCode())) {
            Long count = friendManageMapper.countRoomAuthFriendAllow(oneById.getRoomAuth(), userInfo.getId());
            //不是房主的好友
            if (Objects.equals(count ,0L)) {
                session.close();
                //是房主的好友
            }else {
                String tempRoomId = roomId.intern();
                SimpleUser simpleUser = new SimpleUser(userInfo ,userPicture);
                Set<SimpleUser> simpleUsers = niuniuRooms.get(Long.valueOf(roomId));
                //允许观战
                if (niuniuRoomParameter.getSpecial().contains(SpecialEnum.CAN_SEE.getCode())) {
                    synchronized (tempRoomId) {
                        if (simpleUsers.size() < RoomTypeEnum.getRoomNumByType(niuniuRoomParameter.getRoomType())) {
                            simpleUser.setIsGuanZhan(Boolean.FALSE);
                        }else {
                            simpleUser.setIsGuanZhan(Boolean.TRUE);
                        }
                        simpleUsers.add(simpleUser);
                        session.getAsyncRemote().sendText(JSON.toJSONString(simpleUser));
                    }
                    //不允许观战
                }else {
                    synchronized (tempRoomId) {
                        if (simpleUsers.size() < RoomTypeEnum.getRoomNumByType(niuniuRoomParameter.getRoomType())) {
                            simpleUsers.add(simpleUser);
                            simpleUser.setIsGuanZhan(Boolean.FALSE);
                            session.getAsyncRemote().sendText(JSON.toJSONString(simpleUser));
                        }else {
                            session.close();
                        }
                    }
                }
            }
        }
    }

    private void isNotFriendManage(NiuniuRoomParameter niuniuRoomParameter ,RoomRecord oneById ,
                                UserInfo userInfo ,Session session ,String roomId ,String userPicture) throws IOException {

    }


}
