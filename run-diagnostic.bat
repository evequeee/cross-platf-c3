@echo off
echo === PostgreSQL Port Conflict Diagnostic === > g:\programming\cross-platf-c3-main\diagnostic-result.txt
echo. >> g:\programming\cross-platf-c3-main\diagnostic-result.txt

echo 1. PostgreSQL Windows Services: >> g:\programming\cross-platf-c3-main\diagnostic-result.txt
sc query state= all | findstr /i "postgresql" >> g:\programming\cross-platf-c3-main\diagnostic-result.txt 2>&1
echo. >> g:\programming\cross-platf-c3-main\diagnostic-result.txt

echo 2. Port 5432 Listeners (netstat): >> g:\programming\cross-platf-c3-main\diagnostic-result.txt
netstat -ano | findstr ":5432" >> g:\programming\cross-platf-c3-main\diagnostic-result.txt 2>&1
echo. >> g:\programming\cross-platf-c3-main\diagnostic-result.txt

echo 3. Docker container database check: >> g:\programming\cross-platf-c3-main\diagnostic-result.txt
docker exec order-postgres psql -U postgres -c "\l" >> g:\programming\cross-platf-c3-main\diagnostic-result.txt 2>&1
echo. >> g:\programming\cross-platf-c3-main\diagnostic-result.txt

echo 4. Process details for listening processes: >> g:\programming\cross-platf-c3-main\diagnostic-result.txt
for /f "tokens=5" %%a in ('netstat -ano ^| findstr ":5432" ^| findstr "LISTENING"') do (
    echo PID: %%a >> g:\programming\cross-platf-c3-main\diagnostic-result.txt
    wmic process where "ProcessId=%%a" get Name,ExecutablePath /format:list >> g:\programming\cross-platf-c3-main\diagnostic-result.txt 2>&1
)
echo. >> g:\programming\cross-platf-c3-main\diagnostic-result.txt

echo === DIAGNOSTIC COMPLETE === >> g:\programming\cross-platf-c3-main\diagnostic-result.txt
