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
     * 1-准备
     * 2-抢庄消息
     * 3-闲家下注
     * 4-摊牌的消息
     */
    private Integer messageCode;


    /**
     * 抢庄的倍数
     */
    private Integer qiangZhuangMultiple;

    /**
     * 闲家下注的倍数
     */
    private Integer xianJiaMultiple;

}
