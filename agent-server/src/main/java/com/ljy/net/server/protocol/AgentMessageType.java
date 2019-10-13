package com.ljy.net.server.protocol;

import com.ljy.net.server.exception.MsgException;

/**
 * @author : 夕
 * @date : 2019/10/12
 */

public enum  AgentMessageType {
    REGISTER(1),
    REGISTER_RESULT(2),
    CONNECTED(3),
    DISCONNECTED(4),
    DATA(5),
    KEEPALIVE(6);

    private int code;

    AgentMessageType(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }

    public static AgentMessageType valueOf(int code) throws MsgException {
        for (AgentMessageType item : AgentMessageType.values()) {
            if (item.code == code) {
                return item;
            }
        }
        throw new MsgException("NatxMessageType code error: " + code);
    }

}
