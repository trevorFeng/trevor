package com.trevor.service.cache;

import com.trevor.domain.Config;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-06 22:27
 **/
@Service
public class CacheServiceImpl implements CacheService{

    @Override
    public List<Config> getConfigsByConfigName(String configName) {
        return null;
    }
}
