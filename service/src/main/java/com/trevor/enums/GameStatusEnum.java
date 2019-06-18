package com.trevor.enums;

public enum GameStatusEnum {

    /**
     * 可以准备
     */
    BEFORE_READY(1 ,"准备的倒计时前"),

    BEFORE_FAPAI_4(2 ,"发4张牌前"),

    BEFORE_QIANGZHUANG_COUNTDOWN(3 ,"抢庄倒计时前"),

    BEFORE_SELECT_ZHUANGJIA(4 ,"确定庄家前"),

    BEFORE_XIANJIA_XIAZHU(5 ,"闲家下注倒计时前"),

    BEFORE_LAST_POKE(6 ,"再发一张牌前"),

    BEFORE_TABPAI_COUNTDOWN(7 ,"摊牌倒计时前"),

    BEFORE_CALRESULT(8,"给玩家发返回结果前");




    private Integer code ;

    private String desc;

    GameStatusEnum(Integer code , String desc){
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
