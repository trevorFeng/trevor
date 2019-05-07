package com.trevor.bo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author trevor
 * @date 2019/3/19 09:53
 */
@Data
public class UserPoke {

    /**
     * 玩家id
     */
    private Long userId;

    /**
     * 玩家本局的poke牌
     */
    private List<String> pokes = new ArrayList<>(2<<3);

    /**
     * 牌型
     */
    private String paiXing;

    /**
     * 本局的分数增减
     */
    private Integer thisScore = 0;

    /**
     * 抢庄倍数
     */
    private Integer qiangZhuangMultiple;

    /**
     * 是否抢庄
     */
    private Boolean isQiangZhuang = false;

    /**
     * 是否是庄家
     */
    private Boolean isZhuangJia = false;

    /**
     * 闲家下注的倍数，默认为1倍
     */
    private Integer xianJiaMultiple = 1;
}
