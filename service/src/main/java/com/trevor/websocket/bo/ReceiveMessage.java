package com.trevor.websocket.bo;

import lombok.Data;

/**
 * @author trevor
 * @date 04/22/19 15:52
 */
@Data
public class ReceiveMessage {

    /**
     * 消息类型
     * 2-准备
     * 3-抢庄消息
     * 4-闲家下注
     */
    private Integer messageCode;

    /**
     * 消息
     */
    private String message;
}
