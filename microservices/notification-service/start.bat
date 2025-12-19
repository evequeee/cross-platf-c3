@echo off
title NOTIFICATION SERVICE (Port 8084)
echo ===============================================
echo   NOTIFICATION SERVICE (Port 8084)
echo ===============================================
echo.
set JAVA_TOOL_OPTIONS=-Duser.timezone=UTC
call mvnw.cmd clean quarkus:dev -Dquarkus.http.port=8084
pause
