@echo off
setlocal enabledelayedexpansion

set ROOT_DIR=%~dp0
set BACKEND_DIR=%ROOT_DIR%backend
set FRONTEND_DIR=%ROOT_DIR%frontend

set VITE_API_BASE=http://127.0.0.1:9919

cd /d %BACKEND_DIR%
echo Building backend...
call mvn -DskipTests package -P local-sqlite
if %errorlevel% neq 0 (
    echo Backend build failed.
    exit /b 1
)

echo Starting backend...
start /B java -jar "%BACKEND_DIR%target\ai-wechat-bot.jar" > "%BACKEND_DIR%app.log" 2>&1

echo Starting frontend...
cd /d %FRONTEND_DIR%
call npm install
call npm run dev -- --host 127.0.0.1 --port 8081

endlocal
