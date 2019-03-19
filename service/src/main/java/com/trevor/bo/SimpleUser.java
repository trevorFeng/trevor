package com.trevor.bo;

import lombok.Data;

/**
 * 一句话描述该类作用:【返回给前端得用户信息】
 * @author trevor
 * @date 2019/3/4 11:28
 */
@Data
public class SimpleUser {

    /**
     * id
     */
    private Long id;

    /**
     * 名字
     */
    private String name;

    /**
     * 头像
     */
    private String picture;

    /**
     * 是否准备
     */
    private Boolean isReady;

    /**
     * 是否在线
     */
    private Boolean isOnLine;

    /**
     * 是否在观战 是  否
     */
    private Boolean isGuanZhan;

    /**
     * 是否是吃瓜群众
     */
    private Boolean isChiGuaPeople;

    public SimpleUser(UserInfo userInfo) {
        this.id = userInfo.getId();
        this.name = userInfo.getName();
        this.picture = userInfo.getPicture();
        this.isOnLine = Boolean.TRUE;
        this.isReady = Boolean.FALSE;
    }
}
