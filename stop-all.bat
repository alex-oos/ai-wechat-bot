@echo off
setlocal enabledelayedexpansion

set BACKEND_PORT=9919
set FRONTEND_PORT=8081

for %%p in (%BACKEND_PORT% %FRONTEND_PORT%) do (
  for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":%%p " ^| findstr LISTENING') do (
    echo Killing PID %%a on port %%p
    taskkill /F /PID %%a >nul 2>&1
  )
)

for /f "tokens=2 delims=," %%a in ('tasklist /FI "IMAGENAME eq java.exe" /FO CSV ^| findstr /I "java.exe"') do (
  echo Found java process PID: %%a
)

endlocal
