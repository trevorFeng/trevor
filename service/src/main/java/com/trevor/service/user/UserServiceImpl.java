package com.trevor.service.user;

import com.trevor.bo.Authentication;
import com.trevor.bo.WebSessionUser;
import com.trevor.dao.UserMapper;
import com.trevor.domain.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService{

    @Resource
    private UserMapper userMapper;

    /**
     * 查询玩家是否开启好友管理功能,1为是，0为否
     * @param userId
     * @return
     */
    @Override
    public Boolean isFriendManage(Long userId) {
        Integer userFriendManage = userMapper.isFriendManage(userId);
        if (Objects.equals(1 ,userFriendManage)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 根据openid查找用户是否存在
     * @param openid
     * @return
     */
    @Override
    public Boolean isExistByOpnenId(String openid) {
        Long existByOpnenId = userMapper.isExistByOpnenId(openid);
        if (Objects.equals(existByOpnenId ,0L)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 新增一个用户
     * @param user
     */
    @Override
    public void insertOne(User user) {
        userMapper.insertOne(user);
    }


    /**
     * 根据微信id查询用户，包含openid和hash字段
     * @param openid
     * @return
     */
    @Override
    public User findUserByOpenidContainOpenidAndHash(String openid) {
        return userMapper.findUserByOpenidContainOpenidAndHash(openid);
    }

    /**
     * 根据微信id查询用户，包含id，weixinName，weixinPictureUrl字段
     * @param openid
     * @return
     */
    @Override
    public User findUserByOpenIdContainIdAndAppNameAndPicture(String openid) {
        return userMapper.findUserByOpenIdContainIdAndAppNameAndPicture(openid);
    }

    /**
     * 更新hash值
     * @param hash
     * @param openid
     */
    @Override
    public void updateHash(String hash, String openid) {
        userMapper.updateHash(hash ,openid);
    }


    /**
     * 根据openid查询WebSessionUser
     * @param openid
     * @return
     */
    @Override
    public WebSessionUser getWebSessionUserByOpneid(String openid) {
        User user = this.findUserByOpenIdContainIdAndAppNameAndPicture(openid);
        WebSessionUser webSessionUser = new WebSessionUser(user);
        return webSessionUser;
    }


    /**
     * 根据phoneNum查找用户是否存在
     * @param phoneNum
     * @return
     */
    @Override
    public Boolean isExistByPhoneNum(String phoneNum) {
        Long existByPhoneNum = userMapper.isExistByPhoneNum(phoneNum);
        if (Objects.equals(existByPhoneNum ,0L)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 根据phoneNum查询WebSessionUser
     * @param phoneNum
     * @return
     */
    @Override
    public WebSessionUser getWebSessionUserByPhone(String phoneNum) {
        User user = userMapper.findUserByPhoneNumContainIdAndAppNameAndPicture(phoneNum);
        WebSessionUser webSessionUser = new WebSessionUser(user);
        return webSessionUser;
    }

    /**
     * 根据用户id绑定手机号
     * @param userId
     * @param phoneNum
     */
    @Override
    public void updatePhoneByUserId(Long userId, String phoneNum) {
        userMapper.updatePhoneNumByUserId(userId ,phoneNum);
    }


    /**
     * 实名认证
     * @param authentication
     * @return
     */
    @Override
    public void realNameAuth(Long userId, Authentication authentication) {
        userMapper.realNameAuth(userId ,authentication);
    }
}
