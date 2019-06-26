package com.trevor.websocket.bo;

import java.util.List;

public class RetuenReadyMessage {

    /**
     * 总局数
     */
    private Integer totalJuShu;

    /**
     * 目前在第几局
     */
    private Integer runingNum;

    /**
     * 玩家信息
     */
    private List<SocketUser> socketUserList;
}
