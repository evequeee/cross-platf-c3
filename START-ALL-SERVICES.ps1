# Start All Microservices Script
# Requires PostgreSQL containers to be running

$env:JAVA_TOOL_OPTIONS = "-Duser.timezone=UTC"
$basePath = "g:\programming\cross-platf-c3-main\microservices"

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  Starting Logistics Microservices   " -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Start PostgreSQL containers
Write-Host "Starting PostgreSQL containers..." -ForegroundColor Yellow
docker-compose -f "g:\programming\cross-platf-c3-main\docker-compose-postgres.yml" up -d
Start-Sleep 5

Write-Host ""
Write-Host "Starting microservices..." -ForegroundColor Yellow

# Start each service in a new window
Start-Process -FilePath "cmd.exe" -ArgumentList "/c", "cd /d $basePath\warehouse-service && set JAVA_TOOL_OPTIONS=-Duser.timezone=UTC && mvnw.cmd quarkus:dev -Dquarkus.http.port=8082 -Ddebug=5005"
Start-Sleep 3

Start-Process -FilePath "cmd.exe" -ArgumentList "/c", "cd /d $basePath\delivery-service && set JAVA_TOOL_OPTIONS=-Duser.timezone=UTC && mvnw.cmd quarkus:dev -Dquarkus.http.port=8083 -Ddebug=5006"
Start-Sleep 3

Start-Process -FilePath "cmd.exe" -ArgumentList "/c", "cd /d $basePath\notification-service && set JAVA_TOOL_OPTIONS=-Duser.timezone=UTC && mvnw.cmd quarkus:dev -Dquarkus.http.port=8084 -Ddebug=5007"
Start-Sleep 3

Start-Process -FilePath "cmd.exe" -ArgumentList "/c", "cd /d $basePath\order-service && set JAVA_TOOL_OPTIONS=-Duser.timezone=UTC && mvnw.cmd quarkus:dev -Dquarkus.http.port=8081 -Ddebug=5008"
Start-Sleep 3

Start-Process -FilePath "cmd.exe" -ArgumentList "/c", "cd /d $basePath\frontend-service && set JAVA_TOOL_OPTIONS=-Duser.timezone=UTC && mvnw.cmd quarkus:dev -Dquarkus.http.port=8080 -Ddebug=5009"

Write-Host ""
Write-Host "=====================================" -ForegroundColor Green
Write-Host "  Services are starting...           " -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green
Write-Host ""
Write-Host "Endpoints:" -ForegroundColor Yellow
Write-Host "  Frontend:      http://localhost:8080" -ForegroundColor White
Write-Host "  Order API:     http://localhost:8081/api/orders" -ForegroundColor White
Write-Host "  Warehouse API: http://localhost:8082/api/warehouse" -ForegroundColor White
Write-Host "  Delivery API:  http://localhost:8083/api/delivery" -ForegroundColor White
Write-Host "  Notification:  http://localhost:8084/api/notifications" -ForegroundColor White
Write-Host ""
Write-Host "gRPC:" -ForegroundColor Yellow
Write-Host "  Warehouse gRPC: localhost:9082" -ForegroundColor White
Write-Host "  Delivery gRPC:  localhost:9083" -ForegroundColor White
Write-Host ""
