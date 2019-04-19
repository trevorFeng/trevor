package com.trevor.web.websocket.bo;

import lombok.Data;

/**
 * @Auther: trevor
 * @Date: 2019\4\17 0017 22:30
 * @Description:
 */
@Data
public class ReceiveMessage {

    /**
     *
     */
    private Integer messageType;

    private String message;
}
