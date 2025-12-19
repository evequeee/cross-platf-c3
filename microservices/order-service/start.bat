@echo off
title ORDER SERVICE (Port 8081)
echo ===============================================
echo   ORDER SERVICE (Port 8081)
echo ===============================================
echo.
set JAVA_TOOL_OPTIONS=-Duser.timezone=UTC
call mvnw.cmd clean quarkus:dev -Dquarkus.http.port=8081
pause
