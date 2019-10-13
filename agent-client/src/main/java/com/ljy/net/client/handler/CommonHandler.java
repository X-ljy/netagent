package com.ljy.net.client.handler;

import com.ljy.net.client.protocol.AgentMessage;
import com.ljy.net.client.protocol.AgentMessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author : 夕
 * @date : 2019/10/12
 */
public class CommonHandler extends ChannelInboundHandlerAdapter {

    protected ChannelHandlerContext ctx;

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("--- Exception caugth ---");
        cause.printStackTrace();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                System.out.println("Read idle loss connection.");
                ctx.close();
            } else if (event.state() == IdleState.WRITER_IDLE) {
                AgentMessage agentMessage = new AgentMessage();
                agentMessage.setType(AgentMessageType.KEEPALIVE);
                ctx.writeAndFlush(agentMessage);
            }
        }
    }
}
