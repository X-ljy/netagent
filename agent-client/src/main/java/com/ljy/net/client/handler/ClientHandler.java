package com.ljy.net.client.handler;

import com.ljy.net.client.agent.TcpConnection;
import com.ljy.net.client.exception.MsgException;
import com.ljy.net.client.protocol.AgentMessage;
import com.ljy.net.client.protocol.AgentMessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : 夕
 * @date : 2019/10/13
 */
public class ClientHandler extends CommonHandler {

    private int remotePort;
    private String password;
    private String proxyIp;
    private int proxyPort;

    private ConcurrentHashMap<String, CommonHandler> channelHandlerMap = new ConcurrentHashMap<>();
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public ClientHandler(int remotePort, String password, String proxyIp, int proxyPort) {
        this.remotePort = remotePort;
        this.password = password;
        this.proxyIp = proxyIp;
        this.proxyPort = proxyPort;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //注册客户端信息
        AgentMessage agentMessage = new AgentMessage();
        agentMessage.setType(AgentMessageType.REGISTER);
        HashMap<String, Object> metaData = new HashMap<>();
        metaData.put("port", remotePort);
        metaData.put("password", password);
        agentMessage.setMetaData(metaData);
        ctx.writeAndFlush(agentMessage);

        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        AgentMessage agentMessage = (AgentMessage) msg;
        if (agentMessage.getType() == AgentMessageType.REGISTER_RESULT) {
            processRegisterResult(agentMessage);
        } else if (agentMessage.getType() == AgentMessageType.CONNECTED) {
            processConnected(agentMessage);
        } else if (agentMessage.getType() == AgentMessageType.DISCONNECTED) {
            processDisconnected(agentMessage);
        } else if (agentMessage.getType() == AgentMessageType.DATA) {
            processData(agentMessage);
        } else if (agentMessage.getType() == AgentMessageType.KEEPALIVE) {
            // 心跳包, 不处理
        } else {
            throw new MsgException("Unknown type: " + agentMessage.getType());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channelGroup.close();
        System.out.println("Loss connection to Natx server, Please restart!");
    }

    /**
     * if agentMessage.getType() == AgentMessageType.REGISTER_RESULT
     */
    private void processRegisterResult(AgentMessage agentMessage) {
        if ((Boolean) agentMessage.getMetaData().get("success")) {
            System.out.println("Register to X-Server");
        } else {
            System.out.println("Register fail: " + agentMessage.getMetaData().get("reason"));
            ctx.close();
        }
    }

    /**
     * if agentMessage.getType() == AgentMessageType.CONNECTED
     */
    private void processConnected(AgentMessage agentMessage) throws Exception {

        try {
            ClientHandler thisHandler = this;
            TcpConnection localConnection = new TcpConnection();
            localConnection.connect(proxyIp, proxyPort, new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    LocalProxyHandler localProxyHandler = new LocalProxyHandler(thisHandler, agentMessage.getMetaData().get("channelId").toString());
                    ch.pipeline().addLast(
                            new ByteArrayDecoder(),
                            new ByteArrayEncoder(),
                            localProxyHandler
                    );
                    channelHandlerMap.put(agentMessage.getMetaData().get("channelId").toString(), localProxyHandler);
                    channelGroup.add(ch);
                }
            });
        } catch (Exception e) {
            AgentMessage message = new AgentMessage();
            message.setType(AgentMessageType.DISCONNECTED);
            HashMap<String, Object> metaData = new HashMap<>();
            metaData.put("channelId", message.getMetaData().get("channelId"));
            message.setMetaData(metaData);
            ctx.writeAndFlush(message);
            channelHandlerMap.remove(message.getMetaData().get("channelId"));
            throw e;
        }
    }

    /**
     * if agentMessage.getType() == AgentMessageType.DISCONNECTED
     */
    private void processDisconnected(AgentMessage agentMessage) {
        String channelId = agentMessage.getMetaData().get("channelId").toString();
        CommonHandler handler = channelHandlerMap.get(channelId);
        if (handler != null) {
            handler.getCtx().close();
            channelHandlerMap.remove(channelId);
        }
    }

    /**
     * if agentMessage.getType() == AgentMessageType.DATA
     */
    private void processData(AgentMessage natxMessage) {
        String channelId = natxMessage.getMetaData().get("channelId").toString();
        CommonHandler handler = channelHandlerMap.get(channelId);
        if (handler != null) {
            ChannelHandlerContext ctx = handler.getCtx();
            ctx.writeAndFlush(natxMessage.getData());
        }
    }
}
