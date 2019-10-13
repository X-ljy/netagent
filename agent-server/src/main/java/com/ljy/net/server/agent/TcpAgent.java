package com.ljy.net.server.agent;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author : 夕
 * @date : 2019/10/12
 */
public class TcpAgent {

    private Channel channel;

    public synchronized void bind(int port, ChannelInitializer channelInitializer) {
        final EventLoopGroup bossGroup = new NioEventLoopGroup();
        final EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            channel = serverBootstrap.bind(port).sync().channel();
            channel.closeFuture().addListener((ChannelFutureListener) future -> {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            });
        } catch (Exception e){
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            e.printStackTrace();
        }

    }

    public synchronized void close(){
        if(channel != null){
            channel.close();
        }
    }
}
