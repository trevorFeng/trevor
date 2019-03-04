package com.trevor.domain;

import lombok.Data;

/**
 * @author trevor
 * @date 2019/3/4 14:24
 */
@Data
public class GetRoomRecord {

    private Long id;

    protected Integer roomNum;


    private String roomName;

    /**
     * 开房时间
     */
    private Long time;
}
