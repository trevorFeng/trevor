package com.trevor.config;

import com.trevor.bo.RoomPoke;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

/**
 * @author trevor
 * @date 2019/3/8 16:56
 */
@Configuration
public class RoomConfig {

    /**
     * 注入牛牛的房间
     * @return
     */
    @Bean(name = "niuniuRooms")
    public Map<Long , CopyOnWriteArrayList<Session>> generateNiuNiuRoomMap(){
        Map<Long, CopyOnWriteArrayList<Session>> niuniuRooms = new ConcurrentHashMap(2<<15);
        return niuniuRooms;
    }

    /**
     * 注入牛牛的房间的牌
     * @return
     */
    @Bean(name = "niuniuRoomPoke")
    public Map<Long , RoomPoke> generateNiuNiuRoomPoke(){
        Map<Long , RoomPoke> map = new ConcurrentHashMap<>(2<<15);
        return map;
    }

    /**
     * 注入线程池
     * @return
     */
    @Bean(name = "executor")
    public Executor orderSolrSumExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(40);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("executor-");
        return executor;
    }
}