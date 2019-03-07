package com.trevor.web.websocket.niuniu;

import com.trevor.bo.SimpleUser;
import com.trevor.common.WebKeys;
import com.trevor.service.niuniu.NiuniuService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
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

    /**
     * 收到客户端消息后调用的方法
     *
     * @param action 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String action, Session session) {


    }

}
