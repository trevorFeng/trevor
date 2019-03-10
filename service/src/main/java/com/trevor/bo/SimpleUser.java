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
     * 名字
     */
    private String name;

    /**
     * 头像
     */
    private String picture;

    /**
     * 是否在线
     */
    private Integer isOnLine;

    /**
     * 是否在观战 是  否
     */
    private Boolean isGuanZhan;

    public SimpleUser(UserInfo userInfo ,String picture) {
        this.name = userInfo.getWeixinName();
        this.picture = picture;
        this.isOnLine = 1;
    }
}
