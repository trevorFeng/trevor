package com.trevor.service.user;

import com.trevor.bo.WebKeys;
import com.trevor.bo.WebSessionUser;
import com.trevor.dao.UserMapper;
import com.trevor.domain.User;
import com.trevor.util.CookieUtils;
import com.trevor.util.TokenUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
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
     * 根据cookie得到user
     * @param request
     * @return
     */
    @Override
    public WebSessionUser getUserByCookie(HttpServletRequest request) {
        String token = CookieUtils.fine(request, WebKeys.TOKEN);
        //解析token
        Map<String, Object> claims = TokenUtil.getClaimsFromToken(token);
        String openid = (String) claims.get("openid");
        User user = userMapper.findUserByOpenId(openid);
        WebSessionUser webSessionUser = new WebSessionUser(user);
        return webSessionUser;
    }

    /**
     * 新增一个用户
     * @param user
     */
    @Override
    public void insertOne(User user) {
        userMapper.insertOne(user);
    }
}
