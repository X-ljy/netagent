package com.ljy.net.server.codec;

import com.ljy.net.server.protocol.AgentMessage;
import com.ljy.net.server.protocol.AgentMessageType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

/**
 * @author : 夕
 * @date : 2019/10/12
 */
public class AgentMessageEncoder extends MessageToByteEncoder<AgentMessage> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, AgentMessage agentMessage, ByteBuf out) throws Exception {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {

            AgentMessageType agentMessageType = agentMessage.getType();
            dataOutputStream.writeInt(agentMessageType.getCode());

            JSONObject metaDataJson = new JSONObject(agentMessage.getMetaData());
            byte[] metaDataBytes = metaDataJson.toString().getBytes(CharsetUtil.UTF_8);
            dataOutputStream.writeInt(metaDataBytes.length);
            dataOutputStream.write(metaDataBytes);

            if (agentMessage.getData() != null && agentMessage.getData().length > 0) {
                dataOutputStream.write(agentMessage.getData());
            }

            byte[] data = byteArrayOutputStream.toByteArray();
            out.writeInt(data.length);
            out.writeBytes(data);
        }

    }
}
