@echo off
title DELIVERY SERVICE (Port 8083)
echo ===============================================
echo   DELIVERY SERVICE (Port 8083)
echo ===============================================
echo.
set JAVA_TOOL_OPTIONS=-Duser.timezone=UTC
call mvnw.cmd clean quarkus:dev -Dquarkus.http.port=8083
pause
