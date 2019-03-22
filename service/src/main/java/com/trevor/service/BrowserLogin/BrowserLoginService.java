package com.trevor.service.BrowserLogin;

import com.trevor.bo.JsonEntity;

/**
 * @author trevor
 * @date 03/22/19 13:13
 */
public interface BrowserLoginService {

    /**
     * 生成验证码发给用户
     * @param phoneNum
     * @return
     */
    JsonEntity<Object> generatePhoneCode(String phoneNum);
}
