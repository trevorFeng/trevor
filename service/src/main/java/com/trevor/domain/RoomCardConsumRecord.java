package com.trevor.domain;

import lombok.Data;

/**
 * 一句话描述该类作用:【房卡消费记录】
 *
 * @author: trevor
 * @create: 2019-03-05 0:20
 **/
@Data
public class RoomCardConsumRecord {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 开房id
     */
    private Long roomRecordId;

    /**
     * 开房人的id（用户id）
     */
    private Long roomAuth;

    /**
     * 消费房卡数量
     */
    private Integer consumNum;

}
