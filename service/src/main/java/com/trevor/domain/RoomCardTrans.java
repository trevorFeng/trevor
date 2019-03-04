package com.trevor.domain;

import lombok.Data;

/**
 * @author trevor
 * @date 2019/3/4 14:12
 */
@Data
public class RoomCardTrans {

    private Integer id;

    /**
     * 交易关闭的标志，1表示是，0表示否
     */
    private Integer closeFlag;

    /**
     * 转出的微信号
     */
    private String turnOutweixinId;

    private String turnOutXianliaoId;

    private String turnOutPhoneNum;

    private Integer transNum;

    /**
     * 转出时间
     */
    private Long turnOutTime;

    /**
     * 转入的微信号
     */
    private Integer turnInWinxinId;

    private String turnInXianliaoId;

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
