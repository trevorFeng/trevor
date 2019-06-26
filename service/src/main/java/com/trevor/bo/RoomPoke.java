package com.trevor.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
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
    private Long roomId;

    /**
     * 每一局的玩家的牌
     */
    private volatile List<UserPokesIndex> userPokes = new ArrayList<>(2<<4);

    /**
     * 真正打牌的人的集合，不管是不是已经关闭浏览器重新进来的人
     */
    private volatile List<RealWanJiaInfo> realWanJias = new ArrayList<>(2<<4);

    /**
     * 玩家分数情况
     */
    private List<UserScore> userScores = new ArrayList<>(2<<4);

    private volatile Integer readyNum;

    /**
     * 默认为0，开到第几局了
     */
    private volatile Integer runingNum = 0;

    /**
     * 总局数
     */
    private Integer totalNum;

    /**
     * 对Set<Session>操作的锁
     */
    private ReadWriteLock leaderLock = new ReentrantReadWriteLock();

    private Lock leaderReadLock = leaderLock.readLock();

    private Lock leaderWriteLock = leaderLock.writeLock();

    /**
     * 对realWanJias的锁
     */
    private Lock realWanJiaLock = new ReentrantLock();

    /**
     * 对gameStatus的锁
     */
    private ReadWriteLock gameStatusLock = new ReentrantReadWriteLock();

    private Lock gameStatusReadLock = gameStatusLock.readLock();

    private Lock gameStatusWriteLock = gameStatusLock.writeLock();

    /**
     * 游戏状态
     */
    private volatile Integer gameStatus;

    /**
     * 0-表示可以开始打牌，1-表示在进行中，不能准备，2-表示房间已经结束
     */
    private volatile Integer processFlag;


}
