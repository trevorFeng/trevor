package com.trevor.web.websocket.niuniu;

import com.trevor.bo.SimpleUser;
import com.trevor.common.WebKeys;
import com.trevor.service.niuniu.NiuniuService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一句话描述该类作用:【牛牛服务端】
 *
 * @author: trevor
 * @create: 2019-03-05 22:29
 **/
@ServerEndpoint("/niuniu/{rooId}")
@Component
public class NiuniuServer {

    /**
     * 使用map来收集session，key为roomName，value为同一个房间的用户集合
     */
    private static final Map<String, Set<SimpleUser>> rooms = new ConcurrentHashMap();

    private static NiuniuService niuniuService;

    public static void setNiuniuService(NiuniuService niuniuService){
        NiuniuServer.niuniuService = niuniuService;
    }

    private Session session;

    @OnOpen
    public void onOpen(Session session ,EndpointConfig config ,@PathParam("rooId") String rooId) {
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        SimpleUser simpleUser = (SimpleUser) httpSession.getAttribute(WebKeys.SESSION_USER_KEY);
        if (rooms.containsKey(rooId)) {
            rooms.get(rooId).add(simpleUser);
        }else {

        }
        this.session = session;
    }

    @OnMessage
    public void receiveMsg(@PathParam("roomName") String roomName,
                           String msg, Session session) throws Exception {
        // 此处应该有html过滤
        //session.getUserProperties()
        msg = session.getId() + ":" + msg;
        System.out.println(msg);
        // 接收到信息后进行广播
    }

    @OnClose
    public void disConnect(String roomName, Session session) {
        rooms.get(roomName).remove(session);
        System.out.println("a client has disconnected!");
    }

}
