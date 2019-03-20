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
     * 微信名字
     */
    private String weixinId;

    /**
     * 微信号
     */
    private String weixinName;

    /**
     * 用户微信头像
     */
    private String weixinPicture;

    /**
     * 闲聊名字
     */
    private String xianliaoName;

    /**
     * 闲聊号
     */
    private String xianliaoId;

    /**
     * 闲聊头像
     */
    private String xianliaoPicture;

    /**
     * 本表中关联的userId，实则为同一用户
     */
    private Long userId;

    /**
     * hash值
     */
    private String hash;

    /**
     * 是否开启好友管理，1为是，0为否
     */
    private Integer friendManageFlag;

}
