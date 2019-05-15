package com.trevor.websocket.niuniu;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.trevor.bo.*;
import com.trevor.common.FriendManageEnum;
import com.trevor.common.MessageCodeEnum;
import com.trevor.common.RoomTypeEnum;
import com.trevor.common.SpecialEnum;
import com.trevor.dao.FriendManageMapper;
import com.trevor.dao.GameSituationMapper;
import com.trevor.domain.GameSituation;
import com.trevor.domain.RoomRecord;
import com.trevor.domain.User;
import com.trevor.service.RoomRecordCacheService;
import com.trevor.service.createRoom.bo.NiuniuRoomParameter;
import com.trevor.service.user.UserService;
import com.trevor.util.PokeUtil;
import com.trevor.util.RandomUtils;
import com.trevor.util.WebsocketUtil;
import com.trevor.websocket.bo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
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
    private Map<Long ,CopyOnWriteArrayList<Session>> sessionsMap;

    @Resource(name = "roomPokeMap")
    private Map<Long , RoomPoke> roomPokeMap;

    @Resource(name = "executor")
    private Executor executor;

    @Resource
    private RoomRecordCacheService roomRecordCacheService;

    @Resource
    private FriendManageMapper friendManageMapper;

    @Resource
    private UserService userService;

    @Resource
    private GameSituationMapper gameSituationMapper;

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
            return this.dealCanSee(roomId , user,niuniuRoomParameter);
        }
    }

    /**
     * 处理准备的消息
     * @param socketUser
     * @return
     */
    @Override
    public void dealReadyMessage(SocketUser socketUser ,Long roomId) throws IOException, EncodeException {
        RoomPoke roomPoke = roomPokeMap.get(roomId);
        if (Objects.equals(roomPoke.getIsReadyOver() ,true)) {
            return;
        }
        List<UserPokesIndex> userPokes = roomPoke.getUserPokes();
        CopyOnWriteArrayList<Session> sessions = sessionsMap.get(roomId);

        roomPoke.getLock().lock();
        //算上这一局是第几局
        roomPoke.setRuningNum(roomPoke.getRuningNum()+1);

        //设置userPokesIndex
        List<UserPokesIndex> userPokesIndexList = roomPoke.getUserPokes();
        if (!userPokesIndexList.isEmpty()) {
            for (UserPokesIndex userPokesIndex : userPokesIndexList) {
                if (Objects.equals(userPokesIndex.getIndex() ,roomPoke.getRuningNum())) {
                    UserPoke userPoke = new UserPoke();
                    userPoke.setUserId(socketUser.getId());
                    userPokesIndex.getUserPokeList().add(userPoke);
                    break;
                }
            }
        }else {
            UserPokesIndex userPokesIndex = new UserPokesIndex();
            userPokesIndex.setIndex(roomPoke.getRuningNum());
            UserPoke userPoke = new UserPoke();
            userPoke.setUserId(socketUser.getId());
            userPokesIndex.getUserPokeList().add(userPoke);
            userPokesIndexList.add(userPokesIndex);
        }


        //设置userScores
        List<Long> userScoreIds = roomPoke.getUserScores().stream().map(userScore -> userScore.getUserId()).collect(Collectors.toList());
        if (!userScoreIds.contains(socketUser.getId())) {
            UserScore userScore = new UserScore();
            userScore.setUserId(socketUser.getId());
            roomPoke.getUserScores().add(userScore);
        }

        //是否准备的人数为两人，是则开始自动打牌
        UserPokesIndex thisUserPokesIndex = null;
        for (UserPokesIndex userPokesIndex : userPokesIndexList) {
            if (Objects.equals(userPokesIndex.getIndex() ,roomPoke.getRuningNum())) {
                thisUserPokesIndex = userPokesIndex;
                break;
            }
        }
        if (thisUserPokesIndex.getUserPokeList().size() == 2) {
            roomPoke.getLock().unlock();
            executor.execute(() -> {
                try {
                    playPoke(roomId);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            });
        }else {
            roomPoke.getLock().unlock();
        }
        ReturnMessage<String> returnMessage = new ReturnMessage<>(null ,2);
        WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage);
    }

    /**
     * 处理抢庄的消息
     */
    @Override
    public void dealQiangZhuangMessage(SocketUser socketUser , Long roomId , ReceiveMessage receiveMessage){
        UserPoke userPoke = getUserPoke(roomId ,socketUser);
        userPoke.setIsQiangZhuang(Boolean.TRUE);
        userPoke.setQiangZhuangMultiple(receiveMessage.getQiangZhuangMultiple());
    }

    /**
     * 处理闲家下注的消息
     */
    @Override
    public void dealXianJiaXiaZhuMessage(SocketUser socketUser , Long roomId , ReceiveMessage receiveMessage){
        UserPoke userPoke = getUserPoke(roomId ,socketUser);
        userPoke.setXianJiaMultiple(receiveMessage.getXianJiaMultiple());
    }

    /**
     * 得到玩家userPoke
     * @param roomId
     * @param socketUser
     * @return
     */
    private UserPoke getUserPoke(Long roomId ,SocketUser socketUser){
        RoomPoke roomPoke = roomPokeMap.get(roomId);
        List<Map<Long , UserPoke>> userPokes = roomPoke.getUserPokes();
        Map<Long ,UserPoke> userPokeMap = userPokes.get(roomPoke.getRuningNum()-1);
        UserPoke userPoke = userPokeMap.get(socketUser.getId());
        return userPoke;
    }

    /**
     * 自动开始打牌任务
     * @param rommId
     * @throws InterruptedException
     * @throws EncodeException
     * @throws IOException
     */
    private void playPoke(Long rommId) throws InterruptedException, EncodeException, IOException {
        CopyOnWriteArrayList<Session> sessions = sessionsMap.get(rommId);
        //准备的倒计时
        countDown(true ,sessions ,roomPokeMap.get(rommId));
        //先发四张牌
        RoomPoke roomPoke = roomPokeMap.get(rommId);
        Map<Long, UserPoke> userPokeMap = roomPoke.getUserPokes().get(roomPoke.getRuningNum() - 1);
        List<String> pokes = PokeUtil.generatePoke5();
        List<List<Integer>> lists = RandomUtils.getSplitListByMax(userPokeMap.size() * 5);
        int i = 0;
        for (Map.Entry<Long, UserPoke> entry : userPokeMap.entrySet()) {
            List<Integer> list = lists.get(i);
            List<String> userPokes = Lists.newArrayList();
            list.forEach(index -> {
                userPokes.add(pokes.get(index));
            });
            entry.getValue().setPokes(userPokes);
            i++;
        }
        for (Map.Entry<Long, UserPoke> entry : userPokeMap.entrySet()) {
            for (Session session : sessions) {
                SocketUser socketUser = (SocketUser) session.getUserProperties().get(WebKeys.WEBSOCKET_USER_KEY);
                if (Objects.equals(entry.getKey() ,socketUser.getId())) {
                    ReturnMessage<List<String>> returnMessage3 = new ReturnMessage<>(entry.getValue().getPokes().subList(0,4),4);
                    WebsocketUtil.sendBasicMessage(session ,returnMessage3);
                }
            }
        }
        //WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage2);
//        //通知开始抢庄
//        ReturnMessage<String> returnMessage = new ReturnMessage<>("准备抢庄" ,3);
//        for (Session session : sessions) {
//            if (session.getUserProperties().get("ready") != null) {
//                WebsocketUtil.sendBasicMessage(session ,returnMessage);
//            }
//        }
        //开始抢庄倒计时
        countDown(false ,sessions ,roomPokeMap.get(rommId));
        //选取庄家
        List<Long> qiangZhuangUserIds = Lists.newArrayList();
        userPokeMap.forEach((id ,userPoke) -> {
            if (userPoke.getIsQiangZhuang()) {
                qiangZhuangUserIds.add(id);
            }
        });
        Integer randNum = RandomUtils.getRandNumMax(qiangZhuangUserIds.size());
        userPokeMap.get(qiangZhuangUserIds.get(randNum)).setIsQiangZhuang(Boolean.TRUE);
        ReturnMessage<Long> returnMessage1 = new ReturnMessage<>(qiangZhuangUserIds.get(randNum) ,5);
        WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage1);
        //闲家下注倒计时
        countDown(false ,sessions ,roomPokeMap.get(rommId));
        //再发一张牌
        for (Map.Entry<Long, UserPoke> entry : userPokeMap.entrySet()) {
            for (Session session : sessions) {
                SocketUser socketUser = (SocketUser) session.getUserProperties().get(WebKeys.WEBSOCKET_USER_KEY);
                if (Objects.equals(entry.getKey() ,socketUser.getId())) {
                    ReturnMessage<String> returnMessage3 = new ReturnMessage<>(entry.getValue().getPokes().get(4) ,6);
                    WebsocketUtil.sendBasicMessage(session ,returnMessage3);
                }
            }
        }
        //准备摊牌倒计时
        countDown(false ,sessions ,roomPokeMap.get(rommId));
        //计算分数得失
        UserPoke zhuangJia = null;
        for (Map.Entry<Long, UserPoke> entry : userPokeMap.entrySet()) {
            if (entry.getValue().getIsQiangZhuang()) {
                zhuangJia = entry.getValue();
                break;
            }
        }
        Integer zhuangJiaNiu = isNiuNiu(zhuangJia.getPokes());
        for (Map.Entry<Long, UserPoke> entry : userPokeMap.entrySet()) {
            UserPoke xianJia = entry.getValue();
            if (!xianJia.getIsQiangZhuang()) {
                Integer xianJiaNiu = isNiuNiu(xianJia.getPokes());
                Integer score = zhuangJia.getQiangZhuangMultiple() * xianJia.getXianJiaMultiple();
                if (zhuangJiaNiu > xianJiaNiu) {
                    zhuangJia.setThisScore(score);
                    xianJia.setThisScore(-score);
                }else if (zhuangJiaNiu < xianJiaNiu) {
                    zhuangJia.setThisScore(-score);
                    xianJia.setThisScore(score);
                }else if (zhuangJiaNiu == 10) {
                    zhuangJia.setThisScore(score);
                    xianJia.setThisScore(-score);
                }else if (zhuangJiaNiu == 0){
                    Boolean isZhuangJiaBoss = Boolean.TRUE;
                    List<String> zhuangJiaPokes = zhuangJia.getPokes();
                    List<String> xianJiaPokes = xianJia.getPokes();
                    List<Integer> zhuangJiaNums = zhuangJiaPokes.stream().map(str -> Integer.valueOf(str.indexOf(1))
                    ).collect(Collectors.toList());
                    List<Integer> xianJiaNums = xianJiaPokes.stream().map(str -> Integer.valueOf(str.indexOf(1))
                    ).collect(Collectors.toList());
                    zhuangJiaNums.sort(Comparator.reverseOrder());
                    xianJiaNums.sort(Comparator.reverseOrder());
                    for (int j = 0; j < xianJiaNums.size(); j++) {
                        if (xianJiaNums.get(i) > zhuangJiaNums.get(i)) {
                            isZhuangJiaBoss = Boolean.FALSE;
                            break;
                        }
                    }
                    if (isZhuangJiaBoss) {
                        zhuangJia.setThisScore(score);
                        xianJia.setThisScore(-score);
                    }else {
                        zhuangJia.setThisScore(-score);
                        xianJia.setThisScore(score);
                    }
                }

            }
        }
        Map<Long ,Integer> scoreMap = roomPoke.getScoreMap();
        for (Map.Entry<Long, UserPoke> entry : userPokeMap.entrySet()) {
            if (scoreMap.get(entry.getKey()) == null) {
                scoreMap.put(entry.getKey() ,entry.getValue().getThisScore());
            }else {
                scoreMap.put(entry.getKey() ,scoreMap.get(entry.getKey()) + entry.getValue().getThisScore());
            }
        }
        //返回计算的结果
        List<NiuNiuResult> niuNiuResultList = Lists.newArrayList();
        for (Map.Entry<Long, UserPoke> entry : userPokeMap.entrySet()) {
            NiuNiuResult niuNiuResult = new NiuNiuResult();
            niuNiuResult.setUserId(entry.getKey());
            niuNiuResult.setScore(entry.getValue().getThisScore());
            niuNiuResult.setTotal(scoreMap.get(entry.getKey()));
        }
        ReturnMessage<List<NiuNiuResult>> returnMessage3 = new ReturnMessage<>(niuNiuResultList ,7);
        WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage3);

        GameSituation gameSituation = new GameSituation();
        gameSituation.setRoomRecordId(rommId);
        gameSituation.setGameNum(roomPoke.getRuningNum());
        //保存结果
        List<NiuniuSituation> niuniuSituations = Lists.newArrayList();
        for (Map.Entry<Long ,UserPoke> entry : userPokeMap.entrySet()) {
            NiuniuSituation niuniuSituation = new NiuniuSituation();
            UserPoke userPoke = entry.getValue();
            niuniuSituation.setIsZhuangJia(userPoke.getIsZhuangJia());
            niuniuSituation.setPokes(userPoke.getPokes());
            if (userPoke.getIsQiangZhuang()) {
                niuniuSituation.setBeiShu(userPoke.getQiangZhuangMultiple());
            }else {
                niuniuSituation.setBeiShu(userPoke.getXianJiaMultiple());
            }
            niuniuSituation.setPokesDesc("牛牛");

            niuniuSituations.add(niuniuSituation);
        }
        String niuniuSituationsStr = JSON.toJSONString(niuniuSituations);
        gameSituation.setGameSituation(niuniuSituationsStr);
        gameSituationMapper.insertOne(gameSituation);
    }


    /**
     * 倒计时
     */
    protected void countDown(Boolean iseady ,CopyOnWriteArrayList<Session> sessions , RoomPoke roomPoke) throws InterruptedException, IOException, EncodeException {
        for (int i = 5; i > 0 ; i--) {
            ReturnMessage<Integer> returnMessage = new ReturnMessage<>(i ,3);
            WebsocketUtil.sendAllBasicMessage(sessions , returnMessage);
            Thread.sleep(1000);
        }
        if (iseady) {
            roomPoke.setIsReadyOver(true);
        }
    }

    /**
     * 判断玩家的是否为牛
     * @param pokes
     * @return
     */
    private Integer isNiuNiu(List<String> pokes){
        int ii = 0;
        int jj = 0;
        int kk = 0;
        boolean isNiu = Boolean.FALSE;
        for (int i = 0; i < pokes.size(); i++) {
            if (i > 3) {
                break;

            }
            for (int j = i+1; j < pokes.size(); j++) {
                for (int k = j+1; k < pokes.size(); k++) {
                    int num = Integer.valueOf(pokes.get(i).indexOf(1)) +
                            Integer.valueOf(pokes.get(j).indexOf(1)) +
                            Integer.valueOf(pokes.get(k).indexOf(1));
                    if (num == 10 || num == 20 || num == 30) {
                        ii = i;
                        jj = j;
                        kk = k;
                        isNiu = Boolean.TRUE;
                        break;
                    }
                }
            }
        }
        if (!isNiu) {
            return NiuNiuPaiXingEnum.NIU_0.getPaiXingCode();
        }else {
            int num = 0;
            for (int i = 0; i < pokes.size(); i++) {
                if (i != ii && i != jj && i != kk) {
                    num += Integer.valueOf(pokes.get(i).indexOf(1));
                }
            }
            if (num == 10 || num == 20) {
                return NiuNiuPaiXingEnum.NIU_10.getPaiXingCode();
            }
            if (num < 10) {
                return num;
            }
            if (num > 10) {
                return num - 10;
            }
            return num;
        }
    }


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
        CopyOnWriteArrayList<Session> sessions = sessionsMap.get(Long.valueOf(roomId));
        //允许观战
        if (niuniuRoomParameter.getSpecial()!= null && niuniuRoomParameter.getSpecial().contains(SpecialEnum.CAN_SEE.getCode())) {
            if (sessions.size() < RoomTypeEnum.getRoomNumByType(niuniuRoomParameter.getRoomType())) {
                socketUser.setIsGuanZhong(false);
                socketUser.setIsChiGuaPeople(Boolean.FALSE);
            }else {
                socketUser.setIsChiGuaPeople(Boolean.TRUE);
            }
            return new ReturnMessage<>(socketUser, 1);
            //不允许观战
        }else {
            if (sessions.size() < RoomTypeEnum.getRoomNumByType(niuniuRoomParameter.getRoomType())) {
                socketUser.setIsGuanZhong(false);
                socketUser.setIsChiGuaPeople(Boolean.FALSE);
                return new ReturnMessage<>(socketUser, 1);
            }else {
                return new ReturnMessage<>(MessageCodeEnum.ROOM_FULL);
            }

        }
    }
}
