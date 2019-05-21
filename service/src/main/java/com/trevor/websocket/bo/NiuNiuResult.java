package com.trevor.websocket.bo;

import lombok.Data;

import java.util.List;

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
     * 是否已经摊牌
     */
    private Boolean isTanPai;

    /**
     * 玩家的牌
     */
    private List<String> pokes;

    /**
     * 牌型
     */
    private Integer paiXing;

    /**
     * 总分
     */
    private Integer total;

    /**
     * 本局分数增减
     */
    private Integer score;
}
