package com.trevor.bo;

import lombok.Data;

/**
 * @author trevor
 * @date 2019/3/4 12:36
 */
@Data
public class WeixinToken {

    private String access_token;

    private String expires_in;

    private String refresh_token;

    private String penid;

    private String scope;

    private String unionid;
}
