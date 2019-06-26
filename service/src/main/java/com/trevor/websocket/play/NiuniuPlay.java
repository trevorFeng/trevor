package com.trevor.websocket.play;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.trevor.bo.*;
import com.trevor.dao.GameSituationMapper;
import com.trevor.dao.RoomPokeInitMapper;
import com.trevor.domain.GameSituation;
import com.trevor.domain.Room;
import com.trevor.domain.RoomPokeInit;
import com.trevor.enums.GameStatusEnum;
import com.trevor.service.RoomService;
import com.trevor.service.createRoom.bo.NiuniuRoomParameter;
import com.trevor.util.PokeUtil;
import com.trevor.util.RandomUtils;
import com.trevor.util.WebsocketUtil;
import com.trevor.websocket.bo.NiuNiuPaiXingEnum;
import com.trevor.websocket.bo.NiuNiuResult;
import com.trevor.websocket.bo.ReturnMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NiuniuPlay {

    @Resource
    private RoomService roomService;

    @Resource
    private GameSituationMapper gameSituationMapper;

    @Resource(name = "sessionsMap")
    private Map<Long ,Set<Session>> sessionsMap;
2

    @Resource(name = "roomPokeMap")
    private Map<Long , RoomPoke> roomPokeMap;

    @Resource
    private RoomPokeInitMapper roomPokeInitMapper;

    @Transactional(rollbackFor = Exception.class)
    public void play(Long roomId){
        Room room = roomService.findOneById(roomId);
        NiuniuRoomParameter niuniuRoomParameter = JSON.parseObject(room.getRoomConfig() ,NiuniuRoomParameter.class);
        RoomPoke roomPoke = roomPokeMap.get(roomId);
        Set<Session> sessions = sessionsMap.get(roomId);
        //检查目前是多少局，是否本房间结束
        if (checkJuShu(niuniuRoomParameter ,roomPoke.getRuningNum())) {
            return;
        }
        //准备的倒计时
        countDown(sessions ,roomPokeMap.get(roomId) ,3 ,GameStatusEnum.BEFORE_FAPAI_4.getCode());
        try {
            Thread.sleep(500);
        }catch (Exception e) {
            log.error(e.toString());
        }

        //先发四张牌
        List<UserPoke> userPokeList = roomPoke.getUserPokes().stream().filter(u -> Objects.equals(u.getIndex(), roomPoke.getRuningNum()))
                .collect(Collectors.toList()).get(0).getUserPokeList();
        fapai_4(roomPoke ,sessions ,userPokeList ,niuniuRoomParameter);

        //开始抢庄倒计时
        countDown(sessions ,roomPokeMap.get(roomId) ,11 ,GameStatusEnum.BEFORE_SELECT_ZHUANGJIA.getCode());
        //选取庄家
        selectZhaungJia(roomPoke ,sessions ,userPokeList);
        try {
            Thread.sleep(2000);
        }catch (Exception e) {
            log.error(e.toString());
        }
        //闲家下注倒计时
        countDown(sessions ,roomPokeMap.get(roomId) ,12 ,GameStatusEnum.BEFORE_LAST_POKE.getCode());
        //再发一张牌
        fapai_1(roomPoke ,sessions ,userPokeList ,niuniuRoomParameter);
        //准备摊牌倒计时
        countDown(sessions ,roomPokeMap.get(roomId) ,13 ,GameStatusEnum.BEFORE_CALRESULT.getCode());

        //给玩家发返回计算的结果
        returnResultToUser(sessions ,roomPoke ,userPokeList ,niuniuRoomParameter);
        //计算/设置本局玩家的分数
        setScore(roomPoke ,userPokeList ,niuniuRoomParameter);
        //保存roomPoke
        saveRoomPoke(roomPoke);
        //改变房间状态
        roomPoke.setReadyNum(0);
        roomPoke.setGameStatus(GameStatusEnum.BEFORE_READY.getCode());
        roomPoke.setRuningNum(roomPoke.getRuningNum() + 1);
        //保存结果
        saveResult(roomId ,roomPoke ,userPokeList);
        //如果对局数结束，给所有人发消息，对局结束，如果没有结束则发个消息，继续开始
        Lock leaderReadLock = roomPoke.getLeaderReadLock();
        leaderReadLock.lock();
        roomPoke.setProcessFlag(0);
        if (Objects.equals(roomPoke.getRuningNum() ,roomPoke.getTotalNum())) {
            ReturnMessage<Integer> returnMessage = new ReturnMessage<>(roomPoke.getRuningNum(),14);
            WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage);
            leaderReadLock.unlock();
        }else {
            ReturnMessage<Integer> returnMessage = new ReturnMessage<>(roomPoke.getRuningNum(),13);
            WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage);
            leaderReadLock.unlock();
        }

    }

    /**
     * 倒计时
     */
    protected void countDown(Set<Session> sessions , RoomPoke roomPoke ,Integer messageCode ,Integer gameStatus) {
        Lock leaderReadLock = roomPoke.getLeaderReadLock();
        leaderReadLock.lock();

        roomPoke.getGameStatusLock().writeLock().lock();
        roomPoke.setGameStatus(gameStatus);
        roomPoke.getGameStatusLock().writeLock().unlock();

        for (int i = 5; i > 0 ; i--) {
            ReturnMessage<Integer> returnMessage = new ReturnMessage<>(i ,messageCode);
            WebsocketUtil.sendAllBasicMessage(sessions , returnMessage);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error(e.toString());
            }
        }

        leaderReadLock.unlock();
    }


    /**
     * 判断玩家的是否为牛
     *
     *      * 1---顺子牛，5倍
     *      * 2---五花牛，6倍
     *      * 3---同花牛，6倍
     *      * 4---葫芦牛，7倍
     *      * 5---炸弹牛，8倍
     *      * 6---五小牛，10倍
     *
     *      * 规则
     *      * 1---牛牛x3，牛九x2，牛八x2
     *      * 2---牛牛x4，牛九x3，牛八x2，牛7x2
     *
     * @param pokes
     * @return
     */
    public PaiXing isNiuNiu(List<String> pokes , Set<Integer> paiXingSet ,Integer rule){
        PaiXing paiXing;
        if (paiXingSet == null) {
            paiXingSet = new HashSet<>();
        }
        //是否是五小牛
        paiXing = isNiu_16(pokes ,paiXingSet);
        if (paiXing != null) {
            return paiXing;
        }
        //是否是炸弹牛
        paiXing = isNiu_15(pokes ,paiXingSet);
        if (paiXing != null) {
            return paiXing;
        }
        //是否是葫芦牛
        paiXing = isNiu_14(pokes ,paiXingSet);
        if (paiXing != null) {
            return paiXing;
        }
        //是否是同花牛
        paiXing = isNiu_13(pokes ,paiXingSet);
        if (paiXing != null) {
            return paiXing;
        }
        //是否是五花牛
        paiXing = isNiu_12(pokes ,paiXingSet);
        if (paiXing != null) {
            return paiXing;
        }
        //是否是顺子牛
        paiXing = isNiu_11(pokes ,paiXingSet);
        if (paiXing != null) {
            return paiXing;
        }
        int ii = 0;
        int jj = 0;
        int kk = 0;
        boolean isNiu = Boolean.FALSE;
        for (int i = 0; i < pokes.size(); i++) {
            if (i >= 3) {
                break;
            }
            for (int j = i+1; j < pokes.size(); j++) {
                for (int k = j+1; k < pokes.size(); k++) {
                    int num = changePai_10(pokes.get(i).substring(1,2)) +
                            changePai_10(pokes.get(j).substring(1 ,2)) +
                            changePai_10(pokes.get(k).substring(1 ,2));
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
        //没牛
        if (!isNiu) {
            paiXing = new PaiXing();
            paiXing.setMultiple(1);
            paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_0.getPaiXingCode());
            return paiXing;
        }else {
            paiXing = new PaiXing();
            int num = 0;
            for (int i = 0; i < pokes.size(); i++) {
                if (i != ii && i != jj && i != kk) {
                    num += changePai_10(pokes.get(i).substring(1 ,2));
                }
            }
            // 1 - 牛牛x3，牛九x2，牛八x2
            if (Objects.equals(rule ,1)) {
                if (num == 10 || num == 20) {
                    paiXing.setMultiple(3);
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_10.getPaiXingCode());
                    return paiXing;
                }else if (num == 9 || num == 19) {
                    paiXing.setMultiple(2);
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_9.getPaiXingCode());
                    return paiXing;
                }else if (num == 8 || num == 18) {
                    paiXing.setMultiple(2);
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_8.getPaiXingCode());
                    return paiXing;
                }else {
                    paiXing.setMultiple(1);
                    paiXing.setPaixing(num);
                    return paiXing;
                }
            //2---牛牛x4，牛九x3，牛八x2，牛7x2
            }else {
                if (num == 10 || num == 20) {
                    paiXing.setMultiple(4);
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_10.getPaiXingCode());
                    return paiXing;
                }else if (num == 9 || num == 19) {
                    paiXing.setMultiple(3);
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_9.getPaiXingCode());
                    return paiXing;
                }else if (num == 8 || num == 18) {
                    paiXing.setMultiple(2);
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_8.getPaiXingCode());
                    return paiXing;
                } else if (num == 7 || num == 17) {
                    paiXing.setMultiple(2);
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_7.getPaiXingCode());
                    return paiXing;
                }else {
                    paiXing.setMultiple(1);
                    paiXing.setPaixing(num);
                    return paiXing;
                }
            }

        }
    }

    /**
     * 是否是五小牛 10 倍
     * @param pokes
     * @param paiXingSet
     * @return
     */
    private PaiXing isNiu_16(List<String> pokes , Set<Integer> paiXingSet){
        PaiXing paiXing;
        if (paiXingSet.contains(6)) {
            int num = 0;
            boolean glt_5 = true;
            for (String str : pokes) {
                String pai = str.substring(1 ,2);
                num += changePai(pai);
                if (changePai(pai) < 5) {
                    glt_5 = false;
                    break;
                }
            }
            if (num <= 10 && glt_5) {
                paiXing = new PaiXing();
                paiXing.setMultiple(10);
                paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_16.getPaiXingCode());
                return paiXing;
            }
        }
        return null;
    }

    /**
     * 是否是炸弹牛 8倍
     * @param pokes
     * @param paiXingSet
     * @return
     */
    private PaiXing isNiu_15(List<String> pokes , Set<Integer> paiXingSet){
        PaiXing paiXing;
        if (paiXingSet.contains(5)) {
            int num = 0;
            String pai = pokes.get(0).substring(1,2);
            for (String str : pokes) {
                if (Objects.equals(pai ,str.substring(1 ,2))) {
                    num ++;
                }
            }
            if (num == 0 || num == 4 || num == 1 || num == 5) {
                paiXing = new PaiXing();
                paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_15.getPaiXingCode());
                paiXing.setMultiple(8);
                return paiXing;
            }
        }
        return null;
    }

    /**
     * 是否是葫芦牛 7倍
     * @param pokes
     * @param paiXingSet
     * @return
     */
    private PaiXing isNiu_14(List<String> pokes , Set<Integer> paiXingSet){
        PaiXing paiXing;
        if (paiXingSet.contains(4)) {
            Set<String> set = new HashSet<>();
            for (String str : pokes) {
                set.add(str.substring(1 ,2));
            }
            if (set.size() <=2) {
                int num = 0;
                String pai = pokes.get(0).substring(1,2);
                for (String str : pokes) {
                    if (Objects.equals(pai ,str.substring(1 ,2))) {
                        num ++;
                    }
                }
                if (num == 2 || num == 3) {
                    paiXing = new PaiXing();
                    paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_14.getPaiXingCode());
                    paiXing.setMultiple(7);
                    return paiXing;
                }
            }
        }
        return null;
    }

    /**
     * 是否是同花牛 6倍
     * @param pokes
     * @param paiXingSet
     * @return
     */
    private PaiXing isNiu_13(List<String> pokes , Set<Integer> paiXingSet){
        PaiXing paiXing;
        if (paiXingSet.contains(3)) {
            Set<String> set = new HashSet<>();
            for (String str : pokes) {
                set.add(str.substring(0,1));
            }
            if (set.size() == 1) {
                paiXing = new PaiXing();
                paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_13.getPaiXingCode());
                paiXing.setMultiple(6);
                return paiXing;
            }
        }
        return null;
    }

    /**
     * 是否是五花牛 6倍
     * @param pokes
     * @param paiXingSet
     * @return
     */
    private PaiXing isNiu_12(List<String> pokes , Set<Integer> paiXingSet){
        PaiXing paiXing;
        if (paiXingSet.contains(2)) {
            boolean j_q_k = true;
            List<String> pais = new ArrayList<>();
            pais.add("b");
            pais.add("c");
            pais.add("d");
            for (String str : pokes) {
                if (!pais.contains(str.substring(1 ,2))) {
                    j_q_k = false;
                    break;
                }
            }
            if (j_q_k) {
                paiXing = new PaiXing();
                paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_12.getPaiXingCode());
                paiXing.setMultiple(6);
                return paiXing;
            }
        }
        return null;
    }

    /**
     * 是否是顺子牛 ，5倍
     * @param pokes
     * @param paiXingSet
     * @return
     */
    private PaiXing isNiu_11(List<String> pokes , Set<Integer> paiXingSet){
        PaiXing paiXing;
        if (paiXingSet.contains(1)) {
            List<Integer> paiList = Lists.newArrayList();
            Set<Integer> paiSet = new HashSet<>();
            for (String str : pokes) {
                paiList.add(changePai(str.substring(1 ,2)));
                paiSet.add(changePai(str.substring(1 ,2)));
            }
            paiList.sort(Comparator.reverseOrder());
            if (paiList.get(0) - paiList.get(4) == 4 && paiSet.size() == 5) {
                paiXing = new PaiXing();
                paiXing.setPaixing(NiuNiuPaiXingEnum.NIU_11.getPaiXingCode());
                paiXing.setMultiple(5);
                return paiXing;
            }
        }
        return null;
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
    private void fapai_4(RoomPoke roomPoke  ,Set<Session> sessions ,List<UserPoke> userPokeList ,NiuniuRoomParameter niuniuRoomParameter){
        List<String> rootPokes = PokeUtil.generatePoke5();
        //生成牌在rootPokes的索引
        List<List<Integer>> lists;
        //生成牌
        List<List<String>> pokesList = Lists.newArrayList();
        //判断每个集合是否有两个五小牛，有的话重新生成
        Boolean twoWuXiaoNiu = true;
        while (twoWuXiaoNiu) {
            lists = RandomUtils.getSplitListByMax(rootPokes.size() ,userPokeList.size() * 5);
            //生成牌
            pokesList = Lists.newArrayList();
            for (List<Integer> integers : lists) {
                List<String> stringList = Lists.newArrayList();
                integers.forEach(index -> {
                    stringList.add(rootPokes.get(index));
                });
                pokesList.add(stringList);
            }
            int niu_16_nums = 0;
            for (List<String> pokes : pokesList) {
                PaiXing niu_16 = isNiu_16(pokes, niuniuRoomParameter.getPaiXing());
                if (niu_16 != null) {
                    niu_16_nums ++;
                }
            }
            if (niu_16_nums < 2) {
                twoWuXiaoNiu = false;
            }
        }
        //设置每个人的牌
        for (int j = 0; j < userPokeList.size(); j++) {
            UserPoke userPoke = userPokeList.get(j);
            userPoke.setPokes(pokesList.get(j));
        }
        //给每个人发牌
        /**
         * 加读锁
         */
        Lock leaderReadLock = roomPoke.getLeaderReadLock();
        leaderReadLock.lock();

        roomPoke.getGameStatusLock().writeLock().lock();
        roomPoke.setGameStatus(GameStatusEnum.BEFORE_QIANGZHUANG_COUNTDOWN.getCode());
        roomPoke.getGameStatusLock().writeLock().unlock();

        //设置realWanJias
        roomPoke.getRealWanJiaLock().lock();
        List<RealWanJiaInfo> realWanJias = roomPoke.getRealWanJias();
        userPokeList.forEach(u -> {
            for (RealWanJiaInfo realWanJiaInfo : realWanJias) {
                if (Objects.equals(u.getUserId() ,realWanJiaInfo.getId())) {
                    realWanJiaInfo.setPokes(u.getPokes().subList(0,4));
                    break;
                }
            }
        });
        roomPoke.getRealWanJiaLock().unlock();

        Map<Long ,List<String>> pokeMap = Maps.newHashMap();
        for (UserPoke userPoke : userPokeList) {
            pokeMap.put(userPoke.getUserId() ,userPoke.getPokes());
        }
        ReturnMessage< Map<Long ,List<String>>> returnMessage3 = new ReturnMessage<>(pokeMap,4);
        WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage3);
        /**
         * 加读锁结束
         */
        leaderReadLock.unlock();
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
        Long zhuangJiaUserId;
        if (qiangZhuangUserIds.isEmpty()) {
            zhuangJiaUserId =userPokeList.get(randNum).getUserId();
            returnMessage1 = new ReturnMessage<>(zhuangJiaUserId ,5);
        }else {
            zhuangJiaUserId = qiangZhuangUserIds.get(randNum);
            returnMessage1 = new ReturnMessage<>(zhuangJiaUserId ,5);
        }

        Lock leaderReadLock = roomPoke.getLeaderReadLock();
        leaderReadLock.lock();

        Lock gameStatusWriteLock = roomPoke.getGameStatusWriteLock();
        gameStatusWriteLock.lock();
        roomPoke.setGameStatus(GameStatusEnum.BEFORE_XIANJIA_XIAZHU.getCode());
        gameStatusWriteLock.unlock();

        roomPoke.getRealWanJias().stream().filter(r -> Objects.equals(r.getId() ,zhuangJiaUserId)).findFirst().get().setIsZhuangJia(Boolean.TRUE);

        WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage1);
        leaderReadLock.unlock();
    }

    /**
     * 发一张牌
     * @param roomPoke
     * @param sessions
     * @param userPokeList
     */
    private void fapai_1(RoomPoke roomPoke ,Set<Session> sessions ,List<UserPoke> userPokeList ,NiuniuRoomParameter niuniuRoomParameter){
        Lock leaderReadLock = roomPoke.getLeaderReadLock();
        leaderReadLock.lock();

        Lock gameStatusWriteLock = roomPoke.getGameStatusWriteLock();
        gameStatusWriteLock.lock();
        roomPoke.setGameStatus(GameStatusEnum.BEFORE_TABPAI_COUNTDOWN.getCode());
        gameStatusWriteLock.unlock();

        List<RealWanJiaInfo> realWanJiaInfos = roomPoke.getRealWanJias();
        for (UserPoke userPoke : userPokeList) {
            for (RealWanJiaInfo realWanJiaInfo : realWanJiaInfos) {
                if (Objects.equals(userPoke.getUserId() ,realWanJiaInfo.getId())) {
                    realWanJiaInfo.getPokes().add(userPoke.getPokes().get(4));
                    break;
                }
            }
        }

        userPokeList.forEach(u -> {
            sessions.forEach(session -> {
                Long userId = (Long) session.getUserProperties().get(WebKeys.WEBSOCKET_USER_ID);
                if (Objects.equals(u.getUserId() ,userId)) {
                    LaskPokeMessage laskPokeMessage = new LaskPokeMessage();
                    laskPokeMessage.setLastPoke(u.getPokes().get(4));
                    laskPokeMessage.setPaiXing(isNiuNiu(u.getPokes() ,niuniuRoomParameter.getPaiXing() ,niuniuRoomParameter.getRule()).getPaixing());
                    ReturnMessage<LaskPokeMessage> returnMessage3 = new ReturnMessage<>(laskPokeMessage,6);

                    WebsocketUtil.sendBasicMessage(session ,returnMessage3);
                }
            });
        });
        leaderReadLock.unlock();
    }

    /**
     * 计算/设置本局玩家的分数
     * @param roomPoke
     * @param userPokeList
     */
    private void setScore(RoomPoke roomPoke ,List<UserPoke> userPokeList ,NiuniuRoomParameter niuniuRoomParameter){
        UserPoke zhuangJia = null;
        for (UserPoke userPoke : userPokeList) {
            if (userPoke.getIsQiangZhuang()) {
                zhuangJia = userPoke;
                break;
            }
        }
        PaiXing zhuangJiaPaiXing = isNiuNiu(zhuangJia.getPokes() ,niuniuRoomParameter.getPaiXing() ,niuniuRoomParameter.getRule());
        for (UserPoke userPoke : userPokeList) {
            UserPoke xianJia = userPoke;
            if (!xianJia.getIsQiangZhuang()) {
                PaiXing xianJiaPaiXing = isNiuNiu(xianJia.getPokes() ,niuniuRoomParameter.getPaiXing() ,niuniuRoomParameter.getRule());
                Integer score = zhuangJia.getQiangZhuangMultiple() * xianJia.getXianJiaMultiple() * niuniuRoomParameter.getBasePoint();
                //庄家大于闲家
                if (zhuangJiaPaiXing.getPaixing() > xianJiaPaiXing.getPaixing()) {
                    score = score * zhuangJiaPaiXing.getMultiple();
                    zhuangJia.setThisScore(score);
                    xianJia.setThisScore(-score);
                //庄家小于闲家
                }else if (zhuangJiaPaiXing.getPaixing() < xianJiaPaiXing.getPaixing()) {
                    score = score * xianJiaPaiXing.getMultiple();
                    zhuangJia.setThisScore(-score);
                    xianJia.setThisScore(score);
                }else{
                    List<String> zhuangJiaPokes = zhuangJia.getPokes();
                    List<String> xianJiaPokes = xianJia.getPokes();
                    boolean zhuangJiaDa = true;
                    //炸弹牛，比炸弹大小(已经设置不可能出现两个五小牛)
                    if (Objects.equals(zhuangJiaPaiXing.getPaixing() ,NiuNiuPaiXingEnum.NIU_15.getPaiXingCode())){
                        if (!niu_15_daXiao(zhuangJiaPokes, xianJiaPokes)) {
                            zhuangJiaDa = false;
                        }
                        //葫芦牛，比3张牌一样的大小
                    }else if (Objects.equals(zhuangJiaPaiXing.getPaixing() ,NiuNiuPaiXingEnum.NIU_14.getPaiXingCode())) {
                        if (!niu_14_daXiao(zhuangJiaPokes, xianJiaPokes)) {
                            zhuangJiaDa = false;
                        }
                    //同花牛，先比花色大小，再比牌值大小
                    }else if (Objects.equals(zhuangJiaPaiXing.getPaixing() ,NiuNiuPaiXingEnum.NIU_13.getPaiXingCode())) {
                        if (!niu_13_daXiao(zhuangJiaPokes, xianJiaPokes)) {
                            zhuangJiaDa = false;
                        }
                    //五花牛，比最大牌，再比花色 //顺子牛，比最大牌，再比花色//比最大牌，最后比花色
                    }else {
                        //倒叙排，比大小
                        Integer paiZhi = biPaiZhi(zhuangJiaPokes, xianJiaPokes);
                        if (Objects.equals(paiZhi ,1)) {
                            zhuangJiaDa = true;
                        }else if (Objects.equals(-1 ,paiZhi)) {
                            zhuangJiaDa = false;
                        }else {
                            List<Integer> zhuangJiaNums = zhuangJiaPokes.stream().map(str -> changePai(str.substring(1 ,2))
                            ).collect(Collectors.toList());
                            Map<String ,String> zhuangJiaMap = Maps.newHashMap();
                            for (String zhuang : zhuangJiaPokes) {
                                zhuangJiaMap.put(zhuang.substring(1 ,2) ,zhuang.substring(0 ,1));
                            }
                            List<Integer> xianJiaNums = xianJiaPokes.stream().map(str -> changePai(str.substring(1 ,2))
                            ).collect(Collectors.toList());
                            Map<String ,String> xianJiaMap = Maps.newHashMap();
                            for (String xian : xianJiaPokes) {
                                xianJiaMap.put(xian.substring(1 ,2) ,xian.substring(0 ,1));
                            }
                            zhuangJiaNums.sort(Comparator.reverseOrder());
                            xianJiaNums.sort(Comparator.reverseOrder());
                            if (Integer.valueOf(zhuangJiaMap.get(zhuangJiaNums.get(0))) > Integer.valueOf(xianJiaMap.get(xianJiaNums.get(0)))) {
                                zhuangJiaDa = true;
                            }else {
                                zhuangJiaDa = false;
                            }
                        }
                    }
                    if (zhuangJiaDa) {
                        score = score * zhuangJiaPaiXing.getMultiple();
                        zhuangJia.setThisScore(score);
                        xianJia.setThisScore(-score);
                    }else {
                        score = score * xianJiaPaiXing.getMultiple();
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

    /**
     * 比较两个炸弹牛大小
     * @param zhuangJiaPokes
     * @param xianJiaPokes
     * @return zhuangJiaPokes > xianJiaPokes返回true
     */
    private Boolean niu_15_daXiao(List<String> zhuangJiaPokes ,List<String> xianJiaPokes){
        Integer zhuangJiaNum = getDianShuNumberMap(zhuangJiaPokes ,4);
        Integer xianJiaNum = getDianShuNumberMap(xianJiaPokes ,4);

        if (zhuangJiaNum > xianJiaNum) {
            return true;
        }
        return false;
    }

    private Integer getDianShuNumberMap(List<String> pokes ,Integer ciShu){
        Map<String ,Integer> map = Maps.newHashMap();
        for (String poke : pokes) {
            String dianShu = poke.substring(1 ,2);
            if (!map.keySet().contains(dianShu)) {
                map.put(dianShu ,1);
            }else {
                map.put(dianShu ,map.get(dianShu) + 1);
            }
        }
        for (Map.Entry<String ,Integer> entry : map.entrySet()) {
            if (Objects.equals(ciShu ,entry.getValue())) {
                return changePai(entry.getKey());
            }
        }
        throw new RuntimeException("出现炸弹牛或葫芦牛，但是牌不对");
    }

    /**
     * 比较两个葫芦牛大小
     * @param zhuangJiaPokes
     * @param xianJiaPokes
     * @return zhuangJiaPokes > xianJiaPokes返回true
     */
    private Boolean niu_14_daXiao(List<String> zhuangJiaPokes ,List<String> xianJiaPokes){
        Integer zhuangJiaNum = getDianShuNumberMap(zhuangJiaPokes ,3);
        Integer xianJiaNum = getDianShuNumberMap(xianJiaPokes ,3);
        if (zhuangJiaNum > xianJiaNum) {
            return true;
        }
        return false;
    }

    /**
     * 比牌值大小
     * @param zhuangJiaPokes
     * @param xianJiaPokes
     * @return zhuangJiaPokes > xianJiaPokes返回1 ,zhuangJiaPokes < xianJiaPokes返回-1,zhuangJiaPokes == xianJiaPokes返回0
     */
    private Integer biPaiZhi(List<String> zhuangJiaPokes ,List<String> xianJiaPokes){
        List<Integer> zhuangJiaNums = zhuangJiaPokes.stream().map(str -> changePai(str.substring(1 ,2))
        ).collect(Collectors.toList());
        List<Integer> xianJiaNums = xianJiaPokes.stream().map(str -> changePai(str.substring(1 ,2))
        ).collect(Collectors.toList());
        zhuangJiaNums.sort(Comparator.reverseOrder());
        xianJiaNums.sort(Comparator.reverseOrder());
        for (int j = 0; j < xianJiaNums.size(); j++) {
            if (zhuangJiaNums.get(j) > xianJiaNums.get(j)) {
                return 1;
            }else if (Objects.equals(zhuangJiaNums.get(j) ,xianJiaNums.get(j))) {
                continue;
            }else {
                return -1;
            }
        }
        return 0;
    }

    /**
     * 比较两个同花牛大小
     * @param zhuangJiaPokes
     * @param xianJiaPokes
     * @return zhuangJiaPokes > xianJiaPokes返回true
     */
    private Boolean niu_13_daXiao(List<String> zhuangJiaPokes ,List<String> xianJiaPokes){
        if (Integer.valueOf(zhuangJiaPokes.get(0).substring(0,1)) > Integer.valueOf(xianJiaPokes.get(0).substring(0,1))) {
            return true;
        }else if (Objects.equals(Integer.valueOf(zhuangJiaPokes.get(0).substring(0,1)) ,Integer.valueOf(xianJiaPokes.get(0).substring(0,1))) ) {
            Integer paiZhi = biPaiZhi(zhuangJiaPokes ,xianJiaPokes);
            if (Objects.equals(paiZhi ,1)) {
                return true;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    /**
     * 筹10
     * @param pai
     * @return
     */
    private Integer changePai_10(String pai){
        if (Objects.equals("a" ,pai)) {
            return 10;
        }else if (Objects.equals("b" ,pai)) {
            return 10;
        }else if (Objects.equals("c" ,pai)) {
            return 10;
        }else if (Objects.equals("d" ,pai)) {
            return 10;
        }else {
            return Integer.valueOf(pai);
        }
    }

    /**
     * 比大小
     * @param pai
     * @return
     */
    private Integer changePai(String pai){
        if (Objects.equals("a" ,pai)) {
            return 10;
        }else if (Objects.equals("b" ,pai)) {
            return 11;
        }else if (Objects.equals("c" ,pai)) {
            return 12;
        }else if (Objects.equals("d" ,pai)) {
            return 13;
        }else {
            return Integer.valueOf(pai);
        }
    }

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
    private void returnResultToUser(Set<Session> sessions ,RoomPoke roomPoke ,List<UserPoke> userPokeList ,NiuniuRoomParameter niuniuRoomParameter){
        Map<Long, Integer> scoreMap = roomPoke.getUserScores().stream().collect(Collectors.toMap(UserScore::getUserId, UserScore::getScore, (k1, k2) -> k1));
        List<NiuNiuResult> niuNiuResultList = Lists.newArrayList();
        for (UserPoke userPoke : userPokeList) {
            NiuNiuResult niuNiuResult = new NiuNiuResult();
            niuNiuResult.setUserId(userPoke.getUserId());
            niuNiuResult.setIsTanPai(userPoke.getIsTanPai());
            if (!userPoke.getIsTanPai()) {
                niuNiuResult.setPokes(userPoke.getPokes());
                niuNiuResult.setPaiXing(isNiuNiu(userPoke.getPokes() ,niuniuRoomParameter.getPaiXing() ,niuniuRoomParameter.getRule()).getPaixing());
            }
            niuNiuResult.setScore(userPoke.getThisScore());
            niuNiuResult.setTotal(scoreMap.get(userPoke.getUserId()));
            niuNiuResultList.add(niuNiuResult);
        }
        ReturnMessage<List<NiuNiuResult>> returnMessage3 = new ReturnMessage<>(niuNiuResultList ,7);
        //加读锁
        Lock leaderReadLock = roomPoke.getLeaderReadLock();
        leaderReadLock.lock();

        Lock gameStatusWriteLock = roomPoke.getGameStatusWriteLock();
        gameStatusWriteLock.lock();
        roomPoke.setGameStatus(GameStatusEnum.BEFORE_NEXT_START.getCode());
        gameStatusWriteLock.unlock();

        WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage3);
        leaderReadLock.unlock();
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
