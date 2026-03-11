#!/bin/bash

# 使用 jps 查找 Java 进程，并通过 grep 过滤目标进程
PIDS=$(jps -l | grep 'ai-wechat-bot' | awk '{print $1}')

if [ -z "$PIDS" ]; then
    echo "未找到 ai-wechat-bot 进程。"
    exit 0
fi

# 终止所有匹配的进程
echo "找到以下进程：$PIDS"
for PID in $PIDS; do
    echo "正在终止进程 $PID ..."
    kill $PID
    # 若需要强制终止，可替换为：kill -9 $PID
done

echo "操作完成。"
