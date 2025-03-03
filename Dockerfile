FROM ubuntu:latest
LABEL authors="alex"

#  添加标签,可以设置用户名
LABEL maintainer="alex <alex@qq.com>"

# 步骤2: 修改镜像源为阿里镜像源
RUN mkdir -p /etc/apt/ \
    && echo "deb http://mirrors.aliyun.com/ubuntu/ focal main restricted universe multiverse" > /etc/apt/sources.list \
    && echo "deb http://mirrors.aliyun.com/ubuntu/ focal-updates main restricted universe multiverse" >> /etc/apt/sources.list \
    && echo "deb http://mirrors.aliyun.com/ubuntu/ focal-backports main restricted universe multiverse" >> /etc/apt/sources.list \
    && echo "deb http://mirrors.aliyun.com/ubuntu/ focal-security main restricted universe multiverse" >> /etc/apt/sources.list \



# 步骤3: 修改时间为亚洲上海时间
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

    # 更新软件包列表
RUN apt update
# 步骤4: 安装中文语言包
RUN apt install -y language-pack-zh-hans \
    && locale-gen zh_CN.UTF-8 \
    && update-locale LANG=zh_CN.UTF-8

# 设置环境变量 ,这里必须设置，不然中文语言包不生效
ENV LANG=zh_CN.UTF-8

# 步骤5: 安装Java开发工具包（JDK）
RUN apt install -y openjdk-11-jdk \
    && apt install -y maven \
    && apt install -y vim

WORKDIR /app
COPY . /app

RUN cd /app && mvn  -B clean install -P${env} -Dmaven.test.skip=true -Dautoconfig.skip -pl ai-wechat-bot -am  && cp -r ai-wechat-bot/target /app

EXPOSE 9919
CMD ["java","-jar","/app/ai-wechat-bot.jar"]
