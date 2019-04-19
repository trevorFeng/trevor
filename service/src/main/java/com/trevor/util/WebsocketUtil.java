//package com.trevor.util;
//
//import javax.websocket.Session;
//import java.io.IOException;
//import java.util.Set;
//
///**
// * @author trevpr
// * @date 2019/3/13 10:58
// */
//public class WebsocketUtil {
//
//    public static void sendBasicMessage(Session session ,ReturnMess returnMessage) throws IOException {
//        session.getBasicRemote().sendText(message);
//    }
//
//
//    public static void sendAllBasicMessage(Set<Session> sessions, String message) throws IOException {
//        for (Session session : sessions) {
//            session.getBasicRemote().sendText(message);
//        }
//    }
//
//}
