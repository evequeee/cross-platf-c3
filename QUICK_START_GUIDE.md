# 🚀 Logistics Microservices Platform - Quick Start Guide

## Вітаємо!

Ви успішно налаштували систему з **4 мікросервісів** з **OIDC автентифікацією** та **Keycloak**! 

---

## 📊 Що вже запущено?

✅ **Quarkus додаток** - http://localhost:8080/  
✅ **Keycloak сервер** - http://localhost:8180/  
✅ **gRPC сервер** - localhost:9000  
✅ **4 мікросервіси**: Order, Warehouse, Delivery, Notification  

---

## 🔑 Тестові користувачі

| Username | Password | Роль | Доступ |
|----------|----------|------|--------|
| **alice** | alice | user | Перегляд даних |
| **admin** | admin | admin | Повний доступ + операції |

---

## 🌐 Основні URL

### Frontend (Web GUI)
- **Головна сторінка**: http://localhost:8080/
- **Dashboard**: http://localhost:8080/dashboard (потребує логін)
- **Login сторінка**: http://localhost:8080/login

### REST API Endpoints

#### Orders Service
- `GET /api/orders` - Всі замовлення (user + admin)
- `GET /api/orders/{id}` - Одне замовлення (user + admin)
- `POST /api/orders` - Створити замовлення (user + admin)
- `POST /api/orders/{id}/process` - Обробити замовлення (тільки admin)

#### Warehouse Service
- `GET /api/warehouse/items` - Всі товари (user + admin)
- `GET /api/warehouse/items/{productId}` - Товар по ID (user + admin)
- `POST /api/warehouse/reserve` - Резервувати товар (тільки admin)

#### Delivery Service
- `GET /api/deliveries` - Всі доставки (user + admin)
- `GET /api/deliveries/{id}` - Доставка по ID (user + admin)
- `GET /api/deliveries/order/{orderId}` - Доставка по замовленню (user + admin)
- `POST /api/deliveries` - Створити доставку (тільки admin)

#### Notification Service
- `GET /api/notifications` - Всі повідомлення (тільки admin)
- `GET /api/notifications/{id}` - Повідомлення по ID (user + admin)
- `GET /api/notifications/recipient/{recipient}` - По отримувачу (user + admin)

### Keycloak Admin Console
- **URL**: http://localhost:8180/
- **Username**: admin
- **Password**: admin

---

## 🧪 Як тестувати систему?

### 1. Перевірка автентифікації

**Крок 1**: Відкрийте http://localhost:8080/dashboard  
**Крок 2**: Вас автоматично перенаправить на Keycloak login  
**Крок 3**: Введіть **alice** / **alice**  
**Крок 4**: Ви побачите dashboard з даними  

### 2. Тестування ролей

#### Як користувач (alice):
```bash
# Отримати всі замовлення - OK ✅
curl http://localhost:8080/api/orders

# Спробувати обробити замовлення - FORBIDDEN ❌
curl -X POST http://localhost:8080/api/orders/1/process
```

#### Як адміністратор (admin):
```bash
# Отримати всі замовлення - OK ✅
curl http://localhost:8080/api/orders

# Обробити замовлення - OK ✅
curl -X POST http://localhost:8080/api/orders/1/process
```

### 3. Тестування через Postman

**1. Отримати токен:**
```http
POST http://localhost:8180/realms/quarkus/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

client_id=logistics-app
&client_secret=secret
&username=alice
&password=alice
&grant_type=password
```

**2. Використати токен:**
```http
GET http://localhost:8080/api/orders
Authorization: Bearer {YOUR_ACCESS_TOKEN}
```

---

## 🐳 Docker команди

### Запуск Keycloak
```bash
docker-compose -f docker-compose-keycloak.yml up -d
```

### Зупинка Keycloak
```bash
docker-compose -f docker-compose-keycloak.yml down
```

### Перегляд логів
```bash
docker logs -f logistics-keycloak
```

### Статус контейнерів
```bash
docker ps
```

---

## 💻 Development команди

### Запуск у dev режимі
```bash
./mvnw quarkus:dev
```

### Компіляція
```bash
./mvnw clean compile
```

### Запуск тестів
```bash
./mvnw test
```

### Створення production build
```bash
./mvnw clean package
```

### Запуск production
```bash
java -jar target/quarkus-app/quarkus-run.jar
```

---

## 🔍 Dev UI (Quarkus Developer Console)

Відкрийте http://localhost:8080/q/dev для доступу до:
- 📊 Application Info
- 🔐 OIDC Configuration
- 📡 gRPC Services
- 🗂️ REST Endpoints
- 🔄 Live Reload Status

---

## 📝 Приклади реального використання

### Сценарій 1: Створення замовлення

**1. Логін як alice**  
**2. Створити замовлення:**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "customerName": "Alice",
    "customerEmail": "alice@example.com",
    "items": [
      {"productId": "P001", "productName": "Laptop", "quantity": 1, "pricePerUnit": 1200.00}
    ],
    "totalAmount": 1200.00
  }'
```

### Сценарій 2: Перевірка складу

```bash
curl http://localhost:8080/api/warehouse/items \
  -H "Authorization: Bearer {token}"
```

### Сценарій 3: Відстеження доставки

```bash
curl http://localhost:8080/api/deliveries/order/1 \
  -H "Authorization: Bearer {token}"
```

---

## 🐛 Troubleshooting

### Проблема: Keycloak недоступний
```bash
# Перевірте чи запущений контейнер
docker ps

# Перезапустіть Keycloak
docker-compose -f docker-compose-keycloak.yml restart
```

### Проблема: 401 Unauthorized
- Перевірте чи правильний токен
- Перевірте чи не закінчився термін дії токену (за замовчуванням 5 хв)
- Отримайте новий токен

### Проблема: 403 Forbidden
- Перевірте чи користувач має правильну роль
- alice має роль "user" - обмежений доступ
- admin має роль "admin" - повний доступ

### Проблема: Quarkus не стартує
```bash
# Очистіть build
./mvnw clean

# Перекомпілюйте
./mvnw compile

# Запустіть знову
./mvnw quarkus:dev
```

---

## 📚 Додаткові ресурси

- **LAB3_REPORT.md** - Документація лабораторної роботи №3 (Мікросервіси)
- **LAB4_REPORT.md** - Документація лабораторної роботи №4 (Безпека)
- **Quarkus документація**: https://quarkus.io/guides/
- **Keycloak документація**: https://www.keycloak.org/documentation

---

## 🎉 Вітаємо!

Ваша логістична система **готова до використання**! 

Насолоджуйтесь розробкою! 🚀🔐

---

**Створено з ❤️ використовуючи Quarkus, Keycloak, gRPC, та Qute**
