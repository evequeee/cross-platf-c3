@echo off
title FRONTEND SERVICE (8080)
cd /d %~dp0
echo Starting Frontend Service on port 8080...
set JAVA_TOOL_OPTIONS=-Duser.timezone=UTC
call mvnw.cmd quarkus:dev -Dquarkus.http.port=8080
pause
