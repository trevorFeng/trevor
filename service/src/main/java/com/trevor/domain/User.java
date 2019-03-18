package com.trevor.domain;

import lombok.Data;

/**
 * 一句话描述该类作用:【玩家信息】
 *
 * @author: trevor
 * @create: 2019-03-03 23:14
 **/
@Data
public class User {

    /**
     * id
     */
    private Long id;

    /**
     * 名字
     */
    private String name;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 电话号码
     */
    private String phoneNumber;


    /**
     * 是否开启好友管理，1为是，0为否
     */
    private Integer friendManageFlag;

}
