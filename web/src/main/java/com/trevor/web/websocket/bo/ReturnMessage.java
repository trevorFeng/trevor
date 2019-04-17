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
     * 消息类型，1为加入新的聊天用户，2为聊天内容，3为图片
     */
    private Integer messageType;

    /**
     * 是否是自己的聊天内容，1为是，0为否
     */
    private Integer isMyself;

    /**
     * 消息
     */
    private String message;
}
