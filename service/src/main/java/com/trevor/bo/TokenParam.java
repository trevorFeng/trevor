package com.trevor.bo;

import lombok.Data;

/**
 * @author trevor
 * @date 03/20/19 18:09
 */
@Data
public class TokenParam {

    private String openid;

    private String hash;

    private Long time;
}
