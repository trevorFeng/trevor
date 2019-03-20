package com.trevor.bo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author trevor
 * @date 03/20/19 18:17
 */
@Data
@ToString
public class WeixinAuthUser {

    private String openid;

    private String nickname;

    private Integer sex;

    private String province;

    private String city;

    private String country;

    private String headimgurl;

    private List<String> privilege;

    private String unionid;

}
