#!/bin/bash
set -e

ROOT_DIR=$(cd "$(dirname "$0")" && pwd)
BACKEND_DIR="$ROOT_DIR/backend"
FRONTEND_DIR="$ROOT_DIR/frontend"

export VITE_API_BASE="http://127.0.0.1:9919"

cd "$BACKEND_DIR"
echo "Building backend..."
mvn -DskipTests package -P local-sqlite

echo "Starting backend..."
nohup java -jar "$BACKEND_DIR/target/ai-wechat-bot.jar" > "$BACKEND_DIR/app.log" 2>&1 &
BACKEND_PID=$!
echo "Backend PID: $BACKEND_PID"

echo "Starting frontend..."
cd "$FRONTEND_DIR"
npm install
npm run dev -- --host 127.0.0.1 --port 8081
