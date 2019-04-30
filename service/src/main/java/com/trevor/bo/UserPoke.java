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

    private Integer xianJiaMultiple = 1;
}
