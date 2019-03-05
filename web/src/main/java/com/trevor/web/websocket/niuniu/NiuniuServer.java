package com.trevor.web.websocket.niuniu;

import org.springframework.stereotype.Component;

import javax.websocket.server.ServerEndpoint;

/**
 * 一句话描述该类作用:【牛牛服务端】
 *
 * @author: trevor
 * @create: 2019-03-05 22:29
 **/
@ServerEndpoint("/niuniu/{roomName}/{action}")
@Component
public class NiuniuServer {

}
