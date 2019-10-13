package com.ljy.net.server.protocol;

import java.util.Map;

/**
 * @author : 夕
 * @date : 2019/10/12
 */
public class AgentMessage {

    private AgentMessageType type;
    private Map<String,Object> metaData;
    private byte[] data;

    public AgentMessageType getType() {
        return type;
    }

    public void setType(AgentMessageType type) {
        this.type = type;
    }

    public Map<String, Object> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, Object> metaData) {
        this.metaData = metaData;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
