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
    Boolean isExistByOpnenId(@Param("openid") String openid);

    /**
     * 根据cookie得到user
     * @param request
     * @return
     */
    WebSessionUser getUserByCookie(HttpServletRequest request);

    /**
     * 新增一个用户
     * @param user
     */
    void insertOne(User user);
}
