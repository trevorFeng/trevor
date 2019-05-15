package com.trevor.domain;

import lombok.Data;

import java.sql.Blob;

/**
 * @author trevor
 * @date 05/14/19 18:18
 */
@Data
public class RoomPokeInit {

    private Long id;

    private Long roomRecordId;

    /**
     * 是否激活,0为未激活,1为激活，2为房间使用完成后关闭，3为房间未使用关闭
     */
    private Integer status;

    private Long entryDate;

    private Blob roomPoke;
}
