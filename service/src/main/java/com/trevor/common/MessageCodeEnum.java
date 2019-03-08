package com.trevor.common;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-08 0:24
 **/

public enum MessageCodeEnum {

    /**
     * 您的房卡数量不足
     */
    USER_ROOMCARD_NOT_ENOUGH(-1 ,"您的房卡数量不足"),

    /**
     * 交易已关闭
     */
    TRANS_CLOSE(-2 ,"交易已关闭"),

    /**
     * 创建成功
     */
    CREATE_SUCCESS(1 ,"创建成功"),

    /**
     * 领取成功
     */
    RECEIVE_SUCCESS(1 ,"领取成功");

    private Integer code;

    private String message;

    MessageCodeEnum(Integer code , String message){
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
