package com.trevor.service.niuniu;

import com.alibaba.fastjson.JSON;
import com.trevor.bo.JsonEntity;
import com.trevor.bo.ResponseHelper;
import com.trevor.bo.SocketSessionUser;
import com.trevor.bo.WebSessionUser;
import com.trevor.common.FriendManageEnum;
import com.trevor.common.MessageCodeEnum;
import com.trevor.common.RoomTypeEnum;
import com.trevor.common.SpecialEnum;
import com.trevor.dao.FriendManageMapper;
import com.trevor.domain.RoomRecord;
import com.trevor.service.cache.RoomRecordCacheService;
import com.trevor.service.createRoom.bo.NiuniuRoomParameter;
import com.trevor.service.niuniu.bo.NiuniuAction;
import com.trevor.service.user.UserService;
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
    private UserService userService;

    /**
     * 在websocket连接时检查房间是否存在以及房间人数是否已满
     * @param roomId
     * @return
     */
    @Override
    public JsonEntity<SocketSessionUser> onOpenCheck(String roomId , WebSessionUser webSessionUser) throws IOException {
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
        Boolean isFriendManage = Objects.equals(userService.isFriendManage(oneById.getRoomAuth()) , FriendManageEnum.YES.getCode());
        //开通
        if (isFriendManage) {
            return this.isFriendManage(niuniuRoomParameter ,oneById , webSessionUser,roomId);
        // 未开通
        }else {
            return this.dealCanSee(roomId , webSessionUser,niuniuRoomParameter);
        }
    }

    /**
     * 处理接受到的新消息
     * @param message
     * @param socketSessionUser
     * @return
     */
    @Override
    public JsonEntity<SocketSessionUser> dealReceiveMessage(String message, SocketSessionUser socketSessionUser) {
        //接收到准备的消息
        if (Objects.equals(Integer.valueOf(message) ,NiuniuAction.READY.getCode())) {
            socketSessionUser.setIsReady(Boolean.TRUE);
            return ResponseHelper.createInstanceWithOutData(MessageCodeEnum.READY_SUCCESS);
        }else if (Objects.equals(Integer.valueOf(message) ,NiuniuAction.FAPAI.getCode())) {

        }
        return null;
    }

    /**
     * 处理开通了好友管理
     * @param niuniuRoomParameter
     * @param oneById
     * @param webSessionUser
     * @param roomId
     * @throws IOException
     */
    private JsonEntity<SocketSessionUser> isFriendManage(NiuniuRoomParameter niuniuRoomParameter , RoomRecord oneById ,
                                                         WebSessionUser webSessionUser, String roomId) throws IOException {
        //配置仅限好友
        if (niuniuRoomParameter.getSpecial().contains(SpecialEnum.JUST_FRIENDS.getCode())) {
            return this.justFriends(niuniuRoomParameter ,oneById , webSessionUser,roomId);
        }
        //未配置仅限好友
        else {
            return this.dealCanSee(roomId , webSessionUser,niuniuRoomParameter);
        }
    }


    /**
     * 处理是否是好友
     * @param niuniuRoomParameter
     * @param oneById
     * @param webSessionUser
     * @param roomId
     * @throws IOException
     */
    private JsonEntity<SocketSessionUser> justFriends(NiuniuRoomParameter niuniuRoomParameter , RoomRecord oneById ,
                                                      WebSessionUser webSessionUser, String roomId) throws IOException {
        Long count = friendManageMapper.countRoomAuthFriendAllow(oneById.getRoomAuth(), webSessionUser.getId());
        //不是房主的好友
        if (Objects.equals(count ,0L)) {
            return ResponseHelper.withErrorInstance(MessageCodeEnum.NOT_FRIEND);
        //是房主的好友
        }else {
            return this.dealCanSee(roomId , webSessionUser,niuniuRoomParameter);
        }
    }

    /**
     * 处理是否可以观战
     * @param roomId
     * @param webSessionUser
     * @param niuniuRoomParameter
     * @throws IOException
     */
    private JsonEntity<SocketSessionUser> dealCanSee(String roomId , WebSessionUser webSessionUser, NiuniuRoomParameter niuniuRoomParameter) throws IOException {
        SocketSessionUser socketSessionUser = new SocketSessionUser(webSessionUser);
        Set<Session> sessions = niuniuRooms.get(Long.valueOf(roomId));
        //允许观战
        if (niuniuRoomParameter.getSpecial().contains(SpecialEnum.CAN_SEE.getCode())) {
            if (sessions.size() < RoomTypeEnum.getRoomNumByType(niuniuRoomParameter.getRoomType())) {
                socketSessionUser.setIsGuanZhan(Boolean.FALSE);
            }else {
                socketSessionUser.setIsGuanZhan(Boolean.TRUE);
            }
            return ResponseHelper.createInstance(socketSessionUser,MessageCodeEnum.JOIN_ROOM);
            //不允许观战
        }else {
            if (sessions.size() < RoomTypeEnum.getRoomNumByType(niuniuRoomParameter.getRoomType())) {
                socketSessionUser.setIsGuanZhan(Boolean.FALSE);
                return ResponseHelper.createInstance(socketSessionUser,MessageCodeEnum.JOIN_ROOM);
            }else {
                return ResponseHelper.createInstance(socketSessionUser,MessageCodeEnum.ROOM_FULL);
            }

        }
    }
}
