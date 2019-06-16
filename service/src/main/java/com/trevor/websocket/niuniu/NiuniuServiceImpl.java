package com.trevor.websocket.niuniu;

import com.alibaba.fastjson.JSON;
import com.trevor.bo.*;
import com.trevor.dao.FriendManageMapper;
import com.trevor.domain.Room;
import com.trevor.domain.User;
import com.trevor.enums.FriendManageEnum;
import com.trevor.enums.MessageCodeEnum;
import com.trevor.enums.RoomTypeEnum;
import com.trevor.enums.SpecialEnum;
import com.trevor.play.NiuniuPlay;
import com.trevor.service.RoomService;
import com.trevor.service.createRoom.bo.NiuniuRoomParameter;
import com.trevor.service.user.UserService;
import com.trevor.util.WebsocketUtil;
import com.trevor.websocket.bo.ReceiveMessage;
import com.trevor.websocket.bo.ReturnMessage;
import com.trevor.websocket.bo.SocketUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-06 22:28
 **/
@Service
@Slf4j
public class NiuniuServiceImpl implements NiuniuService {

    @Resource(name = "sessionsMap")
    private Map<Long ,Set<Session>> sessionsMap;

    @Resource(name = "roomPokeMap")
    private Map<Long , RoomPoke> roomPokeMap;

    @Resource(name = "executor")
    private Executor executor;

    @Resource
    private RoomService roomService;

    @Resource
    private FriendManageMapper friendManageMapper;

    @Resource
    private UserService userService;

    @Resource
    private NiuniuPlay niuniuPlay;



    /**
     * 在websocket连接时检查房间是否存在以及房间人数是否已满
     * @param roomId
     * @return
     */
    @Override
    public ReturnMessage<SocketUser> onOpenCheck(String roomId ,User user) throws IOException {
        //查出房间配置
        Room oneById = roomService.findOneById(Long.valueOf(roomId));
        if (oneById == null) {
            return new ReturnMessage<>(MessageCodeEnum.ROOM_NOT_EXIST);
        }
        //房间已关闭
        if (Objects.equals(oneById.getState() ,0)) {
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
            return this.dealCanSee( user ,niuniuRoomParameter ,roomPokeMap.get(Long.valueOf(roomId)));
        }
    }

    /**
     * 处理准备的消息
     * @param socketUser
     * @return
     */
    @Override
    public void dealReadyMessage(SocketUser socketUser ,Long roomId){
        RoomPoke roomPoke = roomPokeMap.get(roomId);
        //是否准备结束
        if (Objects.equals(roomPoke.getIsReadyOver() ,true)) {
            return;
        }
        Set<Session> sessions = sessionsMap.get(roomId);
        //给所有人发自己加入准备的消息
        ReturnMessage<Long> returnMessage = new ReturnMessage<>(socketUser.getId() ,2);
        //加读锁
        roomPoke.getLock().readLock().lock();
        WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage);
        roomPoke.getLock().readLock().unlock();

        String tempRoomId = String.valueOf(roomId).intern();
        synchronized (tempRoomId) {
            //初始化roomPoke
            initReadyMessage(roomPoke ,socketUser);
            //是否准备的人数为两人，是则开始自动打牌
            if (Objects.equals(roomPoke.getReadyNum() ,2)) {
                roomPoke.setRoomStatus(1);
                executor.execute(() -> {
                    try {
                        niuniuPlay.play(roomId);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("roomId :" + roomId ,e.toString());
                        ReturnMessage<Long> message = new ReturnMessage<>(MessageCodeEnum.SYSTEM_ERROT);
                        //加读锁
                        roomPoke.getLock().readLock().lock();
                        WebsocketUtil.sendAllBasicMessage(sessions ,message);
                        roomPoke.getLock().readLock().unlock();
                    }
                });
            }
        }
    }

    /**
     * 处理抢庄的消息
     */
    @Override
    public void dealQiangZhuangMessage(SocketUser socketUser ,Long roomId ,ReceiveMessage receiveMessage){
        UserPoke userPoke = getUserPoke(roomId ,socketUser);
        userPoke.setIsQiangZhuang(Boolean.TRUE);
        userPoke.setQiangZhuangMultiple(receiveMessage.getQiangZhuangMultiple());
        //给其他玩家发抢庄的消息
        QiangZhuangMessage qiangZhuangMessage = new QiangZhuangMessage();
        qiangZhuangMessage.setUserId(userPoke.getUserId());
        qiangZhuangMessage.setQiangZhuangMultiple(receiveMessage.getQiangZhuangMultiple());
        ReturnMessage<QiangZhuangMessage> returnMessage = new ReturnMessage<>(qiangZhuangMessage ,8);
        Set<Session> sessions = sessionsMap.get(roomId);
        RoomPoke roomPoke = roomPokeMap.get(roomId);
        //加读锁
        roomPoke.getLock().readLock().lock();
        WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage);
        roomPoke.getLock().readLock().unlock();
    }

    /**
     * 处理闲家下注的消息
     */
    @Override
    public void dealXianJiaXiaZhuMessage(Session mySession ,SocketUser socketUser ,Long roomId ,ReceiveMessage receiveMessage){
        UserPoke userPoke = getUserPoke(roomId ,socketUser);
        if (userPoke.getIsZhuangJia()) {
            ReturnMessage<String> returnMessage = new ReturnMessage<>("你不是闲家" ,-1);
            WebsocketUtil.sendBasicMessage(mySession ,returnMessage);
            return;
        }
        userPoke.setXianJiaMultiple(receiveMessage.getXianJiaMultiple());
        //给其他玩家发抢庄的消息
        XianJiaXiaZhuMessage xianJiaXiaZhuMessage = new XianJiaXiaZhuMessage();
        xianJiaXiaZhuMessage.setUserId(userPoke.getUserId());
        xianJiaXiaZhuMessage.setXianJiaXiaZhuMultiple(receiveMessage.getXianJiaMultiple());
        ReturnMessage<XianJiaXiaZhuMessage> returnMessage = new ReturnMessage<>(xianJiaXiaZhuMessage ,9);
        RoomPoke roomPoke = roomPokeMap.get(roomId);
        Set<Session> sessions = sessionsMap.get(roomId);
        //加读锁
        roomPoke.getLock().readLock().lock();
        WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage);
        roomPoke.getLock().readLock().unlock();
    }

    /**
     * 处理摊牌的消息
     * @param socketUser
     */
    @Override
    public void dealTanPaiMessage(SocketUser socketUser ,Long roomId){
        Room room = roomService.findOneById(roomId);
        NiuniuRoomParameter niuniuRoomParameter = JSON.parseObject(room.getRoomConfig() ,NiuniuRoomParameter.class);
        RoomPoke roomPoke = roomPokeMap.get(roomId);
        UserPoke userPoke = getUserPoke(roomId ,socketUser);
        TanPaiMessage tanPaiMessage = new TanPaiMessage();
        tanPaiMessage.setUserId(socketUser.getId());
        tanPaiMessage.setPokes(userPoke.getPokes());
        Integer paiXingCode = niuniuPlay.isNiuNiu(userPoke.getPokes() ,niuniuRoomParameter.getPaiXing() ,niuniuRoomParameter.getRule()).getPaixing();
        tanPaiMessage.setPaiXing(paiXingCode);
        userPoke.setIsTanPai(Boolean.TRUE);
        ReturnMessage<TanPaiMessage> returnMessage = new ReturnMessage<>(tanPaiMessage ,10);
        Set<Session> sessions = sessionsMap.get(roomId);
        //加读锁
        roomPoke.getLock().readLock().lock();
        WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage);
        roomPoke.getLock().readLock().unlock();
    }

    /**
     * 得到玩家userPoke
     * @param roomId
     * @param socketUser
     * @return
     */
    private UserPoke getUserPoke(Long roomId ,SocketUser socketUser){
        RoomPoke roomPoke = roomPokeMap.get(roomId);
        List<UserPokesIndex> userPokesIndexList = roomPoke.getUserPokes();
        UserPokesIndex userPokesIndex = userPokesIndexList.stream().filter(u -> Objects.equals(u.getIndex(), roomPoke.getRuningNum()))
                .collect(Collectors.toList()).get(0);
        UserPoke userPoke = userPokesIndex.getUserPokeList().stream().filter(u -> Objects.equals(socketUser.getId(), u.getUserId()))
                .collect(Collectors.toList()).get(0);
        return userPoke;
    }






    /**
     * 处理开通了好友管理
     * @param niuniuRoomParameter
     * @param oneById
     * @param roomId
     * @throws IOException
     */
    private ReturnMessage<SocketUser> isFriendManage(NiuniuRoomParameter niuniuRoomParameter , Room oneById ,
                                                     User user , String roomId) throws IOException {
        //配置仅限好友
        if (niuniuRoomParameter.getSpecial().contains(SpecialEnum.JUST_FRIENDS.getCode())) {
            return this.justFriends(niuniuRoomParameter ,oneById , user,roomId);
        }
        //未配置仅限好友
        else {
            return this.dealCanSee(user ,niuniuRoomParameter ,roomPokeMap.get(Long.valueOf(roomId)));
        }
    }


    /**
     * 处理是否是好友
     * @param niuniuRoomParameter
     * @param oneById
     * @param roomId
     * @throws IOException
     */
    private ReturnMessage<SocketUser> justFriends(NiuniuRoomParameter niuniuRoomParameter , Room oneById ,
                                                      User user, String roomId){
        Long count = friendManageMapper.countRoomAuthFriendAllow(oneById.getRoomAuth(), user.getId());
        //不是房主的好友
        if (Objects.equals(count ,0L)) {
            return new ReturnMessage<>(MessageCodeEnum.NOT_FRIEND);
        //是房主的好友
        }else {
            return this.dealCanSee(user ,niuniuRoomParameter ,roomPokeMap.get(oneById.getId()));
        }
    }

    /**
     * 处理是否可以观战
     * @param niuniuRoomParameter
     * @throws IOException
     */
    private ReturnMessage<SocketUser> dealCanSee(User user,
                                                 NiuniuRoomParameter niuniuRoomParameter ,RoomPoke roomPoke){
        SocketUser socketUser = new SocketUser();
        socketUser.setId(user.getId());
        socketUser.setName(user.getAppName());
        socketUser.setPicture(user.getAppPictureUrl());
        //允许观战
        if (niuniuRoomParameter.getSpecial()!= null && niuniuRoomParameter.getSpecial().contains(SpecialEnum.CAN_SEE.getCode())) {
            if (roomPoke.getRealWanJiaIds().size() < RoomTypeEnum.getRoomNumByType(niuniuRoomParameter.getRoomType())) {
                socketUser.setIsGuanZhong(false);
                socketUser.setIsChiGuaPeople(Boolean.FALSE);
            }else {
                socketUser.setIsChiGuaPeople(Boolean.TRUE);
            }
            return new ReturnMessage<>(socketUser, 1);
            //不允许观战
        }else {
            if (roomPoke.getRealWanJiaIds().size() < RoomTypeEnum.getRoomNumByType(niuniuRoomParameter.getRoomType())) {
                socketUser.setIsGuanZhong(false);
                socketUser.setIsChiGuaPeople(Boolean.FALSE);
                return new ReturnMessage<>(socketUser, 1);
            }else {
                return new ReturnMessage<>(MessageCodeEnum.ROOM_FULL);
            }

        }
    }


    /**
     *
     * @param roomPoke
     * @param socketUser
     * @return
     */
    private UserPokesIndex generateUserPokesIndex(RoomPoke roomPoke ,SocketUser socketUser){
        UserPokesIndex userPokesIndex = new UserPokesIndex();
        userPokesIndex.setIndex(roomPoke.getRuningNum());
        UserPoke userPoke = new UserPoke();
        userPoke.setUserId(socketUser.getId());
        userPokesIndex.getUserPokeList().add(userPoke);
        return userPokesIndex;
    }

    /**
     * 接受到ready的消息初始化
     * @param roomPoke
     * @param socketUser
     */
    private void initReadyMessage(RoomPoke roomPoke ,SocketUser socketUser){
        List<UserPokesIndex> userPokesIndexList = roomPoke.getUserPokes();
        //算上这一局是第几局
        if (Objects.equals(roomPoke.getReadyNum() ,0)) {
            roomPoke.setRuningNum(roomPoke.getRuningNum()+1);
        }
        //设置userPokesIndex
        if (!userPokesIndexList.isEmpty()) {
            if (Objects.equals(roomPoke.getReadyNum() ,0)) {
                UserPokesIndex userPokesIndex = generateUserPokesIndex(roomPoke ,socketUser);
                userPokesIndexList.add(userPokesIndex);
            }else {
                for (UserPokesIndex userPokesIndex : userPokesIndexList) {
                    if (Objects.equals(userPokesIndex.getIndex() ,roomPoke.getRuningNum())) {
                        UserPoke userPoke = new UserPoke();
                        userPoke.setUserId(socketUser.getId());
                        userPokesIndex.getUserPokeList().add(userPoke);
                        break;
                    }
                }
            }
        }else {
            UserPokesIndex userPokesIndex = generateUserPokesIndex(roomPoke ,socketUser);
            userPokesIndexList.add(userPokesIndex);
        }
        //设置userScores
        List<Long> userScoreIds = roomPoke.getUserScores().stream().map(userScore -> userScore.getUserId()).collect(Collectors.toList());
        if (!userScoreIds.contains(socketUser.getId())) {
            UserScore userScore = new UserScore();
            userScore.setUserId(socketUser.getId());
            roomPoke.getUserScores().add(userScore);
        }
        //设置准备的人数
        roomPoke.setReadyNum(roomPoke.getReadyNum() + 1);
    }
}
