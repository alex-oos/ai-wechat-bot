#!/bin/bash

# 定义项目路径和JAR文件名
#PROJECT_PATH="/path/to/your/project"
JAR_FILE_NAME="ai-wechat-bot.jar"

# 进入项目目录
#cd $PROJECT_PATH || exit 1
# 检查并终止已存在的进程
echo "检查是否存在相同名称的进程..."
PID=$(pgrep -f "$JAR_FILE_NAME")

if [ -n "$PID" ]; then
    echo "找到已存在的进程，PID: $PID，正在终止..."
    kill -9 $PID
    if [ $? -eq 0 ]; then
        echo "进程已成功终止。"
    else
        echo "终止进程失败。"
        exit 1
    fi
else
    echo "未找到已存在的进程。"
fi
# 执行mvn package命令
echo "开始执行mvn package..."
mvn clean package

# 检查mvn package是否成功
if [ $? -ne 0 ]; then
    echo "mvn package失败，脚本退出。"
    exit 1
fi

# 找到目标JAR文件
JAR_PATH=$(find target -name "$JAR_FILE_NAME")

# 检查JAR文件是否存在
if [ ! -f "$JAR_PATH" ]; then
    echo "未找到JAR文件：$JAR_FILE_NAME"
    exit 1
fi

# 在后台启动JAR文件
echo "启动JAR文件：$JAR_PATH"
nohup java -jar "$JAR_PATH" > app.log 2>&1 &

# 输出日志文件路径
#echo "日志文件路径：$PROJECT_PATH/app.log"
echo "日志文件路径：$(pwd)/app.log"
# 获取后台进程的PID
APP_PID=$!

# 实时输出日志
echo "应用已启动，PID: $APP_PID"
echo "实时日志输出："
tail -f app.log
