package com.trevor.bo;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * @author trevor
 * @date 2019/3/13 17:41
 */
@Data
public class RoomPoke {

    /**
     * 房间id
     */
    private Long roomRecordId;

    /**
     * poke牌
     */
    private List<String> pokes;

    /**
     * 每一局的玩家的牌
     */
    private List<Map<Long ,UserPoke>> userPokes = new ArrayList<>(2<<4);

    /**
     * 玩家玩完上一局后的分数
     */
    private Map<Long ,Integer> scoreMap = new HashMap<>(2<<4);

    /**
     * 当前准备玩家人数，每重开一局清零
     */
    private Integer userReadyNum;

    /**
     * 每局房间的锁
     */
    private Lock lock;

    /**
     * 是否准备完毕
     */
    private volatile Boolean isReadyOver;

    /**
     * 默认为0，开到第几局了
     */
    private Integer runingNum = 0;

    /**
     * 总局数
     */
    private Integer totalNum;


}
