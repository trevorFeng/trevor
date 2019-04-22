package com.trevor.websocket.niuniu;

import com.alibaba.fastjson.JSON;
import com.trevor.bo.RoomPoke;
import com.trevor.bo.UserPoke;
import com.trevor.common.FriendManageEnum;
import com.trevor.common.MessageCodeEnum;
import com.trevor.common.RoomTypeEnum;
import com.trevor.common.SpecialEnum;
import com.trevor.dao.FriendManageMapper;
import com.trevor.domain.RoomRecord;
import com.trevor.domain.User;
import com.trevor.service.RoomRecordCacheService;
import com.trevor.service.createRoom.bo.NiuniuRoomParameter;
import com.trevor.service.user.UserService;
import com.trevor.web.websocket.bo.Action;
import com.trevor.web.websocket.bo.ReturnMessage;
import com.trevor.websocket.bo.SocketUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.IOException;
import java.util.*;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-06 22:28
 **/
@Service
public class NiuniuServiceImpl implements NiuniuService {

    @Resource(name = "niuniuRooms")
    private Map<Long ,Set<Session>> niuniuRooms;

    @Resource(name = "niuniuRoomPoke")
    private Map<Long , RoomPoke> roomPokeMap;

    @Resource
    private RoomRecordCacheService roomRecordCacheService;

    @Resource
    private FriendManageMapper friendManageMapper;

    @Resource
    private UserService userService;

    /**
     * 在websocket连接时检查房间是否存在以及房间人数是否已满
     * @param roomId
     * @return
     */
    @Override
    public ReturnMessage<SocketUser> onOpenCheck(String roomId , User user) throws IOException {
        //查出房间配置
        RoomRecord oneById = roomRecordCacheService.findOneById(Long.valueOf(roomId));
        if (oneById == null) {
            return new ReturnMessage<>(MessageCodeEnum.ROOM_NOT_EXIST);
        }
        //房间已关闭
        if (niuniuRooms.get(Long.valueOf(roomId)) == null) {
            return new ReturnMessage<>(MessageCodeEnum.ROOM_CLOSE);
        }
        NiuniuRoomParameter niuniuRoomParameter = JSON.parseObject(oneById.getRoomConfig() ,NiuniuRoomParameter.class);
        //房主是否开启好友管理功能
        Boolean isFriendManage = Objects.equals(userService.isFriendManage(oneById.getRoomAuth()) , FriendManageEnum.YES.getCode());
        //开通
        if (isFriendManage) {
            return this.isFriendManage(niuniuRoomParameter ,oneById , user,roomId);
        // 未开通
        }else {
            return this.dealCanSee(roomId , user,niuniuRoomParameter);
        }
    }

    /**
     * 处理准备的消息
     * @param socketUser
     * @return
     */
    @Override
    public ReturnMessage<Object> dealReadyMessage(SocketUser socketUser ,Long roomId) {
        RoomPoke roomPoke = roomPokeMap.get(roomId);
        if (Objects.equals(roomPoke.getIsReadyOver() ,true)) {
            return new ReturnMessage<Object>(MessageCodeEnum.ERROR_NUM_MAX);
        }
        List<Map<Long , UserPoke>> userPokes = roomPoke.getUserPokes();
        roomPoke.getLock().lock();
        //初始化
        if (roomPoke.getLastNum() == 0) {
            if (userPokes.get(roomPoke.getLastNum()) == null) {
                Map<Long , UserPoke> map = new HashMap<>(2<<4);
                userPokes.add(map);
            }
        }else if (userPokes.get(roomPoke.getLastNum()-1) == null){
            Map<Long , UserPoke> map = new HashMap<>(2<<4);
            userPokes.add(map);
        }
        UserPoke userPoke = new UserPoke();
        userPoke.setUserId(socketUser.getId());
        userPoke.setIsReady(Boolean.TRUE);
        if (roomPoke.getLastNum() == 0) {
            userPoke.setTotalScore(0);
        }else {
            userPoke.setTotalScore(userPokes.get(roomPoke.getLastNum()-1).get());
        }
        roomPoke.setUserReadyNum(roomPoke.getUserReadyNum() + 1);

        //开始5s倒计时
        if (roomPoke.getUserReadyNum() == 2) {
            roomPoke.getLock().unlock();
            //通知线程开始打牌任务
            countdownTask.coundDown(niuniuRooms.get(roomId) ,roomPoke);
            //倒计时结束，先发4张牌，等待闲家下注


            //
        }else {
            roomPoke.getLock().unlock();
        }
    }

    private

    /**
     * 处理开通了好友管理
     * @param niuniuRoomParameter
     * @param oneById
     * @param roomId
     * @throws IOException
     */
    private ReturnMessage<SocketUser> isFriendManage(NiuniuRoomParameter niuniuRoomParameter , RoomRecord oneById ,
                                                         User user, String roomId) throws IOException {
        //配置仅限好友
        if (niuniuRoomParameter.getSpecial().contains(SpecialEnum.JUST_FRIENDS.getCode())) {
            return this.justFriends(niuniuRoomParameter ,oneById , user,roomId);
        }
        //未配置仅限好友
        else {
            return this.dealCanSee(roomId , user,niuniuRoomParameter);
        }
    }


    /**
     * 处理是否是好友
     * @param niuniuRoomParameter
     * @param oneById
     * @param roomId
     * @throws IOException
     */
    private ReturnMessage<SocketUser> justFriends(NiuniuRoomParameter niuniuRoomParameter , RoomRecord oneById ,
                                                      User user, String roomId) throws IOException {
        Long count = friendManageMapper.countRoomAuthFriendAllow(oneById.getRoomAuth(), user.getId());
        //不是房主的好友
        if (Objects.equals(count ,0L)) {
            return new ReturnMessage<>(MessageCodeEnum.NOT_FRIEND);
        //是房主的好友
        }else {
            return this.dealCanSee(roomId , user,niuniuRoomParameter);
        }
    }

    /**
     * 处理是否可以观战
     * @param roomId
     * @param niuniuRoomParameter
     * @throws IOException
     */
    private ReturnMessage<SocketUser> dealCanSee(String roomId , User user, NiuniuRoomParameter niuniuRoomParameter) throws IOException {
        SocketUser socketUser = new SocketUser();
        socketUser.setId(user.getId());
        socketUser.setName(user.getAppName());
        socketUser.setPicture(user.getAppPictureUrl());
        Set<Session> sessions = niuniuRooms.get(Long.valueOf(roomId));
        //允许观战
        if (niuniuRoomParameter.getSpecial().contains(SpecialEnum.CAN_SEE.getCode())) {
            if (sessions.size() < RoomTypeEnum.getRoomNumByType(niuniuRoomParameter.getRoomType())) {
                socketUser.setIsChiGuaPeople(Boolean.FALSE);
            }else {
                socketUser.setIsChiGuaPeople(Boolean.TRUE);
            }
            return new ReturnMessage<SocketUser>(socketUser, Action.ENTER_ROOM.getCode());
            //不允许观战
        }else {
            if (sessions.size() < RoomTypeEnum.getRoomNumByType(niuniuRoomParameter.getRoomType())) {
                socketUser.setIsChiGuaPeople(Boolean.FALSE);
                return new ReturnMessage<SocketUser>(socketUser, Action.ENTER_ROOM.getCode());
            }else {
                return new ReturnMessage<>(MessageCodeEnum.ROOM_FULL);
            }

        }
    }
}
