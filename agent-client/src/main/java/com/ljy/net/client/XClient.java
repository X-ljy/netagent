package com.ljy.net.client;

import com.ljy.net.client.agent.TcpConnection;
import com.ljy.net.client.codec.AgentMessageDecoder;
import com.ljy.net.client.codec.AgentMessageEncoder;
import com.ljy.net.client.handler.ClientHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.cli.*;

/**
 * @author : 夕
 * @date : 2019/10/13
 */
public class XClient {

    public static void main(String[] args) throws ParseException, InterruptedException {
        //解析命令行参数
        Options options = new Options();
        options.addOption("help",false,"Help");
        options.addOption("server_ip",true,"X-Server ip");
        options.addOption("server_port",true,"X-Server port");
        options.addOption("password", true, "X-Server password");
        options.addOption("proxy_ip",true,"proxy server ip");
        options.addOption("proxy_port",true,"proxy server port");
        options.addOption("remote_port",true,"proxy server remote port");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options,args);

        if(cmd.hasOption("help")){
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("options", options);
        }else {
            String serverIp = cmd.getOptionValue("server_ip");
            if (serverIp == null) {
                System.out.println("server_ip cannot be null");
                return;
            }
            String serverPort = cmd.getOptionValue("server_port");
            if (serverPort == null) {
                System.out.println("server_port cannot be null");
                return;
            }

            String password = cmd.getOptionValue("password");

            String proxyIp = cmd.getOptionValue("proxy_ip");
            if (proxyIp == null) {
                System.out.println("proxy_ip cannot be null");
                return;
            }
            String proxyPort = cmd.getOptionValue("proxy_port");
            if (proxyPort == null) {
                System.out.println("proxy_port cannot be null");
                return;
            }
            String remotePort = cmd.getOptionValue("remote_port");
            if (remotePort == null) {
                System.out.println("remote_port cannot be null");
                return;
            }

            TcpConnection tcpConnection = new TcpConnection();
            tcpConnection.connect(serverIp,Integer.parseInt(serverPort),new ChannelInitializer<SocketChannel>(){
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(
                            new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4),
                            new AgentMessageDecoder(),
                            new AgentMessageEncoder(),
                            new IdleStateHandler(60, 30, 0),
                            new ClientHandler(Integer.parseInt(remotePort), password,
                            proxyIp, Integer.parseInt(proxyPort))
                    );
                }
            });
        }
    }

}
