# 1. gewechat 

Gewechat 是基于[Gewechat](https://github.com/Devo919/Gewechat)项目实现的微信个人号通道，使用ipad协议登录，该协议能获取到wxid，能发送语音条消息，相比itchat协议更稳定。

api文档地址为：[gewechat api](https://apifox.com/apidoc/shared-69ba62ca-cb7d-437e-85e4-6f3d3df271b1/api-197179336)

首先可以简单了解 ai-wechat-bot、gewechat服务的调用关系，如下图所示

<div align="center">
<img width="700" src="/docs/gewechat/gewechat_service_design.png">
</div>


# 2. gewechat 服务部署教程

gewechat 服务需要自行部署，[ai-wechat-bot](https://github.com/alex-oos/ai-wechat-bot) 项目只负责对接gewechat服务，请参考下方教程部署gewechat服务。

## 2.1 下载镜像

```bash
# 从阿里云镜像仓库拉取(国内)
 docker pull registry.cn-hangzhou.aliyuncs.com/gewe/gewe:latest
 
 docker tag registry.cn-hangzhou.aliyuncs.com/gewe/gewe gewe
```

## 2.2 使用docker启动

```bash
mkdir -p gewechat/data  
docker run -itd -v gewechat/data:/root/temp -p 2531:2531 -p 2532:2532 --privileged=true --name=gewe gewe /usr/sbin/init
#设置开机自启
docker update --restart=always gewe
```

## 2.3 使用docker compose启动

首先创建必要的数据目录:

```bash
mkdir -p gewechat/data
```

创建 `docker-compose.yml` 文件:

```yaml
version: '3'
services:
  gewechat:
    image: gewe
    container_name: gewe
    volumes:
      - ./gewechat/data:/root/temp
    ports:
      - "2531:2531"
      - "2532:2532"
    restart: always
```

运行:
```bash
docker compose up -d
```

## 2.4 成功日志

看到如下日志，表示gewechat服务启动成功

<div align="center">
<img width="700" src="./docs/gewechat/gewechat_service_success.jpg">
</div>

# 3. 使用ai-wechat-bot对接gewechat服务

## 3.1 gewechat相关参数配置

在config.json中需要配置以下gewechat相关的参数：

```bash
{
    "token": "",        # gewechat服务的token，用于接口认证
    "appId": "",       # gewechat服务的应用ID
    "baseUrl": "http://本机ip:2531/v2/api",  # gewechat服务的API基础URL
    "callbackUrl": "http://本机ip:9919/v2/api/callback/collect", # 回调URL，用于接收消息
    "downloadUrl": "http://本机ip:2532/download", # 文件下载URL
}
```

参数说明：
- `token`: gewechat服务的认证token，首次登录时，可以留空，启动ai-wechat-bot服务时，会**自动获取token**并**自动保存到config.json**中
- `appId`: gewechat服务分配的设备ID，首次登录时，可以留空，启动ai-wechat-bot服务时，会**自动获取appid**并**自动保存到config.json**中
- `baseUrl`: gewechat服务的API基础地址，请根据实际情况配置，如果gewechat服务与ai-wechat-bot服务部署在同一台机器上，可以配置为`http://本机ip:2531/v2/api`
- `callbackUrl`: 接收gewechat消息的回调地址，请根据实际情况配置，如果gewechat服务与ai-wechat-bot服务部署在同一台机器上，可以配置为`http://本机ip:9919/v2/api/callback/collect`，如无特殊需要，请使用9919端口号
- `downloadUrl`: 文件下载地址，用于下载语音、图片等文件，请根据实际部署情况配置，如果gewechat服务与ai-wechat-bot服务部署在同一台机器上，可以配置为`http://本机ip:2532/download`

注意：请确保您的回调地址(callback_url)，即ai-wechat-bot启动的回调服务可以被gewechat服务正常访问到。如果您使用Docker部署，需要注意网络配置，确保容器之间可以正常通信。

## 3.2 ai-wechat-bot相关参数配置

在config.json中需要配置以下

```bash
{
  "model": "ali",                              # 模型名称设置为ali
  "singleChatPrefix": [""],                   # 私聊触发前缀
  "singleChatReplyPrefix": "",               # 私聊回复前缀
  "groupChatPrefix": ["@bot"],                # 群聊触发前缀
  "groupNameWhiteList": ["ALL_GROUP"],       # 允许响应的群组
}
```


## 3.3 启动ai-wechat-bot服务

完成上述配置后，你需要确保gewechat服务已正常启动

```bash
mvn run ai-wechat-bot.jar
```
启动成功后，可以看到如下日志信息，注意token和appid会自动保存到config.json，无需手动保存

<div align="center">
<img width="700" src="/docs/gewechat/gewechat_login.jpg">
</div>

[//]: # (## 3.4 利用gewechat发送语音条消息)

[//]: # ()
[//]: # (语音相关配置如下，另外需要在应用中开启语音转文字以及文字转语音功能)

[//]: # ()
[//]: # (```bash)

[//]: # ({)

[//]: # (  "model": "qwen-plus",    )

[//]: # (  "speech_recognition": true,  # 是否开启语音识别)

[//]: # (  "voice_reply_voice": true,   # 是否使用语音回复语音)

[//]: # (  "always_reply_voice": false, # 是否一直使用语音回复)

[//]: # (  "voice_to_text": "ai",     # 语音识别引擎)

[//]: # (  "text_to_voice": "ai"      # 语音合成引擎)

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (gewechat支持**发送语音条消息**，但是gewechat服务只能获取到**20s**以内的语音，所以**你只能给bot发送20s以内的语音**，而**bot给你发送语音时无此限制**。)

[//]: # ()
[//]: # (<div align="center">)

[//]: # (<img width="700" src="/docs/gewechat/gewechat_voice.jpg">)

[//]: # (</div>)


# 4. gewechat_channel 服务的限制
1. gewechat 要求必须搭建服务到**同省**服务器或者电脑里方可正常使用，即登录微信的手机与gewechat服务必须在同一省
2. gewechat 开源框架**只支持**下载接收到的图片，不支持下载文件
3. gewechat_channel 目前暂时**只支持接收文字消息**，**只支持发送文字消息与图片消息**，后续支持的消息类型会逐步完善
4. 此项目仅用于个人娱乐场景，请**勿用于任何商业场景**
