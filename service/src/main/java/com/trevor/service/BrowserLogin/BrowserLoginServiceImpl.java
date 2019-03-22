package com.trevor.service.BrowserLogin;

import com.trevor.bo.JsonEntity;
import com.trevor.dao.UserMapper;

import javax.annotation.Resource;

/**
 * @author trevor
 * @date 03/22/19 13:15
 */
public class BrowserLoginServiceImpl implements BrowserLoginService{

    @Resource
    private UserMapper userMapper;

    /**
     * 生成验证码发给用户
     * @param phoneNum
     * @return
     */
    @Override
    public JsonEntity<Object> generatePhoneCode(String phoneNum) {
        //检查手机号是否已经注册

        return null;
    }
}
