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
     * 真实名字
     */
    private String realName;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 唯一的openid
     */
    private String openid;

    /**
     * hash值
     */
    private String hash;

    /**
     * 本表中自关联的userId，实则为同一用户（微信账号和闲聊账号）
     */
    private Long userId;

    /**
     * 电话号码
     */
    private String phoneNumber;

    /**
     * 微信名字
     */
    private String weixinName;

    /**
     * 用户微信头像
     */
    private String weixinPictureUrl;

    /**
     * 闲聊名字
     */
    private String xianliaoName;

    /**
     * 闲聊头像
     */
    private String xianliaoPictureUrl;

    /**
     * 是否开启好友管理，1为是，0为否
     */
    private Integer friendManageFlag;

}
