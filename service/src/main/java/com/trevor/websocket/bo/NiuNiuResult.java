package com.trevor.websocket.bo;

import lombok.Data;

/**
 * @author trevor
 * @date 05/07/19 18:39
 */
@Data
public class NiuNiuResult {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 总分
     */
    private Integer total;

    /**
     * 本局分数增减
     */
    private Integer score;
}
