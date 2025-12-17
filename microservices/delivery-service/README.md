# Delivery Service - Logistics Microservice

Мікросервіс управління доставками в логістичній системі.

## Функціональність
- Створення та управління доставками
- Призначення водіїв
- Відстеження доставок в реальному часі
- Оновлення статусів доставки
- Підтримка REST та gRPC API

## Запуск
```bash
./mvnw quarkus:dev
```

## Порти
- HTTP: 8083
- gRPC: 9003

## REST Endpoints
- GET /api/delivery - всі доставки
- GET /api/delivery/{id} - доставка за ID
- GET /api/delivery/order/{orderId} - доставка за ID замовлення
- GET /api/delivery/track/{trackingNumber} - відстеження
- POST /api/delivery - створити доставку
- PUT /api/delivery/{id}/status - оновити статус
- PUT /api/delivery/{id}/location - оновити локацію

## gRPC Methods
- CreateDelivery - створення доставки
- UpdateDeliveryStatus - оновлення статусу
- GetDeliveryByOrder - отримання за замовленням
- TrackDelivery - відстеження доставки

## Статуси доставки
- PENDING - Очікує обробки
- ASSIGNED - Призначено водія
- PICKED_UP - Товар забрано
- IN_TRANSIT - В дорозі
- OUT_FOR_DELIVERY - На доставці
- DELIVERED - Доставлено
- FAILED - Не вдалося доставити
- RETURNED - Повернуто
