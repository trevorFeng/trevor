package com.trevor.websocket.bo;

import com.trevor.enums.MessageCodeEnum;
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
     * -1-错误,data为错误信息(string)
     * 0-自己加入房间的消息
     * 1-别人加入房间的消息
     * 2-用户准备
     * 3-倒计时
     * 4-发4张牌
     * 5-庄家确定
     * 6-再发一张牌
     * 7-得分结果和没有摊牌玩家的牌
     * 8-给别人发抢庄的消息
     * 9-给被人发闲家下注的消息
     * 10-摊牌消息
     *
     * 20-关闭浏览器自己重新进来的消息
     * 21-关闭浏览器别人重新进来的消息
     * 22-别的玩家掉线（关闭浏览器）的消息 ,data为玩家的id
     *
     */
    private Integer messageCode;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 消息
     */
    private T data;

    /**
     * 错误消息构造器
     * @param messageCodeEnum
     */
    public ReturnMessage (MessageCodeEnum messageCodeEnum){
        this.messageCode = messageCodeEnum.getCode();
        this.message = messageCodeEnum.getMessage();
    }

    /**
     * 正确消息构造器
     * @param t
     * @param messageCode
     */
    public ReturnMessage (T t ,Integer messageCode){
        this.data = t;
        this.messageCode = messageCode;
    }
}
