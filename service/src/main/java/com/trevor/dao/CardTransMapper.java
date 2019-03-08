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

    /**
     * 根据版本号关闭交易
     * @param turnInTime
     * @param turnInUserId
     * @return 更新条数
     */
    Long closeTrans(Long turnInTime ,Long turnInUserId);
}
