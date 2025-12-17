# Logistics Microservices System

Система з 4 мікросервісів для управління логістичними процесами, побудована на Quarkus 3.26.4.

## 📦 Архітектура

```
┌─────────────────┐
│  Order Service  │ ← Координує всі процеси
│   (Port 8081)   │
└────────┬────────┘
         │
    ┌────┴────┬──────────┬──────────────┐
    │         │          │              │
    ▼         ▼          ▼              ▼
┌────────┐ ┌────────┐ ┌──────────┐ ┌──────────────┐
│Warehouse│ │Delivery│ │Notification│ │   gRPC     │
│ (8082) │ │ (8083) │ │  (8084)   │ │ (9001-9003)│
└────────┘ └────────┘ └──────────┘ └──────────────┘
```

## 🎯 Мікросервіси

### 1. Order Service (8081 / gRPC 9001)
- Управління замовленнями
- Координація між сервісами
- REST + gRPC комунікація

### 2. Warehouse Service (8082 / gRPC 9002)
- Управління складом
- Перевірка наявності товарів
- Резервування та видача товарів

### 3. Delivery Service (8083 / gRPC 9003)
- Управління доставками
- Призначення водіїв
- Відстеження в реальному часі

### 4. Notification Service (8084)
- Email, SMS, Push повідомлення
- Автоматичні повторні спроби
- Історія повідомлень

## 🚀 Швидкий старт

### Запуск окремого сервісу в dev mode:
```bash
cd order-service
./mvnw quarkus:dev
```

### Запуск всіх сервісів (кожен в окремому терміналі):
```bash
# Термінал 1 - Notification Service
cd notification-service && ./mvnw quarkus:dev

# Термінал 2 - Warehouse Service  
cd warehouse-service && ./mvnw quarkus:dev

# Термінал 3 - Delivery Service
cd delivery-service && ./mvnw quarkus:dev

# Термінал 4 - Order Service
cd order-service && ./mvnw quarkus:dev
```

### Використання Docker Compose:
```bash
cd microservices
docker-compose up --build
```

## 📡 API Endpoints

### Order Service (http://localhost:8081)
- `GET /api/orders` - всі замовлення
- `POST /api/orders` - створити замовлення
- `POST /api/orders/{id}/process` - обробити замовлення

### Warehouse Service (http://localhost:8082)
- `GET /api/warehouse` - всі товари
- `GET /api/warehouse/check/{productId}?quantity=X` - перевірка наявності
- `POST /api/warehouse/reserve/{productId}?quantity=X` - резервування

### Delivery Service (http://localhost:8083)
- `GET /api/delivery` - всі доставки
- `GET /api/delivery/track/{trackingNumber}` - відстеження
- `POST /api/delivery?orderId=X&address=...` - створити доставку

### Notification Service (http://localhost:8084)
- `GET /api/notifications` - всі повідомлення
- `POST /api/notifications/send?recipient=...&type=EMAIL&subject=...&message=...`

## 🧪 Тестування через DevUI

Кожен сервіс має Quarkus Dev UI:
- Order: http://localhost:8081/q/dev
- Warehouse: http://localhost:8082/q/dev
- Delivery: http://localhost:8083/q/dev
- Notification: http://localhost:8084/q/dev

## 🔗 Приклад взаємодії

### Створення та обробка замовлення:

1. **Створення замовлення:**
```bash
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Тест Тестович",
    "customerEmail": "test@example.com",
    "customerPhone": "+380501234567",
    "deliveryAddress": "вул. Тестова, 1, Київ",
    "totalPrice": 15000,
    "items": [
      {
        "productId": 101,
        "productName": "Ноутбук Lenovo ThinkPad",
        "quantity": 1,
        "pricePerUnit": 25000
      }
    ]
  }'
```

2. **Обробка замовлення (REST):**
```bash
curl -X POST http://localhost:8081/api/orders/1/process
```

Це автоматично:
- Перевірить наявність на складі (Warehouse Service)
- Зарезервує товари
- Створить доставку (Delivery Service)
- Відправить повідомлення (Notification Service)

3. **Відстеження через gRPC:**
Використовуйте gRPC клієнт або Quarkus Dev UI

## 📊 Технології

- **Framework:** Quarkus 3.26.4
- **Java:** 21
- **REST:** Jakarta REST (JAX-RS)
- **gRPC:** Quarkus gRPC
- **Build:** Maven
- **Container:** Docker (optional)

## 🔧 REST vs gRPC

Кожен сервіс підтримує обидва протоколи:
- **REST** - зручний для тестування, веб-інтеграції
- **gRPC** - швидкий, для міжсервісної комунікації

## 📝 Структура проєкту

```
microservices/
├── order-service/
│   ├── src/main/java/org/acme/logistics/order/
│   ├── src/main/resources/application.properties
│   └── pom.xml
├── warehouse-service/
│   ├── src/main/java/org/acme/logistics/warehouse/
│   ├── src/main/proto/warehouse.proto
│   └── pom.xml
├── delivery-service/
│   ├── src/main/java/org/acme/logistics/delivery/
│   ├── src/main/proto/delivery.proto
│   └── pom.xml
├── notification-service/
│   ├── src/main/java/org/acme/logistics/notification/
│   └── pom.xml
└── docker-compose.yml
```

## 🎓 Лабораторна робота №3

Виконано відповідно до завдання:
1. ✅ Обрано доменну модель (логістика)
2. ✅ Розділено відповідальність на 4 мікросервіси
3. ✅ Створено фейкові репозиторії з реалістичними даними
4. ✅ Налаштовано REST комунікацію
5. ✅ Налаштовано gRPC комунікацію
6. ✅ Готово до тестування через DevUI

## 📞 Підтримка

Кожен сервіс має власний README з детальною документацією.
