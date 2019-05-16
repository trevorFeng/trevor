package com.trevor.bo;

import com.trevor.bo.UserPoke;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author trevor
 * @date 2019/3/13 17:41
 */
@Data
public class RoomPoke implements Serializable {

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
    private List<UserPokesIndex> userPokes = new ArrayList<>(2<<4);

    /**
     * 玩家分数情况
     */
    private List<UserScore> userScores = new ArrayList<>(2<<4);

    /**
     * 默认为0，开到第几局了
     */
    private Integer runingNum = 0;

    /**
     * 总局数
     */
    private Integer totalNum;

    /**
     * 是否准备完毕
     */
    private volatile Boolean isReadyOver = false;

    /**
     * 每局房间的锁
     */
    private Lock lock = new ReentrantLock();

    /**
     * 准备的人数
     */
    private Integer readyNum = 0;


}
