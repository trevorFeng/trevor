package com.trevor.service.niuniu.bo;

/**
 * @author trevor
 * @date 2019/3/13 14:50
 */
public enum  NiuniuAction {

    /**
     * 准备
     */
    READY(1 ,"准备"),

    /**
     * 发牌
     */
    FAPAI(2 ,"发牌");

    NiuniuAction(Integer code ,String desc){
        this.code = code;
        this.desc = desc;
    }

    private Integer code;

    private String desc;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }}
