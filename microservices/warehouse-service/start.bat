@echo off
title WAREHOUSE SERVICE (Port 8082)
echo ===============================================
echo   WAREHOUSE SERVICE (Port 8082)
echo ===============================================
echo.
set JAVA_TOOL_OPTIONS=-Duser.timezone=UTC
call mvnw.cmd clean quarkus:dev -Dquarkus.http.port=8082
pause
