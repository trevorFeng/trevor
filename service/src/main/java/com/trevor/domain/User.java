package com.trevor.domain;

import lombok.Data;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-03 23:14
 **/
@Data
public class User {

    private Long id;

    private String weixinId;

    private String xianliaoId;

    private String phoneNumber;

    private Integer roomCardNumber;

}
