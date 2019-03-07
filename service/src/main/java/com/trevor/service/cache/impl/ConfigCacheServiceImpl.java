package com.trevor.service.cache.impl;

import com.trevor.domain.Config;
import com.trevor.service.cache.ConfigCacheService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-06 22:27
 **/
@Service
public class ConfigCacheServiceImpl implements ConfigCacheService {

    @Override
    public List<Config> getConfigsByConfigName(String configName) {
        return null;
    }
}
