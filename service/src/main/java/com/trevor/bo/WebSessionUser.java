package com.trevor.bo;

import com.trevor.domain.User;
import lombok.Data;

/**
 * 一句话描述该类作用:【session 中存储得用户】
 *
 * @author: trevor
 * @create: 2019-03-03 21:53
 **/
@Data
public class WebSessionUser {

    /**
     * 用户id
     */
    private Long id;

    /**
     * 用户名字
     */
    private String name;

    /**
     * 用户头像
     */
    private String pictureUrl;

}
