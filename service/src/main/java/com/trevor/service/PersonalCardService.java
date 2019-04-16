package com.trevor.service;

import org.apache.ibatis.annotations.Param;

/**
 * @Auther: trevor
 * @Date: 2019\4\16 0016 23:15
 * @Description:
 */
public interface PersonalCardService {

    /**
     * 根据玩家查询玩家拥有的房卡数量
     * @param userId
     * @return
     */
    Integer findCardNumByUserId(Long userId);
}
