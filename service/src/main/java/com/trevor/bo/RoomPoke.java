package com.trevor.bo;

import com.trevor.websocket.bo.SocketUser;
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
     * 真正打牌的人的集合，不管是不是已经关闭浏览器重新进来的人
     */
    private volatile List<RealWanJiaInfo> realWanJias = new ArrayList<>(2<<4);

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
     * 游戏进程
     * 0 ---- 进入房间-准备倒计时前
     * 1 ---- 准备倒计时开始-准备倒计时结束
     * 2 ----
     */
    private Integer gameProcess = 0;

    /**
     * 对Set<Session>操作的锁
     */
    private ReadWriteLock sesionsLock = new ReentrantReadWriteLock();

    /**
     * 游戏进程的锁，对游戏哪个状态进行加锁
     */
    private Lock gameProcessLock = new ReentrantLock();

    /**
     * 对gameStatus的锁
     */
    private Lock gameStatusLock = new ReentrantLock();

    /**
     * 准备的人数
     */
    private volatile Integer readyNum = 0;

    /**
     * 游戏状态
     */
    private volatile Integer gameStatus;


}
