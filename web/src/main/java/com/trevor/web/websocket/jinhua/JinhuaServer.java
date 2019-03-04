package com.trevor.web.websocket.jinhua;

import com.trevor.service.bo.UserInfo;
import org.springframework.stereotype.Component;

import javax.websocket.server.ServerEndpoint;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trevor
 * @date 2019/3/1 11:44
 */
@ServerEndpoint("/jinhua/{roomName}/{action}")
@Component
public class JinhuaServer {

    private final Integer MAX_PERSON = 10;

    private ConcurrentHashMap<String , Set<UserInfo>> jinHuaRooms = new ConcurrentHashMap<>(2<<10);



}
