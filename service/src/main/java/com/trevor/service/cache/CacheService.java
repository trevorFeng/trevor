package com.trevor.service.cache;

import com.trevor.domain.Config;

import java.util.List;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-06 22:26
 **/

public interface CacheService {
    List<Config> getConfigsByConfigName(String configName);
}
