package com.trevor.domain;

import lombok.Data;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-05 23:33
 **/
@Data
public class Config {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 配置名字
     */
    private String configName;

    /**
     * 配置的值
     */
    private String configValue;

    /**
     * 配置描述
     */
    private String configDesc;

    /**
     * configValue 1
     */
    private String varchar1;

    /**
     * configValue 2
     */
    private String varchar2;

    /**
     * configValue 3
     */
    private String varchar3;

    /**
     * configValue 4
     */
    private String varchar4;

    /**
     * configValue 5
     */
    private String varchar5;

    /**
     * configValue 6
     */
    private String varchar6;

    /**
     * configValue 7
     */
    private String varchar7;

    /**
     * configValue 8
     */
    private String varchar8;

    /**
     * configValue 9
     */
    private String varchar9;

    /**
     * configValue 10
     */
    private String varchar10;

    /**
     * Y代表可用，N代表不可用
     */
    private String active;

}
