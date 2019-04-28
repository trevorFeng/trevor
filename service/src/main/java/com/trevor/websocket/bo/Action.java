package com.trevor.web.websocket.bo;

public enum Action {
    /**
     * 进入房间
     */
    ENTER_ROOM(1 ,"进入房间"),

    /**
     * 准备
     */
    READY(2 ,"准备");

    qiangzhuang


    private Integer code;

    private String des;

    private Action(Integer code ,String des){
        this.code = code;
        this.des = des;
    }

    public Integer getCode() {
        return code;
    }

    public String getDes() {
        return des;
    }
}
