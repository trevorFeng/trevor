package com.trevor.web.websocket.niuniu;

import com.google.common.collect.Lists;
import com.trevor.BizException;
import com.trevor.bo.*;
import com.trevor.domain.User;
import com.trevor.service.user.UserService;
import com.trevor.util.TokenUtil;
import com.trevor.util.WebsocketUtil;
import com.trevor.web.websocket.decoder.MessageDecoder;
import com.trevor.web.websocket.encoder.MessageEncoder;
import com.trevor.websocket.bo.ReceiveMessage;
import com.trevor.websocket.bo.ReturnMessage;
import com.trevor.websocket.bo.SocketUser;
import com.trevor.websocket.niuniu.NiuniuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 一句话描述该类作用:【牛牛服务端,每次建立链接就新建了一个对象】
 *
 * @author: trevor
 * @create: 2019-03-05 22:29
 **/
@ServerEndpoint(
        value = "/niuniu/{roomId}",
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
        //检查是否有未删除的session,因为用户网络不好而断开的连接，而session还存在于sessions中
        Boolean isRepeat = Boolean.FALSE;
        for (Session s : sessions) {
            Long userId = (Long) s.getUserProperties().get(WebKeys.WEBSOCKET_USER_ID);
            if (userId != null && Objects.equals(userId ,user.getId())) {
                log.info("有重复session，用户id:"+user.getId());
                isRepeat = Boolean.TRUE;
                s.getUserProperties().put("isRepeat" ,true);
                s.close();
                break;
            }
        }
        //检查是否是真正的玩家退出浏览器后重新进入的玩家
        if (!isRepeat && roomPoke.getRealWanJiaIds().contains(user.getId())) {
            //给其他人发一个已经重新连接的消息
            ReturnMessage<Object> returnMessage = new ReturnMessage<>(null ,21);
            WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage);
            //给自己基本信息，分数，手里的牌
            if (sessions.isEmpty()) {

            }
        }
       //网路断开而引发的重连
        if (isRepeat) {
            //检查该用户是否可以连接
            ReturnMessage<SocketUser> returnMessage = niuniuService.onOpenCheck(roomId ,user);
            if (returnMessage.getMessageCode() > 0) {
                log.info("用户id:" + user.getId() + "加入房间，房间id:" + roomId);
                sessions.add(mySession);
            }
        }else {
            //检查该用户是否可以连接
            ReturnMessage<SocketUser> returnMessage = niuniuService.onOpenCheck(roomId ,user);
            if (returnMessage.getMessageCode() > 0) {
                log.info("用户id:" + user.getId() + "加入房间，房间id:" + roomId);
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
                mySession.getUserProperties().put(WebKeys.WEBSOCKET_USER_ID, socketUser.getId());
                sendReadyMessage(roomPoke ,sessions ,socketUser);
                roomPoke.getLock().writeLock().unlock();
            }
        }

    }

    @OnMessage
    public void onMessage(@PathParam("roomId") String roomId, ReceiveMessage receiveMessage) throws InterruptedException, EncodeException, IOException {
        Integer messageCode = receiveMessage.getMessageCode();
        Long userId = (Long) mySession.getUserProperties().get(WebKeys.WEBSOCKET_USER_ID);
        Long roomIdNum = Long.valueOf(roomId);
        if (Objects.equals(messageCode , 1)) {
            niuniuService.dealReadyMessage( userId,roomIdNum);
        }else if (Objects.equals(messageCode ,2)) {
            niuniuService.dealQiangZhuangMessage(userId ,roomIdNum ,receiveMessage);
        }else if (Objects.equals(messageCode ,3)) {
            niuniuService.dealXianJiaXiaZhuMessage(mySession ,userId ,roomIdNum ,receiveMessage);
        }else if (Objects.equals(messageCode ,4)) {
            niuniuService.dealTanPaiMessage(userId ,roomIdNum);
        }else if (Objects.equals(messageCode ,200)) {
            ReturnMessage<XianJiaXiaZhuMessage> returnMessage = new ReturnMessage<>(null ,200);
            mySession.getAsyncRemote().sendObject(returnMessage);
        }
    }

    /**
     * 1.关闭浏览器调用此方法，断网不会调用
     * 2.断网的时候，浏览器发送ws请求，重新连上会发送消息过来
     * 3.@OnError注解标记的方法执行后会调用此方法
     * 4.用户关闭浏览器应该给房间里的人发离线消息
     * @param roomId
     * @param session
     */
    @OnClose
    public void onClose(@PathParam("roomId") String roomId, Session session) {
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
                //是由于网络断开而重新发起的连接
                if (session.getUserProperties().get("isRepeat") != null) {

                }
                //由于用户直接关闭浏览器而不正常的关闭
                if (session.getUserProperties().get("unNormal") != null) {

                }
                SocketUser user = (SocketUser) targetSession.getUserProperties().get(WebKeys.WEBSOCKET_USER_ID);
                log.info("用户id:"+user.getId()+"断开连接，移除session");
                itrSession.remove();
                break;
            }
        }

        roomPoke.getLock().writeLock().unlock();
    }

    /**
     * 1.@OnOpen注解的方法抛异常会调用此方法
     * 2.@OnMessage标记的方法抛异常会调用此方法
     * @param t
     */
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
            throw new BizException(-500 ,"openid错误，时间：" + System.currentTimeMillis());
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
            SocketUser su = (SocketUser) s.getUserProperties().get(WebKeys.WEBSOCKET_USER_ID);
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
            SocketUser su = (SocketUser) s.getUserProperties().get(WebKeys.WEBSOCKET_USER_ID);
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
