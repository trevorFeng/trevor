package com.trevor.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.trevor.bo.JsonEntity;
import com.trevor.bo.ResponseHelper;
import com.trevor.bo.RoomPoke;
import com.trevor.common.MessageCodeEnum;
import com.trevor.util.WebsocketUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Set;

/**
 * @author trevor
 * @date 2019/3/13 19:20
 */
@Service
public class CountdownTask {

    @Resource
    private QiangZhuangTask qiangZhuangTask;

    /**
     * 开启5秒倒计时
     * @param sessions
     */
    public void coundDown(Set<Session> sessions , RoomPoke roomPoke) throws IOException, InterruptedException {
        for (int i = 5; i > 0 ; i--) {
            Thread.sleep(1000);
            JsonEntity<Integer> jsonEntity = ResponseHelper.createInstance(i , MessageCodeEnum.COUNT_DOWN);
            WebsocketUtil.sendAllBasicMessage(sessions , JSON.toJSONString(jsonEntity));
        }
        roomPoke.setIsReadyOver(true);
        //倒计时结束，通知开始抢庄

    }
}
