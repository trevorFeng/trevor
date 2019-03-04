package com.trevor.service.domain;

import lombok.Data;

/**
 * 一句话描述该类作用:【个人对局情况】
 *
 * @author: trevor
 * @create: 2019-03-04 23:21
 **/
@Data
public class PersonalConfrontation {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 玩家的id
     */
    private Integer userId;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 积分情况
     */
    private Integer integralCondition;
}
