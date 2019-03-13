package com.trevor.web.websocket.niuniu;

import com.alibaba.fastjson.JSON;
import com.trevor.bo.JsonEntity;
import com.trevor.bo.SimpleUser;
import com.trevor.bo.UserInfo;
import com.trevor.bo.WebKeys;
import com.trevor.service.niuniu.NiuniuService;
import com.trevor.util.WebsocketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * 一句话描述该类作用:【牛牛服务端,每次建立链接就新建了一个对象】
 *
 * @author: trevor
 * @create: 2019-03-05 22:29
 **/
@ServerEndpoint("/niuniu/{rooId}")
@Component
@Slf4j
public class NiuniuServer {

    @Resource
    private  NiuniuService niuniuService;

    @Resource(name = "niuniuRooms")
    private Map<Long ,Set<Session>> niuniuRomms;

    private Session session;

    /**
     * 连接时调用
     * @param session
     * @param config
     * @param rooId
     */
    @OnOpen
    public void onOpen(Session session ,EndpointConfig config ,@PathParam("rooId") String rooId) throws IOException {
        this.session = session;
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        UserInfo userInfo = (UserInfo) httpSession.getAttribute(WebKeys.SESSION_USER_KEY);
        String tempRoomId = rooId.intern();
        String jsonString;
        JsonEntity<SimpleUser> jsonEntity;
        synchronized (tempRoomId) {
            jsonEntity = niuniuService.onOpenCheck(rooId, userInfo);
            if (jsonEntity.getCode() > 0) {
                niuniuRomms.get(Long.valueOf(rooId)).add(session);
            }
        }
        jsonString = JSON.toJSONString(jsonEntity);
        if (jsonEntity.getCode() < 0) {
            WebsocketUtil.sendBasicMessage(session , jsonString);
            session.close();
        }else {
            session.getUserProperties().put(WebKeys.SESSION_USER_KEY ,jsonEntity.getData());
            WebsocketUtil.sendAllBasicMessage(niuniuRomms.get(Long.valueOf(rooId)) ,jsonString);
        }
    }

    @OnMessage
    public void receiveMsg(@PathParam("rooId") String rooId, String msg) throws Exception {
        SimpleUser simpleUser = (SimpleUser) session.getUserProperties().get(WebKeys.SESSION_USER_KEY);
    }

    @OnClose
    public void disConnect(@PathParam("rooId") String roomName, Session session) {
        if (niuniuRomms.containsKey(Long.valueOf(roomName))) {
            niuniuRomms.get(roomName).remove(session);
            log.info("断开连接");
        }
        System.out.println("a client has disconnected!");
    }

    @OnError
    public void onError(Throwable t){
        log.error(t.getMessage());
    }

}
