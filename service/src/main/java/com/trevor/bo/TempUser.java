package com.trevor.bo;

import lombok.Data;

/**
 * @author trevor
 * @date 03/20/19 16:22
 */
@Data
public class TempUser {

    private String isAuth;

    private String token;

    public TempUser(String isAuth, String token) {
        this.isAuth = isAuth;
        this.token = token;
    }
}
