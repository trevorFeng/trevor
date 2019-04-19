package com.trevor.web.websocket.bo;

import lombok.Data;

/**
 * @Auther: trevor
 * @Date: 2019\4\17 0017 22:32
 * @Description:
 */
@Data
public class ReturnMessage {

    /**
     * 消息类型
     * 1-打牌用户新增
     * 2-用户准备
     * 3-选择倍数
     * 4-发牌
     * 5-押倍数
     */
    private Integer messageType;

    /**
     * 消息
     */
    private String message;
}
