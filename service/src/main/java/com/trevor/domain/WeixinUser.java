package com.trevor.domain;


import lombok.Data;

/**
 * 一句话描述该类作用:【玩家信息】
 *
 * @author: trevor
 * @create: 2019-03-03 23:14
 **/
@Data
public class WeixinUser {

    /**
     * id
     */
    private Long id;

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
     * 是否开启好友管理，1为是，0为否
     */
    private Integer friendManageFlag;
}
