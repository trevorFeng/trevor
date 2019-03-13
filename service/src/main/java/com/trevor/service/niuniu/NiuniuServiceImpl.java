package com.trevor.service.niuniu;

import com.alibaba.fastjson.JSON;
import com.trevor.bo.JsonEntity;
import com.trevor.bo.ResponseHelper;
import com.trevor.bo.SimpleUser;
import com.trevor.bo.UserInfo;
import com.trevor.common.FriendManageEnum;
import com.trevor.common.MessageCodeEnum;
import com.trevor.common.RoomTypeEnum;
import com.trevor.common.SpecialEnum;
import com.trevor.dao.FriendManageMapper;
import com.trevor.dao.UserMapper;
import com.trevor.domain.RoomRecord;
import com.trevor.service.cache.RoomRecordCacheService;
import com.trevor.service.createRoom.bo.NiuniuRoomParameter;
import com.trevor.service.niuniu.bo.NiuniuAction;
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
    private Map<Long ,Set<Session>> niuniuRooms;

    @Resource
    private RoomRecordCacheService roomRecordCacheService;

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
    public JsonEntity<SimpleUser> onOpenCheck(String roomId , UserInfo userInfo) throws IOException {
        //查出房间配置
        RoomRecord oneById = roomRecordCacheService.findOneById(Long.valueOf(roomId));
        if (oneById == null) {
            return ResponseHelper.withErrorInstance(MessageCodeEnum.ROOM_NOT_EXIST);
        }
        //房间已关闭
        if (niuniuRooms.get(Long.valueOf(roomId)) == null) {
            return ResponseHelper.withErrorInstance(MessageCodeEnum.ROOM_CLOSE);
        }
        NiuniuRoomParameter niuniuRoomParameter = JSON.parseObject(oneById.getRoomConfig() ,NiuniuRoomParameter.class);
        //房主是否开启好友管理功能
        Boolean isFriendManage = Objects.equals(userMapper.findUserFriendManage(oneById.getRoomAuth()) , FriendManageEnum.YES.getCode());
        //开通
        if (isFriendManage) {
            return this.isFriendManage(niuniuRoomParameter ,oneById ,userInfo ,roomId);
        // 未开通
        }else {
            return this.dealCanSee(roomId ,userInfo ,niuniuRoomParameter);
        }
    }

    /**
     * 处理接受到的新消息
     * @param message
     * @param simpleUser
     * @return
     */
    @Override
    public JsonEntity<SimpleUser> dealReceiveMessage(String message, SimpleUser simpleUser) {
        //接收到准备的消息
        if (Objects.equals(Integer.valueOf(message) , NiuniuAction.READY.getCode())) {
            simpleUser.setIsReady(Boolean.TRUE);
            return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.READY_SUCCESS);
        }else if (Objects.equals(Integer.valueOf(message) ,NiuniuAction.FAPAI.getCode())) {

        }
        return null;
    }

    /**
     * 处理开通了好友管理
     * @param niuniuRoomParameter
     * @param oneById
     * @param userInfo
     * @param roomId
     * @throws IOException
     */
    private JsonEntity<SimpleUser> isFriendManage(NiuniuRoomParameter niuniuRoomParameter ,RoomRecord oneById ,
                                UserInfo userInfo ,String roomId) throws IOException {
        //配置仅限好友
        if (niuniuRoomParameter.getSpecial().contains(SpecialEnum.JUST_FRIENDS.getCode())) {
            return this.justFriends(niuniuRoomParameter ,oneById ,userInfo ,roomId);
        }
        //未配置仅限好友
        else {
            return this.dealCanSee(roomId ,userInfo ,niuniuRoomParameter);
        }
    }


    /**
     * 处理是否是好友
     * @param niuniuRoomParameter
     * @param oneById
     * @param userInfo
     * @param roomId
     * @throws IOException
     */
    private JsonEntity<SimpleUser> justFriends(NiuniuRoomParameter niuniuRoomParameter ,RoomRecord oneById ,
                             UserInfo userInfo ,String roomId) throws IOException {
        Long count = friendManageMapper.countRoomAuthFriendAllow(oneById.getRoomAuth(), userInfo.getId());
        //不是房主的好友
        if (Objects.equals(count ,0L)) {
            return ResponseHelper.withErrorInstance(MessageCodeEnum.NOT_FRIEND);
        //是房主的好友
        }else {
            return this.dealCanSee(roomId ,userInfo ,niuniuRoomParameter);
        }
    }

    /**
     * 处理是否可以观战
     * @param roomId
     * @param userInfo
     * @param niuniuRoomParameter
     * @throws IOException
     */
    private JsonEntity<SimpleUser> dealCanSee(String roomId ,UserInfo userInfo ,NiuniuRoomParameter niuniuRoomParameter) throws IOException {
        String tempRoomId = roomId.intern();
        SimpleUser simpleUser = new SimpleUser(userInfo);
        Set<Session> sessions = niuniuRooms.get(Long.valueOf(roomId));
        //允许观战
        if (niuniuRoomParameter.getSpecial().contains(SpecialEnum.CAN_SEE.getCode())) {
            if (sessions.size() < RoomTypeEnum.getRoomNumByType(niuniuRoomParameter.getRoomType())) {
                simpleUser.setIsGuanZhan(Boolean.FALSE);
            }else {
                simpleUser.setIsGuanZhan(Boolean.TRUE);
            }
            return ResponseHelper.createInstance(simpleUser ,MessageCodeEnum.JOIN_ROOM);
            //不允许观战
        }else {
            if (sessions.size() < RoomTypeEnum.getRoomNumByType(niuniuRoomParameter.getRoomType())) {
                simpleUser.setIsGuanZhan(Boolean.FALSE);
                return ResponseHelper.createInstance(simpleUser ,MessageCodeEnum.JOIN_ROOM);
            }else {
                return ResponseHelper.createInstance(simpleUser ,MessageCodeEnum.ROOM_FULL);
            }

        }
    }
}
