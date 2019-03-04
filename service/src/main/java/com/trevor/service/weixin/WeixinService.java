package com.trevor.service.weixin;

import com.trevor.service.bo.JsonEntity;
import com.trevor.service.bo.SimpleUser;

import java.io.IOException;

/**
 * @author trevor
 * @date 2019/3/4 11:37
 */
public interface WeixinService {

    /**
     * 根据code获取微信用户基本信息
     * @return
     */
    JsonEntity<SimpleUser> queryWeixinUser(String code) throws IOException;
}
