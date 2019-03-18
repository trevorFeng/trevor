package com.trevor.domain;

import lombok.Data;

/**
 * 一句话描述该类作用:【玩家信息】
 *
 * @author: trevor
 * @create: 2019-03-03 23:14
 **/
@Data
public class XianliaoUser {

    /**
     * id
     */
    private Long id;

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
     * 是否开启好友管理，1为是，0为否
     */
    private Integer friendManageFlag;
}
