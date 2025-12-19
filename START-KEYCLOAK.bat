@echo off
cls
echo ========================================
echo   KEYCLOAK STARTUP
echo ========================================
echo.

echo Checking Docker...
docker ps >nul 2>&1
if errorlevel 1 (
    echo ERROR: Docker is not running!
    pause
    exit /b 1
)
echo OK: Docker is running
echo.

cd /d G:\programming\cross-platf-c3-main

echo Starting Keycloak...
docker-compose -f docker-compose-keycloak.yml up -d

echo.
echo Waiting for Keycloak to start (this may take 30-60 seconds)...
ping -n 30 127.0.0.1 >nul

echo.
echo ========================================
echo   KEYCLOAK STARTED!
echo ========================================
echo.
echo   Admin Console: http://localhost:8180
echo   Admin Login:   admin / admin
echo.
echo   Test Users:
echo     admin@logistics.com / admin123  (admin role)
echo     user@logistics.com / user123    (user role)
echo     manager@logistics.com / manager123 (manager role)
echo.
echo   Realm: logistics
echo.
echo ========================================
echo.
echo Press any key to open Keycloak in browser...
pause >nul

start http://localhost:8180

echo.
echo Press any key to close this window...
pause >nul
