package com.trevor.bo;

import lombok.Data;

/**
 * 一句话描述该类作用:【session 中存储得用户】
 *
 * @author: trevor
 * @create: 2019-03-03 21:53
 **/
@Data
public class UserInfo {

    private Long id;

    private String weixinId;

    private String weixinName;

    private String xianliaoId;

    private String xianliaoName;


}
