package com.trevor.service.domain;

import lombok.Data;

/**
 * 房卡交易记录
 * @author trevor
 * @date 2019/3/4 14:12
 */
@Data
public class RoomCardTrans {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 交易关闭的标志，1表示是，0表示否
     */
    private Integer closeFlag;

    /**
     * 转出玩家的微信号
     */
    private String turnOutweixinId;

    /**
     * 转出玩家的闲聊号
     */
    private String turnOutXianliaoId;

    /**
     * 转出玩家的手机号
     */
    private String turnOutPhoneNum;

    /**
     * 全局唯一的交易号
     */
    private Integer transNum;

    /**
     * 转出时间
     */
    private Long turnOutTime;

    /**
     * 转入玩家的微信号
     */
    private Integer turnInWinxinId;

    /**
     * 转入玩家的闲聊号
     */
    private String turnInXianliaoId;

    /**
     * 转入玩家的手机号
     */
    private String turnInPhoneNum;

    /**
     * 转入时间
     */
    private Long turnInTime;

    /**
     * 防止同时修改该条记录，房卡多次被领取的情况，每次修改版本号加1
     */
    private Integer version;
}
