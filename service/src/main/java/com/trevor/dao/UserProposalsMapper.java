package com.trevor.dao;

import com.trevor.domain.UserProposals;
import org.apache.ibatis.annotations.Param;

public interface UserProposalsMapper {

    /**
     * 新增一条记录
     * @param userProposals
     */
    void insertOne(@Param("userProposals") UserProposals userProposals);
}
