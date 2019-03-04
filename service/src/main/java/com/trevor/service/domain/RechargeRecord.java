package com.trevor.service.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 一句话描述该类作用:【房卡充值记录】
 *
 * @author: trevor
 * @create: 2019-03-04 22:56
 **/
@Data
public class RechargeRecord {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 充值的微信号
     */
    private String rechargeWeixinId;

    /**
     * 充值的闲聊号
     */
    private String rechargeXianliaoId;

    /**
     * 充值的手机号
     */
    private String rechargePhoneNumId;

    /**
     * 充值房卡数量
     */
    private Integer rechargeRoomCard;

    /**
     * 房卡单价
     */
    private BigDecimal unitPrice;

    /**
     * 本次充值的总价
     */
    private BigDecimal totalPrice;

}
