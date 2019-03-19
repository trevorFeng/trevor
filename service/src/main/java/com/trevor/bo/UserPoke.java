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
}
