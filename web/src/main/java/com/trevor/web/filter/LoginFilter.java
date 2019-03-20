package com.trevor.web.filter;

import com.trevor.bo.UserInfo;
import com.trevor.bo.WebKeys;
import com.trevor.util.CookieUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 一句话描述该类作用:【全局过滤器】
 *
 * @author: trevor
 * @create: 2019-03-03 0:07
 **/
@WebFilter(filterName = "userFilter" , urlPatterns = "/*")
public class LoginFilter implements Filter{

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        //从什么页面进来
        String reUrl = request.getRequestURI();
        String token = CookieUtils.fine(request, WebKeys.SESSION_USER_KEY);
        //token不存在,则要求登录
        if (token == null && !judeLoginPath(reUrl)) {
            // 取得客户端浏览器的类型
            String browserType = request.getHeader("user-agent").toLowerCase();
            //根据浏览器类型去不同的登陆页面
            goToLoginPage(response ,browserType ,reUrl);
        }else {
            filterChain.doFilter(servletRequest ,servletResponse);
        }

    }

    /**
     * 根据浏览器类型去不同的登陆页面
     * @param response
     * @param browserType
     */
    private void goToLoginPage(HttpServletResponse response ,String browserType ,String uri) throws IOException {
        // 是微信浏览器
        if (browserType.indexOf(WebKeys.WEIXIN_BROWSER) > 0) {
            response.sendRedirect(WebKeys.WEIXIN_LOGIN_PAGE_PATH + "?reUrl=" + uri);
        }
        // 是闲聊浏览器
        else if (browserType.indexOf(WebKeys.XIANLIAO_BROWSER) >0) {
            response.sendRedirect(WebKeys.XIANLIAO_LOGIN_PAGE_PATH + "?reUrl=" + uri);
        }
        // 是安卓浏览器
        else if (browserType.indexOf(WebKeys.ANZHUO_BROWSER) >0) {
            response.sendRedirect(WebKeys.SHOUJI_LOGIN_PAGE_PATH + "?reUrl=" + uri);
        }
        // 是苹果浏览器
        else if (browserType.indexOf(WebKeys.IPONE_BROWSER) >0) {
            response.sendRedirect(WebKeys.SHOUJI_LOGIN_PAGE_PATH + "?reUrl=" + uri);
        }
        // 其他去错误页面
        else {
            response.sendRedirect(WebKeys.ERROR_LOGIN_PAGE_PATH);
        }
    }

    /**
     * 根据uri判断是否是登陆页面
     * @param uri
     * @return
     */
    private Boolean judeLoginPath(String uri){
        if (uri.startsWith(WebKeys.WEIXIN_LOGIN_PAGE_PATH)) {
            return Boolean.TRUE;
        }
        if (uri.startsWith(WebKeys.XIANLIAO_LOGIN_PAGE_PATH)) {
            return Boolean.TRUE;
        }
        if (uri.startsWith(WebKeys.SHOUJI_LOGIN_PAGE_PATH)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
