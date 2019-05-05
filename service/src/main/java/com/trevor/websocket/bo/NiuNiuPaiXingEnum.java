package com.trevor.websocket.bo;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-05-05 22:21
 **/

public enum NiuNiuPaiXingEnum {

    NIU_10(10 ,"牛牛"),

    NIU_9(9 ,"牛九"),

    NIU_8(8 ,"牛八"),

    NIU_7(7 ,"牛七"),

    NIU_6(6 ,"牛六"),

    NIU_5(5 ,"牛五"),

    NIU_4(4 ,"牛四"),

    NIU_3(3 ,"牛三"),

    NIU_2(2 ,"牛二"),

    NIU_1(1 ,"牛一");

    Integer paiXingCode;

    String desc;

    NiuNiuPaiXingEnum(Integer paiXingCode , String desc){
        this.paiXingCode = paiXingCode;
        this.desc = desc;
    }
}
