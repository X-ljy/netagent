package com.ljy.net.server.handler;

import com.ljy.net.server.protocol.AgentMessage;
import com.ljy.net.server.protocol.AgentMessageType;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;

/**
 * @author : 夕
 * @date : 2019/10/12
 */
public class RemoteProxyHandler extends CommonHandler {

    private CommonHandler proxyHandler;

    public RemoteProxyHandler(CommonHandler commonHandler) {
        this.proxyHandler = commonHandler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        AgentMessage agentMessage = new AgentMessage();
        agentMessage.setType(AgentMessageType.CONNECTED);
        HashMap<String, Object> metaData = new HashMap<>();
        metaData.put("channelId", ctx.channel().id().asLongText());
        agentMessage.setMetaData(metaData);
        proxyHandler.getCtx().writeAndFlush(agentMessage);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        AgentMessage agentMessage = new AgentMessage();
        agentMessage.setType(AgentMessageType.DISCONNECTED);
        HashMap<String, Object> metaData = new HashMap<>();
        metaData.put("channelId", ctx.channel().id().asLongText());
        agentMessage.setMetaData(metaData);
        proxyHandler.getCtx().writeAndFlush(agentMessage);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] data = (byte[]) msg;
        AgentMessage agentMessage = new AgentMessage();
        agentMessage.setType(AgentMessageType.DATA);
        agentMessage.setData(data);
        HashMap<String, Object> metaData = new HashMap<>();
        metaData.put("channelId", ctx.channel().id().asLongText());
        agentMessage.setMetaData(metaData);
        proxyHandler.getCtx().writeAndFlush(agentMessage);
    }
}
