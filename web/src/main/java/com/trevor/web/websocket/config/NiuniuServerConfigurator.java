package com.trevor.web.websocket.config;

import lombok.Data;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Auther: trevor
 * @Date: 2019\3\29 0029 23:19
 * @Description:
 */
@Data
public class NiuniuServerConfigurator extends ServerEndpointConfig.Configurator {

    /**
     * 拦截打开握手阶段的HTTP请求和响应
     * @param sec
     * @param request
     * @param response
     */
    @Override
    public void modifyHandshake(ServerEndpointConfig sec , HandshakeRequest request , HandshakeResponse response){
        super.modifyHandshake(sec, request, response);
        HttpSession httpSession = (HttpSession) request.getHttpSession();
        sec.getUserProperties().put(HttpSession.class.getName(), httpSession);
    }
}
