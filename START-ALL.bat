@echo off
cls
echo ========================================
echo   LOGISTICS MICROSERVICES PLATFORM
echo ========================================
echo.

REM Set timezone to UTC to avoid PostgreSQL "Europe/Kiev" issue
set JAVA_TOOL_OPTIONS=-Duser.timezone=UTC

echo Checking Docker...
docker ps >nul 2>&1
if errorlevel 1 (
    echo ERROR: Docker is not running!
    pause
    exit /b 1
)
echo OK: Docker is running
echo.

echo Starting PostgreSQL databases...
cd /d G:\programming\cross-platf-c3-main
docker-compose -f docker-compose-postgres.yml up -d
ping -n 6 127.0.0.1 >nul
echo OK: Databases started
echo.

echo Starting ORDER SERVICE on port 8081...
start "ORDER SERVICE" cmd /k "cd /d G:\programming\cross-platf-c3-main\microservices\order-service && set JAVA_TOOL_OPTIONS=-Duser.timezone=UTC && mvnw.cmd quarkus:dev -Dquarkus.http.port=8081"
ping -n 3 127.0.0.1 >nul

echo Starting WAREHOUSE SERVICE on port 8082...
start "WAREHOUSE SERVICE" cmd /k "cd /d G:\programming\cross-platf-c3-main\microservices\warehouse-service && set JAVA_TOOL_OPTIONS=-Duser.timezone=UTC && mvnw.cmd quarkus:dev -Dquarkus.http.port=8082"
ping -n 3 127.0.0.1 >nul

echo Starting DELIVERY SERVICE on port 8083...
start "DELIVERY SERVICE" cmd /k "cd /d G:\programming\cross-platf-c3-main\microservices\delivery-service && set JAVA_TOOL_OPTIONS=-Duser.timezone=UTC && mvnw.cmd quarkus:dev -Dquarkus.http.port=8083"
ping -n 3 127.0.0.1 >nul

echo Starting NOTIFICATION SERVICE on port 8084...
start "NOTIFICATION SERVICE" cmd /k "cd /d G:\programming\cross-platf-c3-main\microservices\notification-service && set JAVA_TOOL_OPTIONS=-Duser.timezone=UTC && mvnw.cmd quarkus:dev -Dquarkus.http.port=8084"
ping -n 3 127.0.0.1 >nul

echo Starting FRONTEND SERVICE on port 8080...
start "FRONTEND SERVICE" cmd /k "cd /d G:\programming\cross-platf-c3-main\microservices\frontend-service && set JAVA_TOOL_OPTIONS=-Duser.timezone=UTC && mvnw.cmd quarkus:dev -Dquarkus.http.port=8080"
ping -n 3 127.0.0.1 >nul

echo.
echo ========================================
echo   ALL SERVICES STARTED!
echo ========================================
echo.
echo Wait for services to initialize...
echo.
echo ========================================
echo   URLS:
echo ========================================
echo.
echo   FRONTEND (Main Entry):
echo     http://localhost:8080
echo.
echo   DevUI Links:
echo     Frontend:     http://localhost:8080/q/dev/
echo     Order:        http://localhost:8081/q/dev/
echo     Warehouse:    http://localhost:8082/q/dev/
echo     Delivery:     http://localhost:8083/q/dev/
echo     Notification: http://localhost:8084/q/dev/
echo.
echo   gRPC Ports:
echo     Warehouse:    9082
echo     Delivery:     9083
echo.
echo ========================================
echo.
echo Press any key to open Frontend in browser...
pause >nul

start http://localhost:8080

echo.
echo Press any key to close this window...
pause >nul
