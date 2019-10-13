package com.ljy.net.server.handler;

import com.ljy.net.server.agent.TcpAgent;
import com.ljy.net.server.exception.MsgException;
import com.ljy.net.server.protocol.AgentMessage;
import com.ljy.net.server.protocol.AgentMessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.HashMap;

/**
 * @author : 夕
 * @date : 2019/10/12
 */
public class ServerHandler extends CommonHandler{

    private TcpAgent remoteConnectionServer = new TcpAgent();

    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private int port;

    private String password;

    private boolean register = false;

    public ServerHandler(String password){
        this.password = password;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        AgentMessage agentMessage = (AgentMessage) msg;

        if(agentMessage.getType() == AgentMessageType.REGISTER){
            processRegister(agentMessage);
        }else if(register){
            if (agentMessage.getType() == AgentMessageType.DISCONNECTED) {
                processDisconnected(agentMessage);
            } else if (agentMessage.getType() == AgentMessageType.DATA) {
                processData(agentMessage);
            } else if (agentMessage.getType() == AgentMessageType.KEEPALIVE) {
                // 心跳包, 不处理
            } else {
                throw new MsgException("Unknown type: " + agentMessage.getType());
            }
        }else {
            ctx.close();
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        remoteConnectionServer.close();
        if (register) {
            System.out.println("Stop server on port: " + port);
        }
    }


    /**
     * if agentMessage.getType() == agentMessage.REGISTER
     */
    private void processRegister(AgentMessage agentMessage) {
        HashMap<String, Object> metaData = new HashMap<>();

        String password = agentMessage.getMetaData().get("password").toString();
        if (this.password != null && !this.password.equals(password)) {
            metaData.put("success", false);
            metaData.put("reason", "Token is wrong");
        } else {
            int port = (int) agentMessage.getMetaData().get("port");

            try {

                ServerHandler thisHandler = this;
                remoteConnectionServer.bind(port, new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new ByteArrayDecoder(),
                                new ByteArrayEncoder(),
                                new RemoteProxyHandler(thisHandler));
                        channels.add(ch);
                    }
                });

                metaData.put("success", true);
                this.port = port;
                register = true;
                System.out.println("Register success, start server on port: " + port);
            } catch (Exception e) {
                metaData.put("success", false);
                metaData.put("reason", e.getMessage());
                e.printStackTrace();
            }
        }

        AgentMessage sendBackMessage = new AgentMessage();
        sendBackMessage.setType(AgentMessageType.REGISTER_RESULT);
        sendBackMessage.setMetaData(metaData);
        ctx.writeAndFlush(sendBackMessage);
        if (!register) {
            System.out.println("Client register error: " + metaData.get("reason"));
            ctx.close();
        }
    }

    /**
     * if agentMessage.getType() == AgentMessageType.DATA
     */
    private void processData(AgentMessage agentMessage) {
        channels.writeAndFlush(agentMessage.getData(), channel -> channel.id().asLongText().equals(agentMessage.getMetaData().get("channelId")));
    }

    /**
     * if agentMessage.getType() == AgentMessageType.DISCONNECTED
     * @param agentMessage
     */
    private void processDisconnected(AgentMessage agentMessage) {
        channels.close(channel -> channel.id().asLongText().equals(agentMessage.getMetaData().get("channelId")));
    }

}
