package com.trevor.bo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
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
    private List<List<UserPoke>> userPokes = new ArrayList<>(2<<4);

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
     *
     */
    private Integer totalNum;


}
