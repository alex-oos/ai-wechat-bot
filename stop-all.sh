#!/bin/bash
set -e

kill_by_port() {
  local port=$1
  if command -v lsof >/dev/null 2>&1; then
    local pids
    pids=$(lsof -ti tcp:"$port" || true)
    if [ -n "$pids" ]; then
      echo "Killing processes on port $port: $pids"
      kill -9 $pids || true
    fi
  fi
}

kill_by_name() {
  local pattern=$1
  if command -v pkill >/dev/null 2>&1; then
    pkill -f "$pattern" || true
  fi
}

# backend (9919) / frontend (8081)
kill_by_port 9919
kill_by_port 8081

# fallback name-based
kill_by_name "ai-wechat-bot.jar"
kill_by_name "vite"
