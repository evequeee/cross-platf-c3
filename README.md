# Logistics Microservices Platform

Мікросервісна платформа для управління логістикою, розроблена на Quarkus.

## 🏗️ Архітектура

```
┌─────────────────────────────────────────────────────────────┐
│                    FRONTEND SERVICE (8080)                   │
│                         (Qute GUI)                           │
└──────┬──────────────┬──────────────┬──────────────┬─────────┘
       │ REST         │ REST         │ REST         │ REST
       ▼              ▼              ▼              ▼
┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│  ORDER   │   │WAREHOUSE │   │ DELIVERY │   │  NOTIF   │
│ SERVICE  │◄─►│ SERVICE  │◄─►│ SERVICE  │◄─►│ SERVICE  │
│  (8081)  │   │  (8082)  │   │  (8083)  │   │  (8084)  │
└────┬─────┘   └────┬─────┘   └────┬─────┘   └────┬─────┘
     │              │ gRPC:9082    │ gRPC:9083    │
     │              │              │              │
     ▼              ▼              ▼              ▼
┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│PostgreSQL│   │PostgreSQL│   │PostgreSQL│   │PostgreSQL│
│  :5432   │   │  :5433   │   │  :5434   │   │  :5435   │
└──────────┘   └──────────┘   └──────────┘   └──────────┘
```

## 🚀 Швидкий старт

### 1. Запуск всіх сервісів (Dev режим)
```bash
# Запустити всі мікросервіси
START-ALL.bat
```

### 2. Відкрити в браузері
- **Frontend**: http://localhost:8080
- **Dashboard**: http://localhost:8080/dashboard

## 📦 Мікросервіси

| Сервіс | Порт | gRPC | База даних | Pattern |
|--------|------|------|------------|---------|
| Frontend | 8080 | - | - | - |
| Order | 8081 | - | order_db:5432 | Active Record (Panache) |
| Warehouse | 8082 | 9082 | warehouse_db:5433 | Active Record (Panache) |
| Delivery | 8083 | 9083 | delivery_db:5434 | Repository (JPA) |
| Notification | 8084 | - | notification_db:5435 | Repository (JPA) |

## 🔧 Технології

- **Framework**: Quarkus 3.26.4
- **Java**: 21
- **Database**: PostgreSQL 16
- **Communication**: REST + gRPC
- **Security**: OIDC (Keycloak)
- **Templates**: Qute
- **Build**: Maven

## 🔐 Безпека (Keycloak)

### Запуск Keycloak
```bash
START-KEYCLOAK.bat
```

### Налаштування
- **URL**: http://localhost:8180
- **Admin**: admin / admin
- **Realm**: logistics

### Тестові користувачі
| Username | Password | Roles |
|----------|----------|-------|
| admin@logistics.com | admin123 | admin, user |
| user@logistics.com | user123 | user |
| manager@logistics.com | manager123 | manager, user |

## 📡 API Endpoints

### Order Service (8081)
```
GET    /api/orders           - Список замовлень
GET    /api/orders/{id}      - Отримати замовлення
POST   /api/orders           - Створити замовлення
PUT    /api/orders/{id}      - Оновити замовлення
DELETE /api/orders/{id}      - Видалити замовлення
```

### Warehouse Service (8082)
```
GET    /api/warehouse              - Список товарів
GET    /api/warehouse/{id}         - Отримати товар
GET    /api/warehouse/check/{id}   - Перевірити наявність
POST   /api/warehouse/reserve/{id} - Зарезервувати
POST   /api/warehouse/release/{id} - Звільнити резерв
```

### Delivery Service (8083)
```
GET    /api/delivery              - Список доставок
GET    /api/delivery/{id}         - Отримати доставку
GET    /api/delivery/track/{num}  - Відстежити посилку
POST   /api/delivery              - Створити доставку
PUT    /api/delivery/{id}/status  - Оновити статус
```

### Notification Service (8084)
```
GET    /api/notifications         - Список сповіщень
GET    /api/notifications/{id}    - Отримати сповіщення
POST   /api/notifications         - Створити сповіщення
POST   /api/notifications/send    - Надіслати сповіщення
```

## 🛠️ DevUI

Кожен сервіс має Quarkus DevUI:
- Frontend: http://localhost:8080/q/dev/
- Order: http://localhost:8081/q/dev/
- Warehouse: http://localhost:8082/q/dev/
- Delivery: http://localhost:8083/q/dev/
- Notification: http://localhost:8084/q/dev/

## 📋 Лабораторні роботи

| № | Назва | Статус |
|---|-------|--------|
| 1 | Quarkus REST + JDK 21 | ✅ |
| 2 | OIDC Security | ✅ |
| 3 | Мікросервіси + REST + gRPC | ✅ |
| 4 | Frontend + Keycloak | ✅ |
| 5 | Active Record Pattern (Panache) | ✅ |
| 6 | Repository Pattern (JPA) | ✅ |

## 📂 Структура проєкту

```
cross-platf-c3-main/
├── docker-compose-postgres.yml    # PostgreSQL databases
├── docker-compose-keycloak.yml    # Keycloak SSO
├── keycloak/
│   └── logistics-realm.json       # Keycloak realm config
├── START-ALL.bat                  # Start all services
├── START-KEYCLOAK.bat             # Start Keycloak
└── microservices/
    ├── frontend-service/          # GUI (port 8080)
    ├── order-service/             # Orders (port 8081)
    ├── warehouse-service/         # Inventory (port 8082)
    ├── delivery-service/          # Deliveries (port 8083)
    └── notification-service/      # Notifications (port 8084)
```

## 🐳 Docker

### Запуск баз даних
```bash
docker-compose -f docker-compose-postgres.yml up -d
```

### Запуск Keycloak
```bash
docker-compose -f docker-compose-keycloak.yml up -d
```

### Зупинка всього
```bash
docker-compose -f docker-compose-postgres.yml down
docker-compose -f docker-compose-keycloak.yml down
```
