package com.trevor.bo;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-03 22:16
 **/

public class WebKeys {

    /**
     * session 中用户的key
     */
    public static final String SESSION_USER_KEY = "sessionUser";

    public static final String TOKEN = "token";

    /**
     * 微信浏览器标识
     */
    public static final String WEIXIN_BROWSER = "micromessenger";

    /**
     * 闲聊浏览器标识
     */
    public static final String XIANLIAO_BROWSER = "micromessenger";

    /**
     * 安卓浏览器标识
     */
    public static final String ANZHUO_BROWSER = "micromessenger";


    /**
     * 苹果浏览器标识
     */
    public static final String IPONE_BROWSER = "micromessenger";

    /**
     * 微信授权
     */
    public static final String WEIXIN_AUTH_PAGE_PATH = "/api/weixin/auth";

    /**
     * 微信登陆
     */
    public static final String WEIXIN_LOGIN_PAGE_PATH = "/front/weixin/login";

    /**
     * 闲聊登陆页面
     */
    public static final String XIANLIAO_LOGIN_PAGE_PATH = "/front/xianliao/login";

    /**
     * 手机浏览器登陆页面
     */
    public static final String SHOUJI_LOGIN_PAGE_PATH = "/front/phone/login";

    /**
     * 错误登陆页面，浏览器类型不满足
     */
    public static final String ERROR_LOGIN_PAGE_PATH = "/view/login/shoujiLogin.html";

    /**
     * appId
     */
    public static final String APPID = "";

    /**
     * APP_SECRET
     */
    public static final String APP_SECRET = "";

    /**
     * grant_type
     */
    public static final String GRANT_TYPE = "authorization_code";

    /**
     * code
     */
    public static final String CODE = "code";

    public static final String UUID = "uuid";

    /**
     * refresh_token
     */
    public static final String REFRESH_TOKEN = "refresh_token";


    /**
     * access_token
     */
    public static final String ACCESS_TOKEN = "access_token";

    /**
     * openid
     */
    public static final String OPEN_ID = "openid";

    /**
     * errcode
     */
    public static final String ERRCODE = "errcode";

    /**
     * errmsg
     */
    public static final String ERRMSG = "errmsg";




}
