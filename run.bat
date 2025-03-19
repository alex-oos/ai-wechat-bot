@echo off
setlocal enabledelayedexpansion

set JAR_FILE_NAME=ai-wechat-bot.jar

echo Checking for existing processes...
for /f "skip=1 tokens=1" %%p in ('wmic process where "commandline like '%%%JAR_FILE_NAME%%%'" get ProcessId 2^>^&1') do (
    echo Terminating process with PID: %%p
    taskkill /F /PID %%p >nul
)

echo Starting Maven build...
call mvn clean package

if %errorlevel% neq 0 (
    echo Maven build failed.
    exit /b 1
)

if not exist "target\%JAR_FILE_NAME%" (
    echo JAR file not found: target\%JAR_FILE_NAME%
    exit /b 1
)

echo Starting JAR file...
start /B java -jar "target\%JAR_FILE_NAME%" > app.log 2>&1

echo Log file: %cd%\app.log
echo App started. Tailing log file...

powershell -NoProfile -ExecutionPolicy Bypass -Command "Get-Content -Path 'app.log' -Wait"

endlocal
