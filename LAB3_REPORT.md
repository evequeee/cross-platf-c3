# Лабораторна робота №3 - Обробка комунікацій у мікросервісній архітектурі

## Виконано завдання

### 1. Обрана доменна модель: **Логістика та доставка**

Проєкт реалізує систему логістики з 4 мікросервісами:
- **Order Service** - управління замовленнями
- **Warehouse Service** - управління складом та запасами
- **Delivery Service** - керування доставками
- **Notification Service** - повідомлення клієнтам

### 2. Мікросервісна архітектура

Кожен мікросервіс має власні:
- **Модель даних** (POJO класи)
- **Репозиторій** (in-memory з тестовими даними)
- **REST endpoints** для HTTP комунікації
- **gRPC сервіси** для високошвидкісної комунікації

### 3. Фейкові репозиторії даних

Створені репозиторії з реалістичними даними:
- `OrderRepository` - замовлення з товарами, адресами, статусами
- `WarehouseRepository` - товари на складі (Ноутбук, Миша, Телефон, Клавіатура)
- `DeliveryRepository` - доставки з водіями та часом доставки
- `NotificationRepository` - повідомлення для клієнтів

### 4. REST комунікація (Розділ 4.1-4.3)

#### Створені REST endpoints:

**Order Service** (`/api/orders`):
- `GET /` - список всіх замовлень
- `GET /{id}` - отримати замовлення
- `POST /` - створити замовлення
- `PUT /{id}/status` - оновити статус
- `POST /{id}/process` - обробити замовлення (викликає інші сервіси)

**Warehouse Service** (`/api/warehouse`):
- `GET /items` - список товарів
- `GET /items/{id}` - товар за ID
- `GET /products/{productId}` - товар за product ID
- `GET /check-availability` - перевірка наявності
- `POST /reserve` - резервування товару

**Delivery Service** (`/api/deliveries`):
- `GET /` - список доставок
- `GET /{id}` - доставка за ID
- `GET /order/{orderId}` - доставка за замовленням
- `POST /` - створити доставку
- `PUT /{id}/status` - оновити статус доставки

**Notification Service** (`/api/notifications`):
- `GET /` - список повідомлень
- `GET /{id}` - повідомлення за ID
- `GET /recipient/{email}` - за отримувачем
- `POST /send` - відправити повідомлення
- `POST /order-confirmation` - підтвердження замовлення

#### REST Clients

Створені typed REST clients з використанням `@RegisterRestClient`:
- `WarehouseClient` - для перевірки наявності та резервування
- `DeliveryClient` - для створення доставок
- `NotificationClient` - для відправки повідомлень

### 5. gRPC комунікація (Розділ 4.6-4.8)

#### Proto файли:

**warehouse.proto**:
```protobuf
service WarehouseService {
  rpc CheckStock(StockRequest) returns (StockResponse);
  rpc ReserveItems(ReserveRequest) returns (ReserveResponse);
  rpc GetItemInfo(ItemRequest) returns (ItemInfo);
}
```

**delivery.proto**:
```protobuf
service DeliveryService {
  rpc CreateDelivery(CreateDeliveryRequest) returns (DeliveryInfo);
  rpc UpdateDeliveryStatus(UpdateStatusRequest) returns (DeliveryInfo);
  rpc GetDeliveryByOrder(OrderRequest) returns (DeliveryInfo);
  rpc TrackDelivery(TrackRequest) returns (TrackResponse);
}
```

#### gRPC implementations:

- `WarehouseGrpcService` - реалізація gRPC для складу
- `DeliveryGrpcService` - реалізація gRPC для доставки

### 6. Конфігурація

**application.properties**:
```properties
# HTTP порт
quarkus.http.port=8080

# gRPC порт
quarkus.grpc.server.port=9000

# REST клієнти
quarkus.rest-client.warehouse-api.url=http://localhost:8080
quarkus.rest-client.delivery-api.url=http://localhost:8080
quarkus.rest-client.notification-api.url=http://localhost:8080

# gRPC клієнти
quarkus.grpc.clients.warehouse-grpc.host=localhost
quarkus.grpc.clients.warehouse-grpc.port=9000
quarkus.grpc.clients.delivery-grpc.host=localhost
quarkus.grpc.clients.delivery-grpc.port=9000
```

### 7. Залежності (pom.xml)

Додано:
- `quarkus-rest` - REST endpoints
- `quarkus-rest-client-jackson` - REST client
- `quarkus-rest-jackson` - JSON serialization
- `quarkus-grpc` - gRPC support
- `quarkus-arc` - CDI
- `quarkus-oidc` - Security (існуючий)

## Як запустити

### Компіляція проєкту:
```bash
./mvnw clean compile
```

### Запуск тестів:
```bash
./mvnw test
```

### Запуск у dev mode з DevUI:
```bash
./mvnw quarkus:dev
```

Після запуску:
- HTTP API: http://localhost:8080
- DevUI: http://localhost:8080/q/dev
- gRPC: localhost:9000

## Тестування через DevUI

1. Відкрити http://localhost:8080/q/dev
2. Перейти до розділу REST Client
3. Тестувати endpoints через Swagger UI
4. Переглянути gRPC сервіси у відповідному розділі

## Приклади викликів API

### Створити замовлення (REST):
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Тест Користувач",
    "customerEmail": "test@example.com",
    "deliveryAddress": "Київ, вул. Тестова, 1",
    "items": [{"productId": 101, "productName": "Ноутбук", "quantity": 1, "price": 25000}],
    "totalPrice": 25000
  }'
```

### Перевірити наявність на складі (REST):
```bash
curl "http://localhost:8080/api/warehouse/check-availability?productId=101&quantity=1"
```

### Отримати доставку (REST):
```bash
curl http://localhost:8080/api/deliveries/1
```

## Структура проєкту

```
src/main/java/org/acme/
├── order/
│   ├── Order.java
│   ├── OrderItem.java
│   ├── OrderStatus.java
│   ├── OrderRepository.java
│   ├── OrderResource.java (REST)
│   └── OrderService.java
├── warehouse/
│   ├── WarehouseItem.java
│   ├── WarehouseRepository.java
│   ├── WarehouseResource.java (REST)
│   └── WarehouseClient.java (REST Client)
├── delivery/
│   ├── Delivery.java
│   ├── DeliveryStatus.java
│   ├── DeliveryRepository.java
│   ├── DeliveryResource.java (REST)
│   └── DeliveryClient.java (REST Client)
├── notification/
│   ├── Notification.java
│   ├── NotificationType.java
│   ├── NotificationStatus.java
│   ├── NotificationRepository.java
│   ├── NotificationResource.java (REST)
│   └── NotificationClient.java (REST Client)
└── grpc/
    ├── warehouse/
    │   └── WarehouseGrpcService.java
    └── delivery/
        └── DeliveryGrpcService.java

src/main/proto/
├── warehouse.proto
└── delivery.proto
```

## Висновки

Реалізовано повнофункціональну мікросервісну архітектуру з:
- ✅ 4 мікросервіси з реалістичною доменною моделлю логістики
- ✅ REST API для всіх сервісів
- ✅ REST Clients для міжсервісної комунікації
- ✅ gRPC сервіси для високошвидкісної комунікації
- ✅ Фейкові репозиторії з тестовими даними
- ✅ Успішна збірка та тести
- ✅ Готовність до тестування через DevUI

Проєкт готовий для демонстрації та подальшого розширення!
