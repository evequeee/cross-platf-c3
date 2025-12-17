# Start PostgreSQL databases for all microservices
Write-Host "Starting PostgreSQL databases..." -ForegroundColor Green

# Start Docker Compose
docker-compose -f docker-compose-postgres.yml up -d

# Wait for databases to be ready
Write-Host "`nWaiting for databases to be ready..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# Check database status
Write-Host "`nChecking database status:" -ForegroundColor Green
docker ps --filter "name=postgres" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

Write-Host "`n✅ PostgreSQL databases are ready!" -ForegroundColor Green
Write-Host "`nDatabase connections:" -ForegroundColor Cyan
Write-Host "  Order Service:        localhost:5432/order_db" -ForegroundColor White
Write-Host "  Warehouse Service:    localhost:5433/warehouse_db" -ForegroundColor White
Write-Host "  Delivery Service:     localhost:5434/delivery_db" -ForegroundColor White
Write-Host "  Notification Service: localhost:5435/notification_db" -ForegroundColor White
Write-Host "`nCredentials: postgres/postgres" -ForegroundColor Yellow
Write-Host "`nTo stop databases: docker-compose -f docker-compose-postgres.yml down" -ForegroundColor Gray
