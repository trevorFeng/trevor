package com.trevor.service.BrowserLogin;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.WebSessionUser;
import com.trevor.domain.User;

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
    JsonEntity<String> generatePhoneCode(String phoneNum);

    /**
     * 查询用户
     * @param phoneNum
     * @return
     */
    JsonEntity<User> getUserHashAndOpenidByPhoneNum(String phoneNum);
}
