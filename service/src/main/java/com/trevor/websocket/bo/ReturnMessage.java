package com.trevor.web.websocket.bo;

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
     * 1-打牌用户新增
     * 2-用户准备
     * 3-选择倍数
     * 4-发牌
     * 5-押倍数
     */
    private Integer messageType;

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
        this.messageType = messageCodeEnum.getCode();
        this.errorMessage = messageCodeEnum.getMessage();
    }

    /**
     * 正确消息构造器
     * @param t
     * @param messageType
     */
    public ReturnMessage (T t ,Integer messageType){
        this.t = t;
        this.messageType = messageType;
    }
}
