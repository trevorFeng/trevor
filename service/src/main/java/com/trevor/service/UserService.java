package com.trevor.service;


import com.trevor.bo.WebSessionUser;

import javax.servlet.http.HttpServletRequest;

public interface UserService {

    /**
     * 根据cookie得到user
     * @param request
     * @return
     */
    WebSessionUser getUserByCookie(HttpServletRequest request);
}
