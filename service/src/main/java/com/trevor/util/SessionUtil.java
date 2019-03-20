package com.trevor.util;

import com.trevor.bo.WebSessionUser;
import com.trevor.bo.WebKeys;
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

    public static WebSessionUser getSessionUser() {
        WebSessionUser userInSession = (WebSessionUser) getSession().getAttribute(WebKeys.SESSION_USER_KEY);
        return userInSession;
    }

}
