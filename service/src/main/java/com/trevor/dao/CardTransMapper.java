package com.trevor.dao;

import com.trevor.domain.CardTrans;
import org.springframework.stereotype.Repository;

/**
 * @author trevor
 * @date 2019/3/8 17:23
 */
@Repository
public interface CardTransMapper {

    /**
     * 插入一条记录
     * @param cardTrans
     */
    void insertOne(CardTrans cardTrans);

    /**
     * 根据交易号查询一条记录
     * @param transNo
     * @return
     */
    CardTrans findOneByTransNo(String transNo);

    void updateVersion(Long id ,Integer version);
}
