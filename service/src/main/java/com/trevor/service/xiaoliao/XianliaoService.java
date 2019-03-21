package com.trevor.service.xiaoliao;

import com.trevor.bo.JsonEntity;

import java.io.IOException;
import java.util.Map;

/**
 * @author trevor
 * @date 03/21/19 18:22
 */
public interface XianliaoService {

    /**
     * 根据code获取闲聊用户基本信息
     * @return
     */
    JsonEntity<Map<String, Object>> weixinAuth(String code) throws IOException;
}
