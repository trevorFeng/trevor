package com.trevor.bo;

import lombok.Data;

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
    private List<String> pokes;

    /**
     * 本局是否准备
     */
    private Boolean isReady;

    /**
     * 本局的分数增减
     */
    private Integer thisScore;

    /**
     * 倍数
     */
    private Integer multiple;
}
