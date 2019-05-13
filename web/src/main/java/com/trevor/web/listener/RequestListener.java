package com.trevor.web.listener;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-05-13 21:27
 **/
@WebListener
public class RequestListener implements ServletRequestListener {
    public void requestInitialized(ServletRequestEvent sre)  {
        //将所有request请求都携带上httpSession
        ((HttpServletRequest) sre.getServletRequest()).getSession();

    }
    public RequestListener() {
        // TODO Auto-generated constructor stub
    }

    public void requestDestroyed(ServletRequestEvent arg0)  {
        // TODO Auto-generated method stub
    }
}
