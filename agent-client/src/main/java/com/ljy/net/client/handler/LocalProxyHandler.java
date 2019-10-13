package com.ljy.net.client.handler;

import com.ljy.net.client.protocol.AgentMessage;
import com.ljy.net.client.protocol.AgentMessageType;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;

/**
 * @author : 夕
 * @date : 2019/10/13
 */
public class LocalProxyHandler extends CommonHandler{

    private CommonHandler proxyHandler;
    private String remoteChannelId;

    public LocalProxyHandler(CommonHandler proxyHandler,String remoteChannelId){
        this.proxyHandler = proxyHandler;
        this.remoteChannelId = remoteChannelId;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] data = (byte[]) msg;
        AgentMessage agentMessage = new AgentMessage();
        agentMessage.setType(AgentMessageType.DATA);
        agentMessage.setData(data);
        HashMap<String,Object> metaData = new HashMap<>();
        metaData.put("channelId",remoteChannelId);
        agentMessage.setMetaData(metaData);
        proxyHandler.getCtx().writeAndFlush(agentMessage);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        AgentMessage agentMessage = new AgentMessage();
        agentMessage.setType(AgentMessageType.DISCONNECTED);
        HashMap<String,Object> metaData = new HashMap<>();
        metaData.put("channelId",remoteChannelId);
        agentMessage.setMetaData(metaData);
        proxyHandler.getCtx().writeAndFlush(agentMessage);
    }
}
