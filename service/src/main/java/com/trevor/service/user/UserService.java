package com.trevor.service.user;


import com.trevor.bo.WebSessionUser;
import com.trevor.domain.User;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;

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
     * 新增一个用户
     * @param user
     */
    void insertOne(User user);

    /**
     * 根据微信id查询用户，包含openid和hash字段
     * @param openid
     * @return
     */
    User findUserByOpenidContainOpenidAndHash(String openid);

    /**
     * 根据微信id查询用户，包含id，weixinName，weixinPictureUrl字段
     * @param openid
     * @return
     */
    User findUserByOpenIdContainIdAndAppNameAndPicture(String openid);

    /**
     * 更新hash值
     * @param hash
     * @param openid
     */
    void updateHash(String hash ,String openid);

    /**
     * 根据openid查询WebSessionUser
     * @param openid
     * @return
     */
    WebSessionUser getWebSessionUserByOpneid(String openid);

    /**
     * 根据phoneNum查找用户是否存在
     * @param phoneNum
     * @return
     */
    Boolean isExistByPhoneNum(String phoneNum);

    /**
     * 根据phoneNum查询WebSessionUser
     * @param phoneNum
     * @return
     */
    WebSessionUser getWebSessionUserByPhone(String phoneNum);

}
