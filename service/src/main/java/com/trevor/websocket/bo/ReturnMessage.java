package com.trevor.websocket.bo;

import com.trevor.common.MessageCodeEnum;
import lombok.Data;

/**
 * @Auther: trevor
 * @Date: 2019\4\17 0017 22:32
 * @Description:
 */
@Data
public class ReturnMessage<T> {

    /**
     * 消息类型,负数为错误
     * 0-session过期，需重新授权
     * 1-加入房间
     * 2-用户准备
     * 3-倒计时
     * 3-选择倍数
     * 4-发牌
     * 5-押倍数
     */
    private Integer messageCode;

    /**
     * 错误消息
     */
    private String errorMessage;

    /**
     * 消息
     */
    private T t;

    /**
     * 错误消息构造器
     * @param messageCodeEnum
     */
    public ReturnMessage (MessageCodeEnum messageCodeEnum){
        this.messageCode = messageCodeEnum.getCode();
        this.errorMessage = messageCodeEnum.getMessage();
    }

    /**
     * 正确消息构造器
     * @param t
     * @param messageCode
     */
    public ReturnMessage (T t ,Integer messageCode){
        this.t = t;
        this.messageCode = messageCode;
    }
}
