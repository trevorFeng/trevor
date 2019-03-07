package com.trevor.common;

import java.util.Objects;

public enum RoomType {

    /**
     * 13人牛牛
     */
    NIU_NIU_13(1 ,"13人牛牛");

    /**
     * 房间类型
     */
    private Integer roomType;

    /**
     * 房间描述
     */
    private String roomDesc;

    RoomType(Integer roomType ,String roomDesc){
        this.roomType = roomType;
        this.roomDesc = roomDesc;
    }

    public RoomType getRoomType(Integer roomType){
        if (Objects.equals(roomType ,1)) {
            return NIU_NIU_13;
        }
        return null;
    }


}
