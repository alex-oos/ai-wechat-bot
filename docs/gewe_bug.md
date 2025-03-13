# 遇见的一些坑点： 

## 一、gewechat 容器启动异常
执行命令：
```shell
docker run -itd -v ./gewechat/data:/root/temp -p 2531:2531 -p 2532:2532 --restart=always --name=gewe gewe
```

定位思考：查看容器日志
```shell
docker logs  gewe 
Failed to mount tmpfs at /run: Operation not permitted
[!!!!!!] Failed to mount API filesystems, freezing.
```
解决方案：
```shell
docker run --privileged -itd -v ./gewechat/data:/root/temp -p 2531:2531 -p 2532:2532 --restart=always --name=gewe gewe
```
## 二、gewechat 容器启动之后，发现2352端口没有启动

定位思考：查看容器日志
```text
docker logs  gewe
FAILED] Failed to start The nginx HTTP and reverse proxy server.
See 'systemctl status nginx.service' for details.
[  OK  ] Started Process Monitoring and Control Daemon.
[  OK  ] Started MySQL Server.
[  OK  ] Reached target Multi-User System.
         Starting Update UTMP about System Runlevel Changes...
[  OK  ] Started Stop Read-Ahead Data Collection 10s After Completed Startup.
[  OK  ] Started Update UTMP about System Runlevel Changes.
```
进入到gewechat容器中，查看nginx服务是否启动
```shell
docker exec -it gewe /bin/bash
```
执行命令
```shell
systemctl status nginx.service
```
提示如下：
```shell
ystemctl status nginx.service
● nginx.service - The nginx HTTP and reverse proxy server
   Loaded: loaded (/usr/lib/systemd/system/nginx.service; enabled; vendor preset: disabled)
   Active: failed (Result: exit-code) since Thu 2025-03-13 09:14:33 UTC; 2h 7min ago
  Process: 99 ExecStartPre=/usr/sbin/nginx -t (code=exited, status=1/FAILURE)
  Process: 96 ExecStartPre=/usr/bin/rm -f /run/nginx.pid (code=exited, status=0/SUCCESS)

Mar 13 09:14:33 283ede18f977 systemd[1]: Starting The nginx HTTP and reverse proxy server...
Mar 13 09:14:33 283ede18f977 nginx[99]: nginx: the configuration file /etc/nginx/nginx.conf syntax is ok
Mar 13 09:14:33 283ede18f977 nginx[99]: nginx: [emerg] socket() [::]:80 failed (97: Address family not supported by protocol)
Mar 13 09:14:33 283ede18f977 nginx[99]: nginx: configuration file /etc/nginx/nginx.conf test failed
Mar 13 09:14:33 283ede18f977 systemd[1]: nginx.service: control process exited, code=exited status=1
Mar 13 09:14:33 283ede18f977 systemd[1]: Failed to start The nginx HTTP and reverse proxy server.
Mar 13 09:14:33 283ede18f977 systemd[1]: Unit nginx.service entered failed state.
Mar 13 09:14:33 283ede18f977 systemd[1]: nginx.service failed.
```

错误信息提示：
```text
nginx: [emerg] socket() [::]:80 failed (97: Address family not supported by protocol)
```
解决方案：
```shell
#打开 Nginx 配置文件：
sudo nano /etc/nginx/nginx.conf
#找到nginx的配置
listen 80;
listen [::]:80;
# 删除下面这个即可
listen [::]:80;
```
然后再重启nginx服务
```shell
systemctl restart nginx
```
