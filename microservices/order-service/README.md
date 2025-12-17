# Order Service - Logistics Microservice

Мікросервіс управління замовленнями в логістичній системі.

## Функціональність
- Створення та управління замовленнями
- Взаємодія зі складом через REST/gRPC
- Координація доставки
- Відправка повідомлень клієнтам

## Запуск
```bash
./mvnw quarkus:dev
```

## Порти
- HTTP: 8081
- gRPC: 9001

## Endpoints
- GET /api/orders - отримати всі замовлення
- GET /api/orders/{id} - отримати замовлення за ID
- POST /api/orders - створити нове замовлення
- POST /api/orders/{id}/process - обробити замовлення
- PUT /api/orders/{id}/status - оновити статус замовлення
