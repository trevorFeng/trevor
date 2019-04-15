package com.trevor.util;

import com.trevor.bo.WebKeys;
import com.trevor.domain.User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;

public class SessionUtil {

    /**
     * 获取session
     */
    public static HttpSession getSession() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest().getSession();
    }

    public static User getSessionUser() {
        User user = (User) getSession().getAttribute(WebKeys.SESSION_USER_KEY);
        return user;
    }

}
