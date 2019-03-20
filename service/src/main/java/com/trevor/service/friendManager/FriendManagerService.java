package com.trevor.service.friendManager;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.WebSessionUser;
import com.trevor.domain.FriendsManage;

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
     * @param webSessionUser
     * @return
     */
    JsonEntity<List<FriendsManage>> findRecevedCardRecord(WebSessionUser webSessionUser);

}
