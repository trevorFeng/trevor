package com.trevor.websocket.bo;

import lombok.Data;

import java.util.List;

/**
 * 一句话描述该类作用:【返回给前端得用户信息】
 * @author trevor
 * @date 2019/3/4 11:28
 */
@Data
public class SocketUser {

    /**
     * id
     */
    private Long id;

    /**
     * 名字
     */
    private String name;

    /**
     * 是否是自己的信息
     */
    private Boolean isMyself;

    /**
     * 头像
     */
    private String picture;

    /**
     * 是否是观众，可以参与打牌
     */
    private Boolean isGuanZhong;

    /**
     * 是否是吃瓜群众，不可以参与打牌，只能看
     */
    private Boolean isChiGuaPeople;

    /**
     * 游戏状态
     */
    private Integer status;

    /**
     * 是否是第一次进入的玩家
     */
    private Boolean isNewUser;

    /**
     * 分数
     */
    private Integer score;

    /**
     * 是否准备
     */
    private Boolean isReady;

    /**
     * 是否是庄家
     */
    private Boolean isZhuangJia;

    /**
     * 玩家的牌
     */
    private List<String> pokes;

    /**
     * 是否离线
     */
    private Boolean isUnconnection;
}

