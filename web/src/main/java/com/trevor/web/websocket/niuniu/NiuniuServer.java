package com.trevor.web.websocket.niuniu;

import com.alibaba.fastjson.JSON;
import com.trevor.bo.JsonEntity;
import com.trevor.bo.SocketSessionUser;
import com.trevor.bo.WebKeys;
import com.trevor.domain.User;
import com.trevor.service.niuniu.NiuniuService;
import com.trevor.util.WebsocketUtil;
import com.trevor.web.websocket.bo.ReturnMessage;
import com.trevor.web.websocket.config.NiuniuServerConfigurator;
import com.trevor.web.websocket.decoder.MessageDecoder;
import com.trevor.web.websocket.encoder.MessageEncoder;
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
@ServerEndpoint(
        value = "/niuniu/{rooId}",
        configurator = NiuniuServerConfigurator.class,
        encoders= {MessageEncoder.class},
        decoders = {MessageDecoder.class}
        )
@Component
@Slf4j
public class NiuniuServer {

    @Resource
    private  NiuniuService niuniuService;

    @Resource(name = "niuniuRooms")
    private Map<Long , Set<Session>> niuniuRomms;

    private Session session;

    /**
     * 连接时调用
     * @param session
     * @param config
     * @param rooId
     */
    @OnOpen
    public void onOpen(Session session , EndpointConfig config , @PathParam("rooId") String rooId) throws IOException {
        this.session = session;
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        User user = (User) httpSession.getAttribute(WebKeys.SESSION_USER_KEY);
        String tempRoomId = rooId.intern();
        String jsonString;
        JsonEntity<SocketSessionUser> jsonEntity;
        synchronized (tempRoomId) {
            jsonEntity = niuniuService.onOpenCheck(rooId, user);
            if (jsonEntity.getCode() > 0) {
                niuniuRomms.get(Long.valueOf(rooId)).add(session);
            }
        }
        if (jsonEntity.getCode() < 0) {
            SocketSessionUser socketSessionUser = jsonEntity.getData();
            ReturnMessage returnMessage = new ReturnMessage();
            //WebsocketUtil.sendBasicMessage(session , jsonString);
            session.close();
        }else {
            session.getUserProperties().put(WebKeys.SESSION_USER_KEY ,jsonEntity.getData());
            //WebsocketUtil.sendAllBasicMessage(niuniuRomms.get(Long.valueOf(rooId)) ,jsonString);
        }
    }

    @OnMessage
    public void receiveMsg(@PathParam("rooId") String rooId, String msg) throws Exception {
                                           SocketSessionUser socketSessionUser = (SocketSessionUser) session.getUserProperties().get(WebKeys.SESSION_USER_KEY);
    }

    @OnClose
    public void disConnect(@PathParam("rooId") String roomName, Session session) {
        if (niuniuRomms.containsKey(Long.valueOf(roomName))) {
            niuniuRomms.get(roomName).remove(session);
            log.info("断开连接");
        }
    }

    @OnError
    public void onError(Throwable t){
        log.error(t.getMessage());
    }

}
