# Warehouse Service - Logistics Microservice

Мікросервіс управління складом в логістичній системі.

## Функціональність
- Управління товарами на складі
- Перевірка наявності товарів
- Резервування та видача товарів
- Підтримка REST та gRPC API

## Запуск
```bash
./mvnw quarkus:dev
```

## Порти
- HTTP: 8082
- gRPC: 9002

## REST Endpoints
- GET /api/warehouse - отримати всі товари
- GET /api/warehouse/{id} - отримати товар за ID
- GET /api/warehouse/product/{productId} - товар за product ID
- GET /api/warehouse/check/{productId} - перевірити наявність
- POST /api/warehouse/reserve/{productId} - зарезервувати товар
- POST /api/warehouse/release/{productId} - зняти резервацію

## gRPC Methods
- CheckStock - перевірка наявності
- ReserveItems - резервування
- ReleaseItems - зняття резервації
- GetItemInfo - інформація про товар
