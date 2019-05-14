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

    private Blob roomPoke;
}
