package com.trevor.websocket.niuniu.process;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.trevor.bo.RoomPoke;
import com.trevor.bo.UserPoke;
import com.trevor.util.PokeUtil;
import com.trevor.util.RandomUtils;
import com.trevor.util.WebsocketUtil;
import com.trevor.websocket.bo.ReturnMessage;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.annotation.Resource;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Auther: trevor
 * @Date: 2019\4\22 0022 23:07
 * @Description:
 */
public abstract class NiuNiuAbstractPlay {

    @Resource(name = "niuniuRooms")
    private Map<Long ,CopyOnWriteArrayList<Session>> niuniuRooms;

    @Resource(name = "niuniuRoomPoke")
    private Map<Long , RoomPoke> roomPokeMap;



    public final void playPoke(Long rommId) throws InterruptedException, EncodeException, IOException {
        CopyOnWriteArrayList<Session> sessions = niuniuRooms.get(rommId);
        //准备的倒计时
        countDown(sessions ,roomPokeMap.get(rommId));
        //倒计时结束通知开始抢庄
        ReturnMessage<String> returnMessage = new ReturnMessage<>("准备抢庄" ,3);
        for (Session session : sessions) {
            if (session.getUserProperties().get("ready") != null) {
                WebsocketUtil.sendBasicMessage(session ,returnMessage);
            }
        }
        //开始抢庄倒计时
        countDown(sessions ,roomPokeMap.get(rommId));
        //选取庄家
        RoomPoke roomPoke = roomPokeMap.get(rommId);
        Map<Long, UserPoke> userPokeMap = roomPoke.getUserPokes().get(roomPoke.getRuningNum() - 1);
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
        //先发四张牌
        List<String> pokes = PokeUtil.generatePoke5();
        List<List<Integer>> lists = RandomUtils.getSplitListByMax(userPokeMap.size() * 5);
        int i = 0;
        Map<Long ,List<String>> userPokesMap = Maps.newHashMap();
        for (Map.Entry<Long, UserPoke> entry : userPokeMap.entrySet()) {
            List<Integer> list = lists.get(i);
            List<String> userPokes = Lists.newArrayList();
            list.forEach(index -> {
                userPokes.add(pokes.get(index));
            });
            entry.getValue().setPokes(userPokes);
            userPokesMap.put(entry.getKey() ,userPokes);
            i++;
        }
        ReturnMessage<Map<Long ,List<String>>> returnMessage2 = new ReturnMessage<>(userPokesMap ,6);
        WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage2);
        //闲家下注倒计时
        countDown(sessions ,roomPokeMap.get(rommId));
        //再发一张牌
        for (Map.Entry<Long, UserPoke> entry : userPokeMap.entrySet()) {

        }

        //计算分数得失

    }


    /**
     * 倒计时
     */
    protected void countDown(CopyOnWriteArrayList<Session> sessions , RoomPoke roomPoke) throws InterruptedException, IOException, EncodeException {
        for (int i = 5; i > 0 ; i--) {
            ReturnMessage<Integer> returnMessage = new ReturnMessage<>(i ,3);
            WebsocketUtil.sendAllBasicMessage(sessions , returnMessage);
            Thread.sleep(1000);
        }
        roomPoke.setIsReadyOver(true);
    }

    /**
     * 准备结束发消息给客户端
     */
    protected void readyOver(CopyOnWriteArrayList<Session> sessions , RoomPoke roomPoke) throws InterruptedException, IOException, EncodeException {
        for (int i = 5; i > 0 ; i--) {
            ReturnMessage<Integer> returnMessage = new ReturnMessage<>(i ,3);
            WebsocketUtil.sendAllBasicMessage(sessions , returnMessage);
            Thread.sleep(1000);
        }
        roomPoke.setIsReadyOver(true);
    }



    /**
     * 发牌
     */
    protected void fapai(){

    }


}
