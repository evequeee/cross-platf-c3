#!/bin/bash

# Script to start all microservices in dev mode

echo "🚀 Starting Logistics Microservices System..."
echo ""

# Function to start a service in a new terminal
start_service() {
    local service_name=$1
    local port=$2
    
    echo "Starting $service_name on port $port..."
    
    # Start in background
    cd $service_name
    ./mvnw quarkus:dev -Dquarkus.http.port=$port &
    cd ..
    
    sleep 2
}

# Start services in order
start_service "notification-service" 8084
start_service "warehouse-service" 8082
start_service "delivery-service" 8083
start_service "order-service" 8081

echo ""
echo "✅ All services are starting..."
echo ""
echo "Access points:"
echo "  Order Service:        http://localhost:8081"
echo "  Warehouse Service:    http://localhost:8082"
echo "  Delivery Service:     http://localhost:8083"
echo "  Notification Service: http://localhost:8084"
echo ""
echo "Dev UI:"
echo "  Order:        http://localhost:8081/q/dev"
echo "  Warehouse:    http://localhost:8082/q/dev"
echo "  Delivery:     http://localhost:8083/q/dev"
echo "  Notification: http://localhost:8084/q/dev"
echo ""
echo "Press Ctrl+C to stop all services"

# Wait for user interrupt
wait
