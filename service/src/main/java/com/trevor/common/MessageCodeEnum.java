package com.trevor.common;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-08 0:24
 **/

public enum MessageCodeEnum {

    /*****************************************               http返回消息                    *********************/

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
    RECEIVE_SUCCESS(2 ,"领取成功"),

    /**
     * 查询成功
     */
    QUERY_SUCCESS(3 ,"查询成功"),


    /*****************************************               websocket返回消息                    *********************/

    /**
     * 房间不存在
     */
    ROOM_NOT_EXIST(-101 ,"房间不存在"),

    /**
     * 房间已关闭
     */
    ROOM_CLOSE(-102 ,"房间已关闭"),

    /**
     * 房间人数已满
     */
    ROOM_FULL(-103 ,"房间人数已满"),

    /**
     * 您不是房主的好友
     */
    NOT_FRIEND(-104 ,"您不是房主的好友"),

    /**
     * 加入房间成功
     */
    JOIN_ROOM(101 ,"加入房间成功"),

    /**
     * 准备成功
     */
    READY_SUCCESS(102 ,"准备成功");


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
