package com.trevor.service.domain;

import lombok.Data;

/**
 * 开房记录表
 * @author trevor
 * @date 2019/3/4 14:24
 */
@Data
public class RoomRecord {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 房间编号
     */
    protected Integer roomNum;

    /**
     * 房间名字
     */
    private String roomName;

    /**
     * 开房人的id
     */
    private Integer roomAuth;

    /**
     * 消耗房卡数量
     */
    private Integer consumeRoomCardNum;

    /**
     * 开房时间
     */
    private Long getRoomTime;

    /**
     * 房间类型 1为牛牛，2为金花
     */
    private Integer roomType;

    /**
     * 房间属性配置，为json字符串
     */
    private String roomConfig;

}
