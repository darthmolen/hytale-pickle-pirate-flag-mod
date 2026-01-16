@echo off
REM Start Hytale Server with Pickle Pirate Flag mod
REM Usage: start-server.bat [server-path]

setlocal

set SERVER_PATH=%~1
if "%SERVER_PATH%"=="" set SERVER_PATH=C:\hytale-server

echo ===================================
echo Starting Hytale Server
echo Server: %SERVER_PATH%
echo ===================================
echo.

cd /d "%SERVER_PATH%\Server"
if errorlevel 1 (
    echo ERROR: Server folder not found at %SERVER_PATH%\Server
    pause
    exit /b 1
)

java -Xms2G -Xmx4G -jar HytaleServer.jar --allow-op

pause
