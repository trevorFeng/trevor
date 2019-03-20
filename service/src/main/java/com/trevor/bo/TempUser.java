package com.trevor.bo;

import lombok.Data;

/**
 * @author trevor
 * @date 03/20/19 16:22
 */
@Data
public class TempUser {

    /**
     * 1代表已授权，0代表未授权
     */
    private String isAuth;

    /**
     * token信息
     */
    private String token;

    /**
     * openid
     */
    private String openid;

    public TempUser(String isAuth, String token ,String openid) {
        this.isAuth = isAuth;
        this.token = token;
        this.openid = openid;
    }
}
