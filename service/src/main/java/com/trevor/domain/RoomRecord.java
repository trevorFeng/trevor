package com.trevor.domain;

import com.alibaba.fastjson.JSON;
import com.trevor.service.createRoom.bo.NiuniuRoomParameter;
import lombok.Data;

/**
 * 开房记录表(开房基本信息)
 * @author trevor
 * @date 2019/3/4 14:24
 */
@Data
public class RoomRecord {

    /**
     * 主键id,房间编号,从10000开始
     */
    private Long id;

    /**
     * 开房时间
     */
    private Long getRoomTime;

    /**
     * 开房人的id（用户id）
     */
    private Long roomAuth;

    /**
     * 房间状态，1为在未进行，2为进行中，3-
     */
    private Integer state;

    /**
     * 房间类型 1为13人牛牛，2为10人牛牛，3为6人牛牛 ，4为金花
     */
    private Integer roomType;

    /**
     * 房间属性配置，为json字符串
     */
    private String roomConfig;

    /**
     * 生成一个房间的基本信息
     * @param roomType
     * @return
     */
    public void generateRoomRecordBase(Integer roomType , NiuniuRoomParameter niuniuRoomParameter ,Long roomAuth){
        this.setRoomType(roomType);
        this.setRoomAuth(roomAuth);
        this.setGetRoomTime(System.currentTimeMillis());
        this.setRoomConfig(JSON.toJSONString(niuniuRoomParameter));
    }

}
