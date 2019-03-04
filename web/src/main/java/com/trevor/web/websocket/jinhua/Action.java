package com.trevor.web.websocket.jinhua;

public enum Action {
    /**
     * 进入房间
     */
    ENTER_ROOM(1 ,"进入房间"),

    /**
     * 准备
     */
    READY(2 ,"准备"),

    /**
     * 看牌
     */
    LOOK_CARDS(3 ,"看牌"),

    /**
     * 丢牌
     */
    GIVE_UP(4 ,"丢牌"),

    /**
     * 比牌
     */
    COMPARISON_CARD(5 ,"比牌");


    private Integer code;

    private String des;

    private Action(Integer code ,String des){
        this.code = code;
        this.des = des;
    }
}
