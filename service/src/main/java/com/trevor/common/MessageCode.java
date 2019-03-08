package com.trevor.common;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-08 0:24
 **/

public enum MessageCode {

    USER_ROOMCARD_NOT_ENOUGH(-1 ,"您的房卡数量不足"),

    CREATE_SUCCESS(1 ,"创建成功");

    private Integer code;

    private String message;

    MessageCode(Integer code ,String message){
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
