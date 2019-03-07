package com.trevor.web.websocket.niuniu.config;

import com.trevor.service.niuniu.NiuniuService;
import com.trevor.web.websocket.niuniu.NiuniuServer;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author trevor
 * @date 2019/3/7 15:06
 */
@Configuration
public class NiuniuConfig {

    @Resource
    public void setNiuniuService(NiuniuService niuniuService){
        NiuniuServer.setNiuniuService(niuniuService);
    }
}
