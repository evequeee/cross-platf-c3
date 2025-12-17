# Quick Test Script for Logistics Microservices

Write-Host "🧪 Testing Logistics Microservices..." -ForegroundColor Green
Write-Host ""

$baseUrls = @{
    Order = "http://localhost:8081"
    Warehouse = "http://localhost:8082"
    Delivery = "http://localhost:8083"
    Notification = "http://localhost:8084"
}

# Test function
function Test-Service {
    param($name, $url)
    Write-Host "Testing $name..." -ForegroundColor Cyan
    try {
        $response = Invoke-RestMethod -Uri $url -Method Get -TimeoutSec 5
        Write-Host "  ✅ $name is running" -ForegroundColor Green
        Write-Host "  Response count: $($response.Count)" -ForegroundColor Gray
        return $true
    } catch {
        Write-Host "  ❌ $name is not responding" -ForegroundColor Red
        return $false
    }
}

Write-Host "1️⃣ Testing individual services..." -ForegroundColor Yellow
Write-Host ""

$allRunning = $true
$allRunning = $allRunning -and (Test-Service "Order Service" "$($baseUrls.Order)/api/orders")
$allRunning = $allRunning -and (Test-Service "Warehouse Service" "$($baseUrls.Warehouse)/api/warehouse")
$allRunning = $allRunning -and (Test-Service "Delivery Service" "$($baseUrls.Delivery)/api/delivery")
$allRunning = $allRunning -and (Test-Service "Notification Service" "$($baseUrls.Notification)/api/notifications")

Write-Host ""

if ($allRunning) {
    Write-Host "2️⃣ Testing inter-service communication..." -ForegroundColor Yellow
    Write-Host ""
    
    # Test warehouse check
    Write-Host "Checking warehouse availability..." -ForegroundColor Cyan
    try {
        $available = Invoke-RestMethod -Uri "$($baseUrls.Warehouse)/api/warehouse/check/101?quantity=1" -Method Get
        if ($available) {
            Write-Host "  ✅ Warehouse check successful" -ForegroundColor Green
        }
    } catch {
        Write-Host "  ❌ Warehouse check failed" -ForegroundColor Red
    }
    
    Write-Host ""
    Write-Host "3️⃣ Creating test order..." -ForegroundColor Yellow
    
    $orderData = @{
        customerName = "Test User"
        customerEmail = "test@example.com"
        customerPhone = "+380501234567"
        deliveryAddress = "Test Address, Kyiv"
        totalPrice = 27500
        items = @(
            @{
                productId = 101
                productName = "Laptop Lenovo ThinkPad"
                quantity = 1
                pricePerUnit = 25000
            },
            @{
                productId = 102
                productName = "Mouse Logitech"
                quantity = 1
                pricePerUnit = 2500
            }
        )
    } | ConvertTo-Json -Depth 10
    
    try {
        $order = Invoke-RestMethod -Uri "$($baseUrls.Order)/api/orders" `
                                   -Method Post `
                                   -Body $orderData `
                                   -ContentType "application/json"
        Write-Host "  ✅ Order created with ID: $($order.id)" -ForegroundColor Green
        
        Write-Host ""
        Write-Host "4️⃣ Processing order..." -ForegroundColor Yellow
        
        try {
            $process = Invoke-RestMethod -Uri "$($baseUrls.Order)/api/orders/$($order.id)/process" -Method Post
            Write-Host "  ✅ Order processed successfully!" -ForegroundColor Green
            Write-Host "  This triggered:" -ForegroundColor Gray
            Write-Host "    - Warehouse stock check" -ForegroundColor Gray
            Write-Host "    - Stock reservation" -ForegroundColor Gray
            Write-Host "    - Delivery creation" -ForegroundColor Gray
            Write-Host "    - Customer notifications" -ForegroundColor Gray
        } catch {
            Write-Host "  ⚠️  Order processing failed: $($_.Exception.Message)" -ForegroundColor Yellow
        }
        
    } catch {
        Write-Host "  ❌ Failed to create order: $($_.Exception.Message)" -ForegroundColor Red
    }
    
} else {
    Write-Host "⚠️  Not all services are running. Start them first with ./start-all.ps1" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "📊 Service Status Summary" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "You can access the Dev UI for each service:" -ForegroundColor White
Write-Host "  Order:        http://localhost:8081/q/dev" -ForegroundColor Gray
Write-Host "  Warehouse:    http://localhost:8082/q/dev" -ForegroundColor Gray
Write-Host "  Delivery:     http://localhost:8083/q/dev" -ForegroundColor Gray
Write-Host "  Notification: http://localhost:8084/q/dev" -ForegroundColor Gray
Write-Host ""
