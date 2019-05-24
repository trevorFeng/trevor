package com.trevor.play;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.trevor.bo.*;
import com.trevor.dao.GameSituationMapper;
import com.trevor.dao.RoomPokeInitMapper;
import com.trevor.domain.GameSituation;
import com.trevor.domain.RoomPokeInit;
import com.trevor.domain.RoomRecord;
import com.trevor.service.RoomRecordCacheService;
import com.trevor.service.createRoom.bo.NiuniuRoomParameter;
import com.trevor.util.PokeUtil;
import com.trevor.util.RandomUtils;
import com.trevor.util.WebsocketUtil;
import com.trevor.websocket.bo.NiuNiuPaiXingEnum;
import com.trevor.websocket.bo.NiuNiuResult;
import com.trevor.websocket.bo.ReturnMessage;
import com.trevor.websocket.bo.SocketUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NiuniuPlay {

    @Resource
    private RoomRecordCacheService roomRecordCacheService;

    @Resource
    private GameSituationMapper gameSituationMapper;

    @Resource(name = "sessionsMap")
    private Map<Long ,Set<Session>> sessionsMap;

    @Resource(name = "roomPokeMap")
    private Map<Long , RoomPoke> roomPokeMap;

    @Resource
    private RoomPokeInitMapper roomPokeInitMapper;

    @Transactional(rollbackFor = Exception.class)
    public void play(Long roomId){
        RoomRecord roomRecord = roomRecordCacheService.findOneById(roomId);
        NiuniuRoomParameter niuniuRoomParameter = JSON.parseObject(roomRecord.getRoomConfig() ,NiuniuRoomParameter.class);
        RoomPoke roomPoke = roomPokeMap.get(roomId);
        Set<Session> sessions = sessionsMap.get(roomId);
        //检查目前是多少局，是否本房间结束
        checkJuShu(niuniuRoomParameter ,roomPoke.getRuningNum());
        //准备的倒计时
        countDown(sessions ,roomPokeMap.get(roomId));
        //设置准备结束标识符
        roomPoke.setIsReadyOver(true);
        //先发四张牌
        List<UserPoke> userPokeList = roomPoke.getUserPokes().stream().filter(u -> Objects.equals(u.getIndex(), roomPoke.getRuningNum()))
                .collect(Collectors.toList()).get(0).getUserPokeList();
        fapai_4(roomPoke ,sessions ,userPokeList);
        //开始抢庄倒计时
        countDown(sessions ,roomPokeMap.get(roomId));
        //选取庄家
        selectZhaungJia(roomPoke ,sessions ,userPokeList);
        //闲家下注倒计时
        countDown(sessions ,roomPokeMap.get(roomId));
        //再发一张牌
        fapai_1(roomPoke ,sessions ,userPokeList);
        //准备摊牌倒计时
        countDown(sessions ,roomPokeMap.get(roomId));
        //计算/设置本局玩家的分数
        setScore(roomPoke ,userPokeList);
        //保存roomPoke
        saveRoomPoke(roomPoke);
        //给玩家发返回计算的结果
        returnResultToUser(sessions ,roomPoke ,userPokeList);
        //改变房间状态
        roomPoke.setReadyNum(0);
        roomPoke.setRoomStatus(0);
        roomPoke.setIsReadyOver(false);
        //保存结果
        saveResult(roomId ,roomPoke ,userPokeList);

    }

    /**
     * 倒计时
     */
    protected void countDown(Set<Session> sessions , RoomPoke roomPoke) {
        //加读锁
        roomPoke.getLock().readLock().lock();
        for (int i = 5; i > 0 ; i--) {
            ReturnMessage<Integer> returnMessage = new ReturnMessage<>(i ,3);
            WebsocketUtil.sendAllBasicMessage(sessions , returnMessage);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.toString());
            }
        }
        roomPoke.getLock().readLock().unlock();
    }


    /**
     * 判断玩家的是否为牛
     * @param pokes
     * @return
     */
    public Integer isNiuNiu(List<String> pokes){
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
                    int num = Integer.valueOf(pokes.get(i).substring(1,2)) +
                            Integer.valueOf(pokes.get(j).substring(1 ,2)) +
                            Integer.valueOf(pokes.get(k).substring(1 ,2));
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
                    num += Integer.valueOf(pokes.get(i).substring(1 ,2));
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
     * 检查目前是多少局，是否本房间结束
     * @return
     */
    private Boolean checkJuShu(NiuniuRoomParameter niuniuRoomParameter ,Integer runingNum){
        Integer juShu = 0;
        if (Objects.equals(niuniuRoomParameter.getConsumCardNum() ,1)) {
            juShu = 12;
        }else {
            juShu = 24;
        }
        if (Objects.equals(juShu ,runingNum)) {
            return Boolean.TRUE;
        }else {
            return Boolean.FALSE;
        }
    }

    /**
     * 发4张牌
     * @param sessions
     * @param userPokeList
     */
    private void fapai_4(RoomPoke roomPoke  ,Set<Session> sessions ,List<UserPoke> userPokeList){
        List<String> rootPokes = PokeUtil.generatePoke5();
        List<List<Integer>> lists = RandomUtils.getSplitListByMax(userPokeList.size() * 5);
        //设置每个人的牌
        for (int j = 0; j < userPokeList.size(); j++) {
            UserPoke userPoke = userPokeList.get(j);
            List<Integer> list = lists.get(j);
            List<String> pokes = Lists.newArrayList();
            list.forEach(index -> {
                pokes.add(rootPokes.get(index));
            });
            userPoke.setPokes(pokes);
        }
        //给每个人发牌
        userPokeList.forEach(u -> {
            sessions.forEach(session -> {
                SocketUser socketUser = (SocketUser) session.getUserProperties().get(WebKeys.WEBSOCKET_USER_KEY);
                if (Objects.equals(u.getUserId() ,socketUser.getId())) {
                    ReturnMessage<List<String>> returnMessage3 = new ReturnMessage<>(u.getPokes().subList(0,4),4);

                    //加读锁
                    roomPoke.getLock().readLock().lock();
                    WebsocketUtil.sendBasicMessage(session ,returnMessage3);
                    roomPoke.getLock().readLock().unlock();
                }
            });
        });
    }

    /**
     * 选取庄家
     * @param sessions
     * @param userPokeList
     */
    private void selectZhaungJia(RoomPoke roomPoke ,Set<Session> sessions ,List<UserPoke> userPokeList){
        List<Long> qiangZhuangUserIds = Lists.newArrayList();
        userPokeList.forEach(u -> {
            if (u.getIsQiangZhuang()) {
                qiangZhuangUserIds.add(u.getUserId());
            }
        });
        Integer randNum;
        //没人抢庄
        if (qiangZhuangUserIds.isEmpty()) {
            randNum = RandomUtils.getRandNumMax(userPokeList.size());
            userPokeList.forEach(u -> {
                if (Objects.equals(userPokeList.get(randNum).getUserId() ,u.getUserId())) {
                    u.setIsQiangZhuang(Boolean.TRUE);
                }
            });
        }else {
            randNum = RandomUtils.getRandNumMax(qiangZhuangUserIds.size());
            userPokeList.forEach(u -> {
                if (Objects.equals(qiangZhuangUserIds.get(randNum) ,u.getUserId())) {
                    u.setIsQiangZhuang(Boolean.TRUE);
                }
            });
        }
        ReturnMessage<Long> returnMessage1;
        if (qiangZhuangUserIds.isEmpty()) {
            returnMessage1 = new ReturnMessage<>(userPokeList.get(randNum).getUserId() ,5);
        }else {
            returnMessage1 = new ReturnMessage<>(qiangZhuangUserIds.get(randNum) ,5);
        }
        //加读锁
        roomPoke.getLock().readLock().lock();
        WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage1);
        roomPoke.getLock().readLock().unlock();
    }

    /**
     * 发一张牌
     * @param roomPoke
     * @param sessions
     * @param userPokeList
     */
    private void fapai_1(RoomPoke roomPoke ,Set<Session> sessions ,List<UserPoke> userPokeList){
        userPokeList.forEach(u -> {
            sessions.forEach(session -> {
                SocketUser socketUser = (SocketUser) session.getUserProperties().get(WebKeys.WEBSOCKET_USER_KEY);
                if (Objects.equals(u.getUserId() ,socketUser.getId())) {
                    LaskPokeMessage laskPokeMessage = new LaskPokeMessage();
                    laskPokeMessage.setLastPoke(u.getPokes().get(4));
                    laskPokeMessage.setPaiXing(isNiuNiu(u.getPokes()));
                    ReturnMessage<LaskPokeMessage> returnMessage3 = new ReturnMessage<>(laskPokeMessage,6);

                    roomPoke.getLock().readLock().lock();
                    WebsocketUtil.sendBasicMessage(session ,returnMessage3);
                    roomPoke.getLock().readLock().unlock();
                }
            });
        });
    }

    /**
     * 计算/设置本局玩家的分数
     * @param roomPoke
     * @param userPokeList
     */
    private void setScore(RoomPoke roomPoke ,List<UserPoke> userPokeList){
        UserPoke zhuangJia = null;
        for (UserPoke userPoke : userPokeList) {
            if (userPoke.getIsQiangZhuang()) {
                zhuangJia = userPoke;
                break;
            }
        }
        Integer zhuangJiaNiu = isNiuNiu(zhuangJia.getPokes());
        for (UserPoke userPoke : userPokeList) {
            UserPoke xianJia = userPoke;
            if (!xianJia.getIsQiangZhuang()) {
                Integer xianJiaNiu = isNiuNiu(xianJia.getPokes());
                Integer score = zhuangJia.getQiangZhuangMultiple() * xianJia.getXianJiaMultiple();
                //庄家大于闲家
                if (zhuangJiaNiu > xianJiaNiu) {
                    zhuangJia.setThisScore(score);
                    xianJia.setThisScore(-score);
                    //庄家小于闲家
                }else if (zhuangJiaNiu < xianJiaNiu) {
                    zhuangJia.setThisScore(-score);
                    xianJia.setThisScore(score);
                    //牛牛
                }else if (zhuangJiaNiu == 10) {
                    zhuangJia.setThisScore(score);
                    xianJia.setThisScore(-score);
                    //没牛
                }else if (zhuangJiaNiu == 0){
                    Boolean isZhuangJiaBoss = Boolean.TRUE;
                    List<String> zhuangJiaPokes = zhuangJia.getPokes();
                    List<String> xianJiaPokes = xianJia.getPokes();
                    List<Integer> zhuangJiaNums = zhuangJiaPokes.stream().map(str -> Integer.valueOf(str.substring(1 ,2))
                    ).collect(Collectors.toList());
                    List<Integer> xianJiaNums = xianJiaPokes.stream().map(str -> Integer.valueOf(str.substring(1 ,2))
                    ).collect(Collectors.toList());
                    zhuangJiaNums.sort(Comparator.reverseOrder());
                    xianJiaNums.sort(Comparator.reverseOrder());
                    for (int j = 0; j < xianJiaNums.size(); j++) {
                        if (xianJiaNums.get(j) > zhuangJiaNums.get(j)) {
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
        //打完本局后计算各个玩家总分
        List<UserScore> userScores = roomPoke.getUserScores();
        for (UserScore userScore : userScores) {
            userPokeList.forEach(u -> {
                if (Objects.equals(userScore.getUserId() ,u.getUserId())) {
                    userScore.setScore(userScore.getScore() + u.getThisScore());
                }
            });
        }
    }

//    private Integer changePai(){
//
//    }

    /**
     * 保存roomPoke
     * @param roomPoke
     */
    private void saveRoomPoke(RoomPoke roomPoke){
        RoomPokeInit roomPokeInit = new RoomPokeInit();
        roomPokeInit.setRoomRecordId(roomPoke.getRoomRecordId());
        roomPokeInit.setUserPokes(JSON.toJSONString(roomPoke.getUserPokes()));
        roomPokeInit.setUserScores(JSON.toJSONString(roomPoke.getUserScores()));
        roomPokeInit.setRuningNum(roomPoke.getRuningNum());
        roomPokeInitMapper.updateRoomPoke(roomPokeInit);
    }

    /**
     * 給玩家返回結果
     * @param sessions
     * @param roomPoke
     * @param userPokeList
     */
    private void returnResultToUser(Set<Session> sessions ,RoomPoke roomPoke ,List<UserPoke> userPokeList){
        Map<Long, Integer> scoreMap = roomPoke.getUserScores().stream().collect(Collectors.toMap(UserScore::getUserId, UserScore::getScore, (k1, k2) -> k1));
        List<NiuNiuResult> niuNiuResultList = Lists.newArrayList();
        for (UserPoke userPoke : userPokeList) {
            NiuNiuResult niuNiuResult = new NiuNiuResult();
            niuNiuResult.setUserId(userPoke.getUserId());
            niuNiuResult.setIsTanPai(userPoke.getIsTanPai());
            if (!userPoke.getIsTanPai()) {
                niuNiuResult.setPokes(userPoke.getPokes());
                niuNiuResult.setPaiXing(isNiuNiu(userPoke.getPokes()));
            }
            niuNiuResult.setScore(userPoke.getThisScore());
            niuNiuResult.setTotal(scoreMap.get(userPoke.getUserId()));
            niuNiuResultList.add(niuNiuResult);
        }
        ReturnMessage<List<NiuNiuResult>> returnMessage3 = new ReturnMessage<>(niuNiuResultList ,7);
        //加读锁
        roomPoke.getLock().readLock().lock();
        WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage3);
        roomPoke.getLock().readLock().unlock();
    }

    /**
     * 保存结果
     * @param roomId
     * @param roomPoke
     * @param userPokeList
     */
    private void saveResult(Long roomId ,RoomPoke roomPoke ,List<UserPoke> userPokeList){
        GameSituation gameSituation = new GameSituation();
        gameSituation.setRoomRecordId(roomId);
        gameSituation.setGameNum(roomPoke.getRuningNum());
        List<NiuniuSituation> niuniuSituations = Lists.newArrayList();
        for (UserPoke userPoke : userPokeList) {
            NiuniuSituation niuniuSituation = new NiuniuSituation();
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
}
