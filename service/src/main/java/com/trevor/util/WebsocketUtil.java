package com.trevor.util;

import com.trevor.websocket.bo.ReturnMessage;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author trevpr
 * @date 2019/3/13 10:58
 */
public class WebsocketUtil {

    public static void sendBasicMessage(Session session ,ReturnMessage returnMessage) throws IOException, EncodeException {
        session.getBasicRemote().sendObject(returnMessage);
    }


    public static void sendAllBasicMessage(CopyOnWriteArrayList<Session> sessions, ReturnMessage returnMessage) throws IOException, EncodeException {
        for (Session session : sessions) {
            session.getBasicRemote().sendObject(returnMessage);
        }
    }

}
