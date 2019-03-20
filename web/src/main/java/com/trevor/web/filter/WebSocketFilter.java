package com.trevor.web.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 一句话描述该类作用:【主要任务是用ServletRequest将我们的HttpSession携带过去】
 *
 * @author: trevor
 * @create: 2019-03-07 0:07
 **/
@WebFilter(filterName = "wsFilter", urlPatterns = "/websocket/*")
public class WebSocketFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ((HttpServletRequest) (servletRequest)).getSession();
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}
