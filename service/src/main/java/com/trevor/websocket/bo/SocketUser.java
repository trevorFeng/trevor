package com.trevor.websocket.bo;

import lombok.Data;

/**
 * 一句话描述该类作用:【返回给前端得用户信息】
 * @author trevor
 * @date 2019/3/4 11:28
 */
@Data
public class SocketUser {

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
     * 是否是观众，可以参与打牌
     */
    private Boolean isGuanZhong;

    /**
     * 是否是吃瓜群众，不可以参与打牌，只能看
     */
    private Boolean isChiGuaPeople;
}
