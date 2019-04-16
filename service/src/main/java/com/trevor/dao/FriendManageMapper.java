package com.trevor.dao;

import com.trevor.bo.FriendInfo;
import com.trevor.domain.FriendsManage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-03 23:59
 **/
@Repository
public interface FriendManageMapper {

    /**
     * 查询玩家是否是房主的好友，并且通过
     * @param userId
     * @param manageFriendId
     * @return
     */
    Long countRoomAuthFriendAllow(Long userId ,Long manageFriendId);

    /**
     * 根据用户id查询管理的好友
     * @param userId
     * @return
     */
    List<FriendsManage> findByUserId(@Param("userId") Long userId);
}
