package com.ljy.net.client.agent;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author : 夕
 * @date : 2019/10/13
 */
public class TcpConnection {

    private Channel channel;

    public void connect(String host, int port, ChannelInitializer channelInitializer) throws InterruptedException {
        final NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap
                    .group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(channelInitializer);
            channel = bootstrap.connect(host,port).sync().channel();
            channel.closeFuture().addListener((ChannelFutureListener)future -> workerGroup.shutdownGracefully());
        } catch (Exception e) {
            workerGroup.shutdownGracefully();
            throw e;
        }
    }

}
