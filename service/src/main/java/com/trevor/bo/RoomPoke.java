package com.trevor.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
    private volatile List<UserPokesIndex> userPokes = new ArrayList<>(2<<4);

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
     * 每局房间的锁,对Set<Session>操作的锁
     */
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * 准备的人数
     */
    private Integer readyNum = 0;

    /**
     * 房间状态，0-在打牌中(可以参与本局打牌)，（不可以参与本局打牌）1-在等人打牌
     */
    private volatile Integer roomStatus;


}
