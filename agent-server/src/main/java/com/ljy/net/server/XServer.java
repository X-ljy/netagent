package com.ljy.net.server;

import com.ljy.net.server.agent.TcpAgent;
import com.ljy.net.server.codec.AgentMessageDecoder;
import com.ljy.net.server.codec.AgentMessageEncoder;
import com.ljy.net.server.handler.ServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.cli.*;

/**
 * @author : 夕
 * @date : 2019/10/12
 */
public class XServer {

    public static void main(String[] args) throws ParseException {

        //解析命令行参数
        Options options = new Options();
        options.addOption("help", false, "Help");
        options.addOption("port", true, "X-Server port");
        options.addOption("password", true, "X-Server password");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        if (cmd.hasOption("help")) {
            //输出提示信息
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("options", options);
        } else {
            //获取命令行参数，并设置默认值
            int port = Integer.parseInt(cmd.getOptionValue("port", "1024"));
            String password = cmd.getOptionValue("password", "123456");

            TcpAgent tcpAgentServer = new TcpAgent();
            tcpAgentServer.bind(port, new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(
                            new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
                            new AgentMessageDecoder(),
                            new AgentMessageEncoder(),
                            new IdleStateHandler(60, 30, 0),
                            new ServerHandler(password)
                    );
                }
            });
            System.out.println("X-Server started on port " + port);
        }




    }

}
