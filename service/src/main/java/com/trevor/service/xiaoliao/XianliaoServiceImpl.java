package com.trevor.service.xiaoliao;

import com.trevor.bo.JsonEntity;
import com.trevor.dao.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * @author trevor
 * @date 03/21/19 18:24
 */
@Service
@Slf4j
public class XianliaoServiceImpl implements XianliaoService{

    @Resource
    private UserMapper userMapper;

    /**
     * 根据code获取闲聊用户基本信息
     * @return
     */
    @Override
    public JsonEntity<Map<String, Object>> weixinAuth(String code) throws IOException {
        return null;
    }
}
