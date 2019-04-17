package com.trevor.service.friendManager;

import com.trevor.bo.FriendInfo;
import com.trevor.bo.JsonEntity;
import com.trevor.domain.User;
import springfox.documentation.spring.web.json.Json;

import java.util.List;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-03 23:56
 **/

public interface FriendManagerService {

    /**
     * 查询好友（申请通过和未通过的）
     * @return
     */
    List<FriendInfo> queryFriends(User user);

    /**
     * 申请好友
     * @param roomId
     * @param applyUserId
     * @return
     */
    JsonEntity<Object> applyFriend(Long roomId ,Long applyUserId);


    /**
     * 申请好友
     * @param userId
     * @return
     */
    JsonEntity<Object> passFriend(Long userId ,Long passUserId);


    /**
     * 剔除好友
     * @param userId
     * @return
     */
    JsonEntity<Object> removeFriend(Long userId ,Long removeUserId);

}
