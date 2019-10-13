# netagent  

netagent是一个内网穿透工具，本项目是基于netty实现，可以实现任意网络客户端的内网穿透。

## 使用说明

1. 在具有公网IP的服务器上运行agent-server，可选参数选项如下：

    usage: options  
     -help     &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;      Help  
     -password \<arg>  &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;  X-Server password（默认：123456）  
     -port \<arg>      &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;  X-Server port（默认：1024）  

 
**示例：**

```sh
java -jar agent-server.jar -port 1024 -password 123456 
```

2. 在任意内网主机，或者自己的电脑本地运行agent-client，可选参数如下：  

usage: options  
 -help     &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;    Help  
 -password \<arg>  &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;    X-Server password（对应的agent-server 的密码）  
 -server_ip \<arg>  &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;   X-Server ip （对应的agent-server IP地址）  
 -server_port \<arg>   &ensp;&ensp;&ensp;&ensp;&ensp;  X-Server port （对应的agent-server 端口）  
 -remote_port \<arg>  &ensp;&ensp;&ensp;&ensp;  proxy server remote port（本地需要进行穿透的服务在agent-server映射的端口）  
 -proxy_ip \<arg>     &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;  proxy server ip （本地需要进行穿透的服务的IP地址）  
 -proxy_port \<arg>    &ensp;&ensp;&ensp;&ensp;&ensp;&ensp;  proxy server port （本地需要进行穿透的服务的端口）  
 
**示例：**

```sh
java -jar agent-client.jar -server_ip x.x.x.x -server_port 1024 -password 123456 -proxy_ip 127.0.0.1 -proxy_port 8080 -remote_port 8080
```
