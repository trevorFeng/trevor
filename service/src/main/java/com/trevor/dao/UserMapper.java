package com.trevor.dao;

import com.trevor.bo.Authentication;
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
    Integer isFriendManage(@Param("userId") Long userId);

    /**
     * 根据openid查找用户是否存在
     * @param openid
     * @return
     */
    Long isExistByOpnenId(@Param("openid") String openid);

    /**
     * 根据openid查询用户，包含openid和hash字段
     * @param openid
     * @return
     */
    User findUserByOpenidContainOpenidAndHash(@Param("openid") String openid);

    /**
     * 根据openid查询用户，包含id，weixinName，weixinPictureUrl字段
     * @param openid
     * @return
     */
    User findUserByOpenIdContainIdAndAppNameAndPicture(@Param("openid") String openid);

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

    /**
     * 根据手机号查询用户是否存在
     * @param phoneNum
     * @return
     */
    Long isExistByPhoneNum(@Param("phoneNum") String phoneNum);

    /**
     * 根据phoneNum查询用户，包含id，weixinName，weixinPictureUrl字段
     * @param phoneNum
     * @return
     */
    User findUserByPhoneNumContainIdAndAppNameAndPicture(@Param("phoneNum") String phoneNum);

    /**
     * 根据用户id更新手机号
     * @param userId
     * @param phoneNum
     */
    void updatePhoneNumByUserId(@Param("userId") Long userId ,@Param("phoneNum") String phoneNum);

    /**
     * 实名认证
     * @param userId
     * @param authentication
     */
    void realNameAuth(@Param("userId") Long userId , @Param("authentication") Authentication authentication);

}
