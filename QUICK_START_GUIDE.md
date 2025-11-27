## естові користувачі

| Username | Password | Роль | Доступ |
|----------|----------|------|--------|
| **alice** | alice | user | Перегляд даних |
| **admin** | admin | admin | Повний доступ + операції |

---

## Основні URL

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

### Тестування через Postman

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

## Docker команди

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

## Development команди

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

### Проблема: Quarkus не стартує
```bash
# Очистіть build
./mvnw clean

# Перекомпілюйте
./mvnw compile

# Запустіть знову
./mvnw quarkus:dev
```