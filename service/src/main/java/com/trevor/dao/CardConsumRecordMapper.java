package com.trevor.dao;

import com.trevor.domain.CardConsumRecord;
import org.springframework.stereotype.Repository;

/**
 * @author trevor
 * @date 2019/3/8 16:23
 */
@Repository
public interface CardConsumRecordMapper {

    /**
     * 插入一条记录并返回主键
     * @param cardConsumRecord
     * @return
     */
    Long insertOne(CardConsumRecord cardConsumRecord);
}
