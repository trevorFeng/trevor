package com.trevor.web.controller.weixin;

import com.trevor.bo.JsonEntity;
import com.trevor.bo.SimpleUser;
import com.trevor.service.WeixinService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author trevorl
 * @date 2019/3/4 10:49
 */
@RestController
public class WeixinController {

    @Resource
    private WeixinService weixinService;

    @RequestMapping(value = "public/api/weixin/user/{code}", method = {RequestMethod.GET},
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<SimpleUser> queryWeixinUser(@PathVariable("code") String code) throws IOException {
        return weixinService.queryWeixinUser(code);
    }


}
