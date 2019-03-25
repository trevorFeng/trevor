package com.trevor.dao;


import com.trevor.domain.UserProposals;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 一句话描述该类作用:【】
 *
 * @author: trevor
 * @create: 2019-03-09 14:13
 **/
@Repository
public interface UserProposalsMapper {

    /**
     * 插入一条新纪录
     * @param userProposals
     */
    void insertOne(@Param("userProposals") UserProposals userProposals);
}
