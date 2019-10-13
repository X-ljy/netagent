package com.ljy.net.server.codec;

import com.ljy.net.server.protocol.AgentMessage;
import com.ljy.net.server.protocol.AgentMessageType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author : 夕
 * @date : 2019/10/12
 */
public class AgentMessageDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List out) throws Exception {
        int type = byteBuf.readInt();
        AgentMessageType agentMessageType = AgentMessageType.valueOf(type);

        int metaDataLength = byteBuf.readInt();
        CharSequence metaDataString = byteBuf.readCharSequence(metaDataLength, CharsetUtil.UTF_8);
        JSONObject jsonObject = new JSONObject(metaDataString.toString());
        Map<String, Object> metaData = jsonObject.toMap();

        byte[] data = null;
        if (byteBuf.isReadable()) {
            data = ByteBufUtil.getBytes(byteBuf);
        }

        AgentMessage agentMessage = new AgentMessage();
        agentMessage.setType(agentMessageType);
        agentMessage.setMetaData(metaData);
        agentMessage.setData(data);

        out.add(agentMessage);

    }
}
