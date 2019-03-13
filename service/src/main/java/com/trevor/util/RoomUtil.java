package com.trevor.util;

import com.trevor.bo.RoomPoke;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author trevor
 * @date 2019/3/13 17:38
 */
public class RoomUtil {

    /**
     * key为roomRecord的id，key为每一句的对局情况
     */
    public static final Map<Long , List<RoomPoke>> roomPokes = new ConcurrentHashMap<>(2<<15);


}
