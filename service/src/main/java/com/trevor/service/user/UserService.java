package com.trevor.service.user;


import com.trevor.bo.Authentication;
import com.trevor.domain.User;

public interface UserService {

    /**
     * 查询玩家是否开启好友管理功能,1为是，0为否
     * @param userId
     * @return
     */
    Boolean isFriendManage(Long userId);

    /**
     * 根据openid查找用户是否存在
     * @param openid
     * @return
     */
    Boolean isExistByOpnenId(String openid);

    /**
     * 新增一个用户,返回主键
     * @param user
     */
    void insertOne(User user);

    /**
     * 根据微信id查询用户，包含openid和hash字段
     * @param openid
     * @return
     */
    User findUserByOpenid(String openid);


    /**
     * 更新hash值
     */
    void updateUser(User user);


    /**
     * 根据phoneNum查找用户是否存在
     * @param phoneNum
     * @return
     */
    Boolean isExistByPhoneNum(String phoneNum);

    /**
     * 根据phoneNum查询user 包含openid 和hash
     * @param phoneNum
     * @return
     */
    User getUserByPhoneNumContainOpenidAndHash(String phoneNum);

    /**
     * 根据用户id绑定手机号
     * @param userId
     * @param phoneNum
     */
    void updatePhoneByUserId(Long userId ,String phoneNum);

    /**
     * 实名认证
     * @param authentication
     * @return
     */
    void realNameAuth(Long userId , Authentication authentication);

}
