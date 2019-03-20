package com.trevor.web.filter;

import com.trevor.bo.WebKeys;
import com.trevor.dao.UserMapper;
import com.trevor.domain.User;
import com.trevor.util.CookieUtils;
import com.trevor.util.TokenUtil;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * 一句话描述该类作用:【全局过滤器】
 *
 * @author: trevor
 * @create: 2019-03-03 0:07
 **/
@WebFilter(filterName = "userFilter" , urlPatterns = "/*")
public class LoginFilter implements Filter{

    @Resource
    private UserMapper userMapper;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        //从什么页面进来
        String reUrl = request.getRequestURI();
        String token = CookieUtils.fine(request, WebKeys.TOKEN);
        // 取得客户端浏览器的类型
        String browserType = request.getHeader("user-agent").toLowerCase();
        //token不存在,则要求登录
        if (token == null && !judeLoginPath(reUrl)) {
            //根据浏览器类型去不同的登陆页面
            goToLoginPage(response ,browserType ,reUrl);
            return;
        }
        try {
            //解析token
            Map<String, Object> claims = TokenUtil.getClaimsFromToken(token);
            String openid = (String) claims.get("openid");
            String hash = (String) claims.get("hash");
            String timestamp = String.valueOf(claims.get("timestamp"));
            //三者必须存在,少一样说明token被篡改
            if (openid == null || hash == null || timestamp == null) {
                goToLoginPage(response ,browserType ,reUrl);
                return;
            }
            //三者合法才通过
            if(!(checkOpenidAndHash(openid,hash) && checkTimeStamp(timestamp))){
                goToLoginPage(response ,browserType ,reUrl);
                return;
            }
            //刷新token时间
            claims.put("timestamp" ,System.currentTimeMillis());
            CookieUtils.add(WebKeys.TOKEN ,TokenUtil.generateToken(claims) ,response);
            filterChain.doFilter(servletRequest,servletResponse);
        } catch (Exception e) {
            //token非法
            goToLoginPage(response ,"error" ,reUrl);
            return;
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
        if (uri.startsWith(WebKeys.WEIXIN_AUTH_PAGE_PATH)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 检查token是否过期
     * 开发时:指定1分钟,可以更好的看到效果
     * @param timestamp
     * @return
     */
    private boolean checkTimeStamp(String timestamp) {
        // 有效期: 30分钟,单位: ms
        long expires_in = 30 * 1000 * 60;
        long timestamp_long = Long.parseLong(timestamp);
        //两者相差的时间,单位(ms)
        long time = new Date().getTime() - timestamp_long;
        if(time > expires_in){
            //过期
            return false;
        }else {
            return true;
        }
    }

    /**
     * 判断opendid,hash是否合法
     * true合法
     * false不合法
     * @param openid
     * @return
     */
    private Boolean checkOpenidAndHash(String openid,String hash){
        User user = userMapper.findUserOpenidAndHash(openid);
        if(user.getWeixinId() != null){
            //对比
            if(openid.equals(user.getWeixinId()) && hash.equals(user.getHash())){
                return true;
            }
        }
        return false;
    }
}
