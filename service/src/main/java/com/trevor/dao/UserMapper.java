package com.trevor.dao;

import org.springframework.stereotype.Repository;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-09 14:13
 **/
@Repository
public interface UserMapper {

    /**
     * 查询玩家是否开启好友管理功能,1为是，0为否
     * @param userId
     * @return
     */
    Integer findUserFriendManage(Long userId);
}
