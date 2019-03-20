package com.trevor.service;

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

@Service
public class UserServiceImpl implements UserService{

    @Resource
    private UserMapper userMapper;

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
        User user = userMapper.findUserByWeiXinId(openid);
        WebSessionUser webSessionUser = new WebSessionUser(user);
        return webSessionUser;
    }
}
