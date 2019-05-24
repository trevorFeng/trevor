package com.trevor.web.websocket.niuniu;

import com.google.common.collect.Lists;
import com.trevor.BizException;
import com.trevor.bo.*;
import com.trevor.domain.User;
import com.trevor.service.user.UserService;
import com.trevor.util.TokenUtil;
import com.trevor.util.WebsocketUtil;
import com.trevor.web.websocket.config.NiuniuServerConfigurator;
import com.trevor.web.websocket.decoder.MessageDecoder;
import com.trevor.web.websocket.encoder.MessageEncoder;
import com.trevor.websocket.bo.ReceiveMessage;
import com.trevor.websocket.bo.ReturnMessage;
import com.trevor.websocket.bo.SocketUser;
import com.trevor.websocket.niuniu.NiuniuService;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


/**
 * 一句话描述该类作用:【牛牛服务端,每次建立链接就新建了一个对象】
 *
 * @author: trevor
 * @create: 2019-03-05 22:29
 **/
@ServerEndpoint(
        value = "/niuniu/{roomId}",
        configurator = NiuniuServerConfigurator.class,
        encoders= {MessageEncoder.class},
        decoders = {MessageDecoder.class}
        )
@Component
@Slf4j
public class NiuniuServer {


    private static NiuniuService niuniuService;

    @Resource
    public void setNiuniuService (NiuniuService niuniuService) {
        NiuniuServer.niuniuService = niuniuService;
    }
    
    private static Map<Long , Set<Session>> sessionsMap;

    @Resource(name = "sessionsMap")
    public void setSessions (Map<Long , Set<Session>> sessions) {
        NiuniuServer.sessionsMap = sessions;
    }

    private static Map<Long , RoomPoke> roomPokeMap;

    @Resource(name = "roomPokeMap")
    public void setRoomPokeMap (Map<Long , RoomPoke> roomPokeMap) {
        NiuniuServer.roomPokeMap = roomPokeMap;
    }

    private static UserService userService;

    @Resource
    public void setUserService (UserService userService) {
        NiuniuServer.userService = userService;
    }

    private Session mySession;

    @OnOpen
    public void onOpen(Session session ,@PathParam("roomId") String roomId) throws IOException{
        //设置最大空闲时间为45分钟
        session.setMaxIdleTimeout(1000 * 60 * 45);
        mySession = session;
        List<String> params = session.getRequestParameterMap().get(WebKeys.TOKEN);
        if (params == null || params.isEmpty()) {
            throw new BizException(-500 ,"token 为null");
        }
        String token = session.getRequestParameterMap().get(WebKeys.TOKEN).get(0);
        //token合法性检查
        User user = checkToken(token);

        Set<Session> sessions = sessionsMap.get(Long.valueOf(roomId));
        RoomPoke roomPoke = roomPokeMap.get(Long.valueOf(roomId));
        //加写锁
        roomPoke.getLock().writeLock().lock();
        //检查是否有未删除的session,因为用户网络不好断开连接
        Iterator<Session> itrSession = sessions.iterator();
        while (itrSession.hasNext()) {
            Session targetSession = itrSession.next();
            SocketUser socketUser = (SocketUser) targetSession.getUserProperties().get(WebKeys.WEBSOCKET_USER_KEY);
            if (socketUser != null && Objects.equals(socketUser.getId() ,user.getId())) {
                log.info("连接时移除session，用户id:"+user.getId());
                itrSession.remove();
                break;
            }
        }
        ReturnMessage<SocketUser> returnMessage = niuniuService.onOpenCheck(roomId, user);
        if (returnMessage.getMessageCode() > 0) {
            sessions.add(mySession);
        }
        SocketUser socketUser = returnMessage.getData();
        //不能进入房间
        if (returnMessage.getMessageCode() < 0) {
           WebsocketUtil.sendBasicMessage(mySession ,returnMessage);
           roomPoke.getLock().writeLock().unlock();
           mySession.close();
        //可以进入房间
        } else {
            //将用户放入mySession中
            mySession.getUserProperties().put(WebKeys.WEBSOCKET_USER_KEY, socketUser);
            sendReadyMessage(roomPoke ,sessions ,socketUser);
            roomPoke.getLock().writeLock().unlock();
        }
    }

    @OnMessage
    public void receiveMsg(@PathParam("roomId") String roomId, ReceiveMessage receiveMessage) throws InterruptedException, EncodeException, IOException {
        Integer messageCode = receiveMessage.getMessageCode();
        SocketUser socketUser = (SocketUser) mySession.getUserProperties().get(WebKeys.WEBSOCKET_USER_KEY);
        Long roomIdNum = Long.valueOf(roomId);
        if (Objects.equals(messageCode , 1)) {
            niuniuService.dealReadyMessage( socketUser,roomIdNum);
        }else if (Objects.equals(messageCode ,2)) {
            niuniuService.dealQiangZhuangMessage(socketUser ,roomIdNum ,receiveMessage);
        }else if (Objects.equals(messageCode ,3)) {
            niuniuService.dealXianJiaXiaZhuMessage(socketUser ,roomIdNum ,receiveMessage);
        }else if (Objects.equals(messageCode ,4)) {
            niuniuService.dealTanPaiMessage(socketUser ,roomIdNum);
        }else if (Objects.equals(messageCode ,200)) {
            ReturnMessage<XianJiaXiaZhuMessage> returnMessage = new ReturnMessage<>(null ,200);
            mySession.getAsyncRemote().sendObject(returnMessage);
        }
    }

    @OnClose
    public void disConnect(@PathParam("roomId") String roomId, Session session) {
        RoomPoke roomPoke = roomPokeMap.get(Long.valueOf(roomId));
        Set<Session> sessions = sessionsMap.get(Long.valueOf(roomId));
        if (sessions == null) {
            return;
        }
        //加写锁
        roomPoke.getLock().writeLock().lock();
        Iterator<Session> itrSession = sessions.iterator();
        while (itrSession.hasNext()) {
            Session targetSession = itrSession.next();
            if (Objects.equals(targetSession ,session)) {
                SocketUser user = (SocketUser) targetSession.getUserProperties().get(WebKeys.WEBSOCKET_USER_KEY);
                log.info("房间的sessions移除session，用户id:"+user.getId());
                itrSession.remove();
                break;
            }
        }
        roomPoke.getLock().writeLock().unlock();
    }

    @OnError
    public void onError(Throwable t){
        t.printStackTrace();
        log.error(t.toString());
    }

    /**
     * token合法性检查
     * @param token
     * @throws IOException
     */
    private User checkToken(String token){
        Map<String, Object> claims = TokenUtil.getClaimsFromToken(token);
        String openid = (String) claims.get(WebKeys.OPEN_ID);
        String hash = (String) claims.get("hash");
        Long timestamp = (Long) claims.get("timestamp");
        if (openid == null || hash == null || timestamp == null) {
            throw new BizException(-500 ,"token无法解析，时间：" + System.currentTimeMillis());
        }
        User user = userService.findUserByOpenid(openid);
        if(user == null || !Objects.equals(user.getHash() ,hash)){
            throw new BizException(- 500 ,"openid错误，时间：" + System.currentTimeMillis());
        }
        return user;
    }

    /**
     * 得到玩家userPoke
     * @return
     */
    private UserPoke getUserPoke(RoomPoke roomPoke ,SocketUser socketUser){
        List<UserPokesIndex> userPokesIndexList = roomPoke.getUserPokes();
        if (userPokesIndexList.isEmpty()) {
            return null;
        }
        UserPokesIndex userPokesIndex = userPokesIndexList.stream().filter(u -> Objects.equals(u.getIndex(), roomPoke.getRuningNum()))
                .collect(Collectors.toList()).get(0);
        List<UserPoke> userPoke = userPokesIndex.getUserPokeList().stream().filter(u -> Objects.equals(socketUser.getId(), u.getUserId()))
                .collect(Collectors.toList());
        if (userPoke.isEmpty()) {
            return null;
        }else {
            return userPoke.get(0);
        }
    }

    /**
     * 加入房间时给所有人发送消息
     * @param roomPoke
     * @param sessions
     * @param socketUser
     */
    private void sendReadyMessage(RoomPoke roomPoke ,Set<Session> sessions ,SocketUser socketUser){
        //给自己发所有人信息的消息，给别人发自己的信息
        ReadyReturnMessage readyReturnMessage = new ReadyReturnMessage();
        readyReturnMessage.setRuningNum(roomPoke.getRuningNum());
        readyReturnMessage.setTotalNum(roomPoke.getTotalNum());
        readyReturnMessage.setRoomStatus(roomPoke.getRoomStatus());
        List<SocketUser> mySocketUserList = Lists.newArrayList();
        List<UserScore> userScores = roomPoke.getUserScores();
        Map<Long ,Integer> scoreMap = userScores.stream().collect(Collectors.toMap(UserScore::getUserId ,UserScore::getScore));
        for (Session s : sessions) {
            SocketUser su = (SocketUser) s.getUserProperties().get(WebKeys.WEBSOCKET_USER_KEY);
            UserPoke userPoke = getUserPoke(roomPoke ,su);
            if (userPoke == null) {
                su.setIsReady(Boolean.FALSE);
            }else {
                su.setIsReady(Boolean.TRUE);
            }
            su.setScore(scoreMap.get(su.getId()) != null? scoreMap.get(su.getId()):0);
            mySocketUserList.add(su);
        }
        readyReturnMessage.setSocketUserList(mySocketUserList);
        ReturnMessage<ReadyReturnMessage> myReturnMessage = new ReturnMessage<>(readyReturnMessage, 0);
        WebsocketUtil.sendBasicMessage(mySession, myReturnMessage);
        //给别人发自己的信息
        for (Session s : sessions) {
            SocketUser su = (SocketUser) s.getUserProperties().get(WebKeys.WEBSOCKET_USER_KEY);
            socketUser.setScore(0);
            socketUser.setIsReady(false);
            if (!Objects.equals(su.getId() ,socketUser.getId())) {
                List<SocketUser> otherSocketUserList = Lists.newArrayList();
                otherSocketUserList.add(socketUser);
                readyReturnMessage.setSocketUserList(otherSocketUserList);
                ReturnMessage<ReadyReturnMessage> otherReturnMessage = new ReturnMessage<>(readyReturnMessage, 1);
                WebsocketUtil.sendBasicMessage(s, otherReturnMessage);
            }
        }
    }

}
