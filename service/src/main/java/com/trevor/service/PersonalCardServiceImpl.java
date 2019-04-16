package com.trevor.service;

import com.trevor.dao.PersonalCardMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Auther: trevor
 * @Date: 2019\4\16 0016 23:16
 * @Description:
 */
@Service
public class PersonalCardServiceImpl implements PersonalCardService{

    @Resource
    private PersonalCardMapper personalCardMapper;

    /**
     * 根据玩家查询玩家拥有的房卡数量
     * @param userId
     * @return
     */
    @Override
    public Integer findCardNumByUserId(Long userId) {
        return personalCardMapper.findCardNumByUserId(userId);
    }
}
