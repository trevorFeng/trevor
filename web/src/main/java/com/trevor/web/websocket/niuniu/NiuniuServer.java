package com.trevor.web.websocket.niuniu;

import com.trevor.bo.SimpleUser;
import com.trevor.bo.UserInfo;
import com.trevor.common.WebKeys;
import com.trevor.service.niuniu.NiuniuService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一句话描述该类作用:【牛牛服务端,每次建立链接就新建了一个对象】
 *
 * @author: trevor
 * @create: 2019-03-05 22:29
 **/
@ServerEndpoint("/niuniu/{rooId}")
@Component
public class NiuniuServer {

    @Resource
    private  NiuniuService niuniuService;

    @Resource(name = "niuniuRooms")
    private Map<Long ,Set<SimpleUser>> niuniuRomms;

    private Session session;

    /**
     * 用户头像
     */
    private String userPicture;

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
        niuniuService.onOpenCheck(session ,rooId ,userInfo ,userPicture);
    }

    @OnMessage
    public void receiveMsg(@PathParam("roomName") String roomName,
                           Integer msg, Session session) throws Exception {
        // 此处应该有html过滤
        //session.getUserProperties()

        System.out.println(msg);
        // 接收到信息后进行广播
    }

    @OnClose
    public void disConnect(@PathParam("roomName") String roomName, Session session) {
        rooms.get(roomName).remove(session);
        System.out.println("a client has disconnected!");
    }

}
