package com.trevor.dao;

import com.trevor.domain.User;
import org.apache.ibatis.annotations.Param;
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

    /**
     * 根据微信id查找用户是否存在
     * @param openid
     * @return
     */
    Long findByWeiXinId(@Param("openid") String openid);

    /**
     * 根据微信id查询用户，包含openid和hash字段
     * @param openid
     * @return
     */
    User findUserOpenidAndHash(@Param("openid") String openid);

    /**
     * 根据微信id查询用户，包含id，weixinName，weixinPictureUrl字段
     * @param openid
     * @return
     */
    User findUserByWeiXinId(@Param("openid") String openid);

    /**
     * 新增一个用户
     * @param user
     */
    void insertOne(@Param("user") User user);

    /**
     * 更新hash值
     * @param hash
     * @param openid
     */
    void updateHash(@Param("hash") String hash ,@Param("openid") String openid);

}
