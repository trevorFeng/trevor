package com.trevor.util;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Set;
import com.trevor.web.websocket.bo.ReturnMessage;

/**
 * @author trevpr
 * @date 2019/3/13 10:58
 */
public class WebsocketUtil {

    public static void sendBasicMessage(Session session ,ReturnMessage returnMessage) throws IOException, EncodeException {
        session.getBasicRemote().sendObject(returnMessage);
    }


    public static void sendAllBasicMessage(Set<Session> sessions, ReturnMessage returnMessage) throws IOException, EncodeException {
        for (Session session : sessions) {
            session.getBasicRemote().sendObject(returnMessage);
        }
    }

}
