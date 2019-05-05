package com.trevor.websocket.niuniu.process;

import com.google.common.collect.Lists;
import com.trevor.bo.RoomPoke;
import com.trevor.bo.UserPoke;
import com.trevor.util.PokeUtil;
import com.trevor.util.RandomUtils;
import com.trevor.util.WebsocketUtil;
import com.trevor.websocket.bo.ReturnMessage;

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

        //闲家下注倒计时
        countDown(sessions ,roomPokeMap.get(rommId));


        //再发一张牌


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

    private void jugePaiXing(List<String> pokes){
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

        }else {
            int num = 0;
            for (int i = 0; i < pokes.size(); i++) {
                if (i != ii && i != jj && i != kk) {
                    num += Integer.valueOf(pokes.get(i).indexOf(1));
                }
            }
            if (num == 10 || num == 20) {

            }
        }

    }


}
