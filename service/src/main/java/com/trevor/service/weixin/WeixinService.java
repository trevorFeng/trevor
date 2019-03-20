package com.trevor.service.weixin;

import com.trevor.bo.JsonEntity;

import java.io.IOException;
import java.util.Map;

/**
 * @author trevor
 * @date 2019/3/4 11:37
 */
public interface WeixinService {

    /**
     * 根据code获取微信用户基本信息
     * @return
     */
    JsonEntity<Map<String, Object>> weixinAuth(String code) throws IOException;
}
