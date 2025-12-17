# PowerShell script to start all microservices

Write-Host "🚀 Starting Logistics Microservices System..." -ForegroundColor Green
Write-Host ""

# Array of services with their ports
$services = @(
    @{Name="notification-service"; Port=8084},
    @{Name="warehouse-service"; Port=8082},
    @{Name="delivery-service"; Port=8083},
    @{Name="order-service"; Port=8081}
)

# Start each service in a new PowerShell window
foreach ($service in $services) {
    Write-Host "Starting $($service.Name) on port $($service.Port)..." -ForegroundColor Cyan
    
    $scriptBlock = {
        param($serviceName, $port)
        Set-Location $serviceName
        Write-Host "Starting $serviceName..." -ForegroundColor Yellow
        .\mvnw.cmd quarkus:dev -D"quarkus.http.port=$port"
    }
    
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "& {Set-Location '$PWD'; $scriptBlock '$($service.Name)' $($service.Port)}"
    
    Start-Sleep -Seconds 3
}

Write-Host ""
Write-Host "✅ All services are starting in separate windows..." -ForegroundColor Green
Write-Host ""
Write-Host "Access points:" -ForegroundColor Yellow
Write-Host "  Order Service:        http://localhost:8081" -ForegroundColor White
Write-Host "  Warehouse Service:    http://localhost:8082" -ForegroundColor White
Write-Host "  Delivery Service:     http://localhost:8083" -ForegroundColor White
Write-Host "  Notification Service: http://localhost:8084" -ForegroundColor White
Write-Host ""
Write-Host "Dev UI:" -ForegroundColor Yellow
Write-Host "  Order:        http://localhost:8081/q/dev" -ForegroundColor White
Write-Host "  Warehouse:    http://localhost:8082/q/dev" -ForegroundColor White
Write-Host "  Delivery:     http://localhost:8083/q/dev" -ForegroundColor White
Write-Host "  Notification: http://localhost:8084/q/dev" -ForegroundColor White
Write-Host ""
Write-Host "Close the individual windows to stop each service" -ForegroundColor Yellow
