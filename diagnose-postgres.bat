@echo off
echo === PostgreSQL Port Conflict Diagnostic ===
echo.

echo 1. Checking for PostgreSQL Windows services...
sc query state= all | findstr /i "postgresql"
echo.

echo 2. What's listening on port 5432...
netstat -ano | findstr ":5432"
echo.

echo 3. Getting process details for port 5432...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":5432" ^| findstr "LISTENING"') do (
    echo PID: %%a
    wmic process where "ProcessId=%%a" get Name,ExecutablePath 2>nul
)
echo.
echo === Diagnostic Complete ===
pause
