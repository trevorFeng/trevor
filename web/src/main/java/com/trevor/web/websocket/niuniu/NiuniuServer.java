package com.trevor.web.websocket.niuniu;

import com.google.common.collect.Lists;
import com.trevor.BizException;
import com.trevor.bo.*;
import com.trevor.domain.Room;
import com.trevor.domain.User;
import com.trevor.enums.GameStatusEnum;
import com.trevor.enums.MessageCodeEnum;
import com.trevor.service.RoomService;
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

    private static RoomService roomService;

    @Resource
    public void setRoomService (RoomService roomService) {
        NiuniuServer.roomService = roomService;
    }

    private Session mySession;

    @OnOpen
    public void onOpen(Session session ,@PathParam("roomId") Long roomId) throws IOException{
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
        //查出房间配置
        Room room = roomService.findOneById(Long.valueOf(roomId));
        if (room == null) {
            ReturnMessage<String> returnMessage = new ReturnMessage<>("房间不存在" ,-1);
            WebsocketUtil.sendBasicMessage(session ,returnMessage);
            return;
        }
        //房间已关闭
        if (Objects.equals(room.getState() ,0)) {
            ReturnMessage<String> returnMessage = new ReturnMessage<>("房间已关闭" ,-1);
            WebsocketUtil.sendBasicMessage(session ,returnMessage);
            return;
        }
        //对sessions操作，加读锁
        roomPoke.getLock().readLock().lock();
        Boolean isRepeatUserId = checkIsRepeatConnection(sessions ,user);
        roomPoke.getLock().readLock().unlock();

        //加写锁
        roomPoke.getLock().writeLock().lock();
        List<RealWanJiaInfo> realWanJias = roomPoke.getRealWanJias();
        List<Long> realWanJiaIds = realWanJias.stream().map(realWanJiaInfo -> realWanJiaInfo.getId()).collect(Collectors.toList());
        //是真正的玩家
        if (realWanJiaIds.contains(user.getId())) {
            //检查是否是因为用户网络不好而断开的连接而引发的重连,是的话删除session,给玩家发送游戏状态消息和本人的信息，其他人的基本信息
            if (isRepeatUserId) {
                //在session中放入是否是观众信息和玩家id
                mySession.getUserProperties().put(WebKeys.WEBSOCKET_IS_CHIGUA, Boolean.FALSE);
                mySession.getUserProperties().put(WebKeys.WEBSOCKET_USER_ID, user.getId());
                sessions.add(mySession);
                //给自己发自己，其他人（真正玩家）的基本信息、分数
                List<SocketUser> socketUserList = generateList(realWanJias ,user ,roomPoke);
                ReturnMessage<List<SocketUser>> returnMessage = new ReturnMessage<>(socketUserList ,23);
                WebsocketUtil.sendBasicMessage(session ,returnMessage);
                return;
                //是由于关闭浏览器而退出的玩家
            }else {
                //给其他玩家发送重新进来的消息
                ReturnMessage<Long> returnMessage = new ReturnMessage<>(user.getId() ,21);
                WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage);
                //给自己发送游戏状态和本人和别人的的信息
                List<SocketUser> socketUserList = generateList(realWanJias ,user ,roomPoke);
                ReturnMessage<List<SocketUser>> myReturnMessage = new ReturnMessage<>(socketUserList ,20);
                WebsocketUtil.sendBasicMessage(session ,myReturnMessage);
                //在session中放入是否是观众信息和玩家id
                mySession.getUserProperties().put(WebKeys.WEBSOCKET_IS_CHIGUA, Boolean.FALSE);
                mySession.getUserProperties().put(WebKeys.WEBSOCKET_USER_ID, user.getId());
                sessions.add(mySession);
                return;
            }
        //是观众或者第一次进来的玩家
        }else {
            ReturnMessage<SocketUser> onOpenCheck = niuniuService.onOpenCheck(room ,user);
            if (onOpenCheck.getMessageCode() > 0) {
                log.info("用户id:" + user.getId() + "加入房间，房间id:" + roomId);
                SocketUser onOpenCheckUser = onOpenCheck.getData();
                //吃瓜群众返回真正玩家的信息
                if (onOpenCheckUser.getIsChiGuaPeople()) {
                    List<SocketUser> socketUserList = generateList(realWanJias ,user ,roomPoke);
                    ReturnMessage<List<SocketUser>> returnMessage = new ReturnMessage<>(socketUserList ,24);
                    WebsocketUtil.sendBasicMessage(session ,returnMessage);
                    //在session中放入是否是观众信息和玩家id
                    mySession.getUserProperties().put(WebKeys.WEBSOCKET_IS_CHIGUA, Boolean.TRUE);
                    mySession.getUserProperties().put(WebKeys.WEBSOCKET_USER_ID, user.getId());
                    sessions.add(mySession);
                    //真正的玩家
                }else {
                    //添加到realWanJias
                    RealWanJiaInfo realWanJiaInfo = new RealWanJiaInfo();
                    realWanJiaInfo.setId(user.getId());
                    realWanJiaInfo.setName(user.getAppName());
                    realWanJiaInfo.setPicture(user.getAppPictureUrl());
                    realWanJiaInfo.setIsGuanZhong(Boolean.FALSE);
                    realWanJiaInfo.setIsReady(Boolean.FALSE);
                    realWanJiaInfo.setScore(0);
                    realWanJiaInfo.setIsZhuangJia(Boolean.FALSE);
                    realWanJiaInfo.setIsUnconnection(Boolean.FALSE);
                    roomPoke.getRealWanJias().add(realWanJiaInfo);

                    //给自己发消息
                    List<SocketUser> socketUserList = generateList(realWanJias ,user ,roomPoke);
                    SocketUser socketUser = new SocketUser();
                    socketUser.setIsMyself(Boolean.TRUE);
                    socketUser.setId(user.getId());
                    socketUser.setName(user.getAppName());
                    socketUser.setPicture(user.getAppPictureUrl());
                    socketUser.setIsChiGuaPeople(Boolean.FALSE);
                    socketUser.setIsNewUser(Boolean.TRUE);
                    socketUser.setStatus(roomPoke.getGameStatus());
                    socketUser.setIsGuanZhong(Boolean.FALSE);
                    socketUser.setScore(0);
                    socketUser.setIsUnconnection(Boolean.FALSE);
                    socketUserList.add(socketUser);
                    ReturnMessage<List<SocketUser>> myReturnMessage = new ReturnMessage<>(socketUserList ,25);
                    WebsocketUtil.sendBasicMessage(session ,myReturnMessage);

                    //给别的玩家发消息
                    socketUser.setIsMyself(null);
                    ReturnMessage<SocketUser> returnMessage = new ReturnMessage<>(socketUser ,26);
                    WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage);

                    //在session中放入是否是观众信息和玩家id
                    mySession.getUserProperties().put(WebKeys.WEBSOCKET_IS_CHIGUA, Boolean.FALSE);
                    mySession.getUserProperties().put(WebKeys.WEBSOCKET_USER_ID, user.getId());
                    sessions.add(mySession);
                }
            }else {
                ReturnMessage<String> returnMessage = new ReturnMessage<>(onOpenCheck.getMessage() ,-1);
                WebsocketUtil.sendBasicMessage(session ,returnMessage);
                return;
            }
        }
        roomPoke.getLock().writeLock().unlock();
    }

    /**
     * 检查是否是因为用户网络不好而断开的连接而引发的重连
     * @param sessions
     * @param user
     * @return
     * @throws IOException
     */
    private Boolean checkIsRepeatConnection(Set<Session> sessions ,User user) throws IOException {
        Boolean isRepeatUserId = Boolean.FALSE;
        for (Session s : sessions) {
            Long userId = (Long) s.getUserProperties().get(WebKeys.WEBSOCKET_USER_ID);
            if (userId != null && Objects.equals(userId ,user.getId())) {
                isRepeatUserId = Boolean.TRUE;
                log.info("有重复session，用户id:"+user.getId());
                //记一个重复userId的标记，在onclose注解标记的方法上用
                s.getUserProperties().put("isRepeat" ,true);
                s.close();
                break;
            }
        }
        return isRepeatUserId;
    }

//    /**
//     * 检查是否是因为用户网络不好而断开的连接而引发的重连
//     * @param sessions
//     * @param user
//     * @return
//     */
//    private Boolean checkIsGuanZhong(Set<Session> sessions ,User user){
//        Boolean isGuanZhong = Boolean.FALSE;
//        for (Session s : sessions) {
//            Long userId = (Long) s.getUserProperties().get(WebKeys.WEBSOCKET_USER_ID);
//            if (userId != null && Objects.equals(userId ,user.getId())) {
//                isGuanZhong = (Boolean) s.getUserProperties().get("isGuanZhong");
//                break;
//            }
//        }
//        return isGuanZhong;
//    }

//    private void removeSession(Set<Session> sessions ,User user) throws IOException {
//        for (Session s : sessions) {
//            Long userId = (Long) s.getUserProperties().get(WebKeys.WEBSOCKET_USER_ID);
//            if (userId != null && Objects.equals(userId ,user.getId())) {
//
//                break;
//            }
//        }
//    }

    @OnMessage
    public void onMessage(@PathParam("roomId") Long roomId, ReceiveMessage receiveMessage) throws InterruptedException, EncodeException, IOException {
        Integer messageCode = receiveMessage.getMessageCode();
        Long userId = (Long) mySession.getUserProperties().get(WebKeys.WEBSOCKET_USER_ID);
        if (Objects.equals(messageCode , 1)) {
            niuniuService.dealReadyMessage(mySession ,userId ,roomId);
        }else if (Objects.equals(messageCode ,2)) {
            niuniuService.dealQiangZhuangMessage(mySession ,userId ,roomId ,receiveMessage);
        }else if (Objects.equals(messageCode ,3)) {
            niuniuService.dealXianJiaXiaZhuMessage(mySession ,userId ,roomId ,receiveMessage);
        }else if (Objects.equals(messageCode ,4)) {
            niuniuService.dealTanPaiMessage(userId ,roomId);
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
        Long userId = 0L;
        Boolean isNormalClose = Boolean.TRUE;
        Boolean isChiGua = Boolean.FALSE;
        while (itrSession.hasNext()) {
            Session targetSession = itrSession.next();
            if (Objects.equals(targetSession ,session)) {
                userId = (Long) targetSession.getUserProperties().get(WebKeys.WEBSOCKET_USER_ID);
                //是由于网络断开而重新发起的连接，直接移除掉
                if (session.getUserProperties().get("isRepeat") != null) {
                    log.info("重复session，用户id:"+userId+"断开连接，移除session");
                    itrSession.remove();
                    break;
                }
                //是不是观众
                isChiGua = (Boolean) session.getUserProperties().get(WebKeys.WEBSOCKET_IS_CHIGUA);
                //由于用户直接关闭浏览器而不正常的关闭，正常关闭的isNormalClose的值为true,给所有的玩家发消息该玩家断开
                isNormalClose = session.getUserProperties().get("isNormalClose") == null ? Boolean.FALSE : Boolean.TRUE;
                if (!isNormalClose) {
                    log.info("关闭浏览器，不正常断开，用户id:"+userId+"断开连接，移除session");
                    itrSession.remove();
                    break;
                }else {
                    log.info("正常断开，用户id:"+userId+"断开连接，移除session");
                    itrSession.remove();
                    break;
                }
            }
        }
        //是真正的玩家，并且已经断开，给所有的人发消息，该玩家已经断开,标记已经断开
        if (!isNormalClose && !isChiGua) {
            for (RealWanJiaInfo realWanJiaInfo : roomPoke.getRealWanJias()) {
                if (Objects.equals(realWanJiaInfo.getId() ,userId)) {
                    realWanJiaInfo.setIsUnconnection(Boolean.TRUE);
                    break;
                }
            }
            ReturnMessage<Long> returnMessage = new ReturnMessage<>(userId ,22);
            WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage);
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

//    /**
//     * 得到玩家userPoke
//     * @return
//     */
//    private UserPoke getUserPoke(RoomPoke roomPoke ,Long userId){
//        List<UserPokesIndex> userPokesIndexList = roomPoke.getUserPokes();
//        if (userPokesIndexList.isEmpty()) {
//            return null;
//        }
//        UserPokesIndex userPokesIndex = userPokesIndexList.stream().filter(u -> Objects.equals(u.getIndex(), roomPoke.getRuningNum()))
//                .collect(Collectors.toList()).get(0);
//        List<UserPoke> userPoke = userPokesIndex.getUserPokeList().stream().filter(u -> Objects.equals(userId, u.getUserId()))
//                .collect(Collectors.toList());
//        if (userPoke.isEmpty()) {
//            return null;
//        }else {
//            return userPoke.get(0);
//        }
//    }

//    /**
//     * 加入房间时给所有人发送消息
//     * @param roomPoke
//     * @param sessions
//     * @param socketUser
//     */
//    private void sendReadyMessage(RoomPoke roomPoke ,Set<Session> sessions ,SocketUser socketUser){
//        //给自己发所有人信息的消息，给别人发自己的信息
//        ReadyReturnMessage readyReturnMessage = new ReadyReturnMessage();
//        readyReturnMessage.setRuningNum(roomPoke.getRuningNum());
//        readyReturnMessage.setTotalNum(roomPoke.getTotalNum());
//        readyReturnMessage.setRoomStatus(roomPoke.getRoomStatus());
//        List<SocketUser> mySocketUserList = Lists.newArrayList();
//        List<UserScore> userScores = roomPoke.getUserScores();
//        Map<Long ,Integer> scoreMap = userScores.stream().collect(Collectors.toMap(UserScore::getUserId ,UserScore::getScore));
//        for (Session s : sessions) {
//            SocketUser su = (SocketUser) s.getUserProperties().get(WebKeys.WEBSOCKET_USER_ID);
//            UserPoke userPoke = getUserPoke(roomPoke ,su);
//            if (userPoke == null) {
//                su.setIsReady(Boolean.FALSE);
//            }else {
//                su.setIsReady(Boolean.TRUE);
//            }
//            su.setScore(scoreMap.get(su.getId()) != null? scoreMap.get(su.getId()):0);
//            mySocketUserList.add(su);
//        }
//        readyReturnMessage.setSocketUserList(mySocketUserList);
//        ReturnMessage<ReadyReturnMessage> myReturnMessage = new ReturnMessage<>(readyReturnMessage, 0);
//        WebsocketUtil.sendBasicMessage(mySession, myReturnMessage);
//        //给别人发自己的信息
//        for (Session s : sessions) {
//            SocketUser su = (SocketUser) s.getUserProperties().get(WebKeys.WEBSOCKET_USER_ID);
//            socketUser.setScore(0);
//            socketUser.setIsReady(false);
//            if (!Objects.equals(su.getId() ,socketUser.getId())) {
//                List<SocketUser> otherSocketUserList = Lists.newArrayList();
//                otherSocketUserList.add(socketUser);
//                readyReturnMessage.setSocketUserList(otherSocketUserList);
//                ReturnMessage<ReadyReturnMessage> otherReturnMessage = new ReturnMessage<>(readyReturnMessage, 1);
//                WebsocketUtil.sendBasicMessage(s, otherReturnMessage);
//            }
//        }
//    }

    private List<SocketUser> generateList(List<RealWanJiaInfo> realWanJias ,User user ,RoomPoke roomPoke) {
        List<SocketUser> socketUserList = Lists.newArrayList();
        List<UserScore> userScores = roomPoke.getUserScores();
        Map<Long ,Integer> scoreMap = userScores.stream().collect(Collectors.toMap(UserScore::getUserId ,UserScore::getScore));
        realWanJias.forEach(r -> {
            SocketUser socketUser = new SocketUser();
            if (Objects.equals(r.getId() ,user.getId())) {
                socketUser.setIsMyself(Boolean.TRUE);
            }else {
                socketUser.setIsMyself(Boolean.FALSE);
            }
            socketUser.setId(r.getId());
            socketUser.setName(r.getName());
            socketUser.setPicture(r.getPicture());
            socketUser.setIsChiGuaPeople(Boolean.FALSE);
            socketUser.setIsNewUser(Boolean.FALSE);
            socketUser.setStatus(roomPoke.getGameStatus());
            socketUser.setIsGuanZhong(r.getIsGuanZhong());
            socketUser.setScore(scoreMap.get(r.getId()));
            if (Objects.equals(r.getId() ,user.getId())) {
                socketUser.setIsUnconnection(Boolean.FALSE);
            }else {
                socketUser.setIsUnconnection(r.getIsUnconnection());
            }
            if (Objects.equals(GameStatusEnum.BEFORE_FAPAI_4.getCode() ,roomPoke.getGameStatus())) {
                socketUser.setIsReady((r.getIsReady() == null || Objects.equals(r.getIsReady() ,Boolean.FALSE))? Boolean.FALSE :Boolean.TRUE);
            }
            socketUserList.add(socketUser);

        });
        return socketUserList;
    }

    /**
     * 处理第一次进来的玩家
     * @param room
     * @param user
     * @param roomId
     * @param roomPoke
     * @param realWanJias
     * @param session
     * @param sessions
     * @throws IOException
     */
//    private void processIsFirstComing(Room room ,User user ,Long roomId ,RoomPoke roomPoke ,
//                                List<RealWanJiaInfo> realWanJias ,Session session ,Set<Session> sessions) throws IOException {
//        ReturnMessage<SocketUser> onOpenCheck = niuniuService.onOpenCheck(room ,user);
//        if (onOpenCheck.getMessageCode() > 0) {
//            log.info("用户id:" + user.getId() + "加入房间，房间id:" + roomId);
//            SocketUser onOpenCheckUser = onOpenCheck.getData();
//            //吃瓜群众返回真正玩家的信息
//            if (onOpenCheckUser.getIsChiGuaPeople()) {
//                List<SocketUser> socketUserList = generateList(realWanJias ,user ,roomPoke);
//                ReturnMessage<List<SocketUser>> returnMessage = new ReturnMessage<>(socketUserList ,24);
//                WebsocketUtil.sendBasicMessage(session ,returnMessage);
//                //在session中放入是否是观众信息和玩家id
//                mySession.getUserProperties().put(WebKeys.WEBSOCKET_IS_CHIGUA, Boolean.TRUE);
//                mySession.getUserProperties().put(WebKeys.WEBSOCKET_USER_ID, user.getId());
//                sessions.add(mySession);
//                //真正的玩家
//            }else {
//                //添加到realWanJias
//                RealWanJiaInfo realWanJiaInfo = new RealWanJiaInfo();
//                realWanJiaInfo.setId(user.getId());
//                realWanJiaInfo.setName(user.getAppName());
//                realWanJiaInfo.setPicture(user.getAppPictureUrl());
//                realWanJiaInfo.setIsGuanZhong(Boolean.FALSE);
//                realWanJiaInfo.setIsReady(Boolean.FALSE);
//                realWanJiaInfo.setScore(0);
//                realWanJiaInfo.setIsZhuangJia(Boolean.FALSE);
//                realWanJiaInfo.setIsUnconnection(Boolean.FALSE);
//                roomPoke.getRealWanJias().add(realWanJiaInfo);
//
//                //给自己发消息
//                List<SocketUser> socketUserList = generateList(realWanJias ,user ,roomPoke);
//                SocketUser socketUser = new SocketUser();
//                socketUser.setIsMyself(Boolean.TRUE);
//                socketUser.setId(user.getId());
//                socketUser.setName(user.getAppName());
//                socketUser.setPicture(user.getAppPictureUrl());
//                socketUser.setIsChiGuaPeople(Boolean.FALSE);
//                socketUser.setIsNewUser(Boolean.TRUE);
//                socketUser.setStatus(roomPoke.getGameStatus());
//                socketUser.setIsGuanZhong(Boolean.FALSE);
//                socketUser.setScore(0);
//                socketUser.setIsUnconnection(Boolean.FALSE);
//                socketUserList.add(socketUser);
//                ReturnMessage<List<SocketUser>> myReturnMessage = new ReturnMessage<>(socketUserList ,25);
//                WebsocketUtil.sendBasicMessage(session ,myReturnMessage);
//
//                //给别的玩家发消息
//                socketUser.setIsMyself(null);
//                ReturnMessage<SocketUser> returnMessage = new ReturnMessage<>(socketUser ,26);
//                WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage);
//
//                //在session中放入是否是观众信息和玩家id
//                mySession.getUserProperties().put(WebKeys.WEBSOCKET_IS_CHIGUA, Boolean.FALSE);
//                mySession.getUserProperties().put(WebKeys.WEBSOCKET_USER_ID, user.getId());
//                sessions.add(mySession);
//            }
//        }else {
//            ReturnMessage<String> returnMessage = new ReturnMessage<>(onOpenCheck.getMessage() ,-1);
//            WebsocketUtil.sendBasicMessage(session ,returnMessage);
//            return;
//        }
//
//    }

    /**
     * 处理不是一次进来的玩家
     * @param sessions
     * @param user
     * @param roomPoke
     * @param realWanJias
     * @param session
     * @throws IOException
     */
//    private void processReComing(Set<Session> sessions ,User user ,RoomPoke roomPoke ,List<RealWanJiaInfo> realWanJias ,Session session) throws IOException {
//        Boolean isRepeatUserId = checkIsRepeatConnection(sessions ,user);
//        //检查是否是因为用户网络不好而断开的连接而引发的重连,是的话删除session,给玩家发送游戏状态消息和本人的信息，其他人的基本信息
//        if (isRepeatUserId) {
//            //在session中放入是否是观众信息和玩家id
//            mySession.getUserProperties().put(WebKeys.WEBSOCKET_IS_CHIGUA, Boolean.FALSE);
//            mySession.getUserProperties().put(WebKeys.WEBSOCKET_USER_ID, user.getId());
//            sessions.add(mySession);
//            //给自己发自己，其他人（真正玩家）的基本信息、分数
//            List<SocketUser> socketUserList = generateList(realWanJias ,user ,roomPoke);
//            ReturnMessage<List<SocketUser>> returnMessage = new ReturnMessage<>(socketUserList ,23);
//            WebsocketUtil.sendBasicMessage(session ,returnMessage);
//            return;
//            //是由于关闭浏览器而退出的玩家
//        }else {
//            //给其他玩家发送重新进来的消息
//            ReturnMessage<Long> returnMessage = new ReturnMessage<>(user.getId() ,21);
//            WebsocketUtil.sendAllBasicMessage(sessions ,returnMessage);
//            //给自己发送游戏状态和本人和别人的的信息
//            List<SocketUser> socketUserList = generateList(realWanJias ,user ,roomPoke);
//            ReturnMessage<List<SocketUser>> myReturnMessage = new ReturnMessage<>(socketUserList ,20);
//            WebsocketUtil.sendBasicMessage(session ,myReturnMessage);
//            //在session中放入是否是观众信息和玩家id
//            mySession.getUserProperties().put(WebKeys.WEBSOCKET_IS_CHIGUA, Boolean.FALSE);
//            mySession.getUserProperties().put(WebKeys.WEBSOCKET_USER_ID, user.getId());
//            sessions.add(mySession);
//            return;
//        }
//    }

}
