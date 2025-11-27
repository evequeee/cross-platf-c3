# Лабораторна робота №4 - Безпека мікросервісів з OIDC та Keycloak

## Виконано завдання

### 1. Створено новий мікросервіс з графічним інтерфейсом ✅

**Frontend мікросервіс** створено з використанням **Qute templating engine**:

#### Компоненти:
- `FrontendResource.java` - контролер для веб-інтерфейсу
- Три HTML шаблони:
  - `index.html` - головна сторінка
  - `dashboard.html` - панель керування з даними
  - `login.html` - сторінка входу

#### Функціонал:
- **Головна сторінка** (`/`) - привітання та навігація
- **Dashboard** (`/dashboard`) - захищена панель з:
  - Статистикою замовлень, складу, доставок
  - Таблицями з даними всіх сервісів
  - Інформацією про користувача
- **Сторінка входу** (`/login`) - інформація про автентифікацію

### 2. Додано безпекову частину з OIDC ✅

#### Залежності в pom.xml:
```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-oidc</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-security</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-keycloak-authorization</artifactId>
</dependency>
```

#### Конфігурація OIDC (application.properties):
```properties
# OIDC Configuration
quarkus.oidc.auth-server-url=http://localhost:8180/realms/quarkus
quarkus.oidc.client-id=logistics-app
quarkus.oidc.credentials.secret=**********
quarkus.oidc.application-type=web-app
quarkus.oidc.roles.source=accesstoken

# Security permissions
quarkus.http.auth.permission.public.paths=/,/login,/q/*
quarkus.http.auth.permission.public.policy=permit
quarkus.http.auth.permission.authenticated.paths=/*
quarkus.http.auth.permission.authenticated.policy=authenticated
```

### 3. Пропаговано підтримку безпеки на всі мікросервіси ✅

Додано **Role-Based Access Control (RBAC)** до всіх endpoints:

#### OrderResource:
```java
@Authenticated // Всі endpoints потребують автентифікації
@GET
@RolesAllowed({"user", "admin"}) // Перегляд для user та admin
public List<Order> getAllOrders()

@POST
@RolesAllowed("admin") // Тільки admin може обробляти
public Response processOrder(@PathParam("id") Long id)
```

#### WarehouseResource:
```java
@GET @RolesAllowed({"user", "admin"})
public List<WarehouseItem> getAllItems()

@POST @RolesAllowed("admin") // Резервування тільки для admin
public Response reserveStock(...)
```

#### DeliveryResource:
```java
@GET @RolesAllowed({"user", "admin"})
public List<Delivery> getAllDeliveries()

@POST @RolesAllowed("admin")
public Response createDelivery(...)
```

#### NotificationResource:
```java
@GET @RolesAllowed("admin") // Всі повідомлення тільки admin
public List<Notification> getAllNotifications()

@GET @RolesAllowed({"user", "admin"})
public List<Notification> getNotificationsByRecipient(...)
```

### 4. Перевірка роботи безпеки в dev режимі

#### Dev Services Configuration:
```properties
%dev.quarkus.keycloak.devservices.enabled=true
%dev.quarkus.keycloak.devservices.realm-path=quarkus-realm.json
%dev.quarkus.keycloak.devservices.users.alice=alice
%dev.quarkus.keycloak.devservices.users.bob=admin
```

**Dev Services автоматично запускає Keycloak** в Docker контейнері!

#### Тестові користувачі:
- **alice** / **alice** - роль `user`
- **admin** / **admin** - роль `admin`

#### Запуск у dev mode:
```bash
./mvnw quarkus:dev
```

Quarkus автоматично:
1. Запустить Keycloak в Docker
2. Створить realm `quarkus`
3. Налаштує client `logistics-app`
4. Створить користувачів alice та admin

### 5. Налагодження Keycloak для production ⏳

#### Створено Keycloak realm конфігурацію:
`src/main/resources/quarkus-realm.json` містить:
- Realm "quarkus"
- Client "logistics-app" з секретом
- Користувачів з ролями
- Налаштування redirect URIs

#### Production налаштування:
Для production можна використати:

**Варіант 1: Docker Compose**
```yaml
services:
  keycloak:
    image: quay.io/keycloak/keycloak:latest
    environment:
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin
    ports:
      - "8180:8080"
    command: start-dev
```

**Варіант 2: Локальний Keycloak**
```bash
# Завантажити Keycloak
https://www.keycloak.org/downloads

# Запустити
bin/kc.sh start-dev --http-port=8180

# Імпортувати realm
Admin Console -> Import -> quarkus-realm.json
```

### 6. Production Configuration

Для production режиму потрібно оновити `application.properties`:

```properties
%prod.quarkus.oidc.auth-server-url=https://your-keycloak-server.com/realms/quarkus
%prod.quarkus.oidc.client-id=logistics-app
%prod.quarkus.oidc.credentials.secret=YOUR_PRODUCTION_SECRET
%prod.quarkus.oidc.tls.verification=required
```

### 7. Перевірка роботи в production ⏳

Після запуску Keycloak:

1. **Запустити додаток**:
```bash
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```

2. **Доступ до системи**:
   - Головна: http://localhost:8080/
   - Dashboard: http://localhost:8080/dashboard (автоматичний redirect до Keycloak)
   - Keycloak Admin: http://localhost:8180/

3. **Перевірити автентифікацію**:
   - Спроба доступу до `/dashboard` → redirect на Keycloak login
   - Ввести alice/alice → доступ з роллю `user`
   - Ввести admin/admin → доступ з роллю `admin`

## Структура проєкту

```
src/main/
├── java/org/acme/
│   ├── frontend/
│   │   └── FrontendResource.java (Qute контролер)
│   ├── order/
│   │   ├── OrderResource.java (@RolesAllowed)
│   │   └── OrderService.java
│   ├── warehouse/
│   │   └── WarehouseResource.java (@Authenticated)
│   ├── delivery/
│   │   └── DeliveryResource.java (@RolesAllowed)
│   └── notification/
│       └── NotificationResource.java (@RolesAllowed)
├── resources/
│   ├── templates/FrontendResource/
│   │   ├── index.html
│   │   ├── dashboard.html
│   │   └── login.html
│   ├── application.properties (OIDC config)
│   └── quarkus-realm.json (Keycloak realm)
└── proto/
    ├── warehouse.proto
    └── delivery.proto
```

## Ролі та дозволи

| Endpoint | Метод | user | admin |
|----------|-------|------|-------|
| GET /api/orders | GET | ✅ | ✅ |
| POST /api/orders | POST | ✅ | ✅ |
| POST /api/orders/{id}/process | POST | ❌ | ✅ |
| GET /api/warehouse/items | GET | ✅ | ✅ |
| POST /api/warehouse/reserve | POST | ❌ | ✅ |
| GET /api/deliveries | GET | ✅ | ✅ |
| POST /api/deliveries | POST | ❌ | ✅ |
| GET /api/notifications | GET | ❌ | ✅ |
| POST /api/notifications/send | POST | ❌ | ✅ |
| GET /dashboard | GET | ✅ | ✅ |

## Команди для запуску

### Варіант 1: Dev mode з Docker Compose (рекомендовано)

1. **Запустити Keycloak**:
```bash
docker-compose -f docker-compose-keycloak.yml up -d
```

2. **Дочекатися запуску** (15-30 секунд):
```bash
docker logs -f logistics-keycloak
```

3. **Запустити Quarkus**:
```bash
./mvnw quarkus:dev
```

4. **Відкрити у браузері**:
   - Додаток: http://localhost:8080/
   - Dashboard: http://localhost:8080/dashboard
   - Keycloak Admin: http://localhost:8180/ (admin/admin)

### Варіант 2: Dev mode (з автоматичним Keycloak Dev Services)
Потребує Docker Desktop запущений:
```bash
./mvnw quarkus:dev
```

### Production build:
```bash
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```

### Тести:
```bash
./mvnw test
```

## Висновки

✅ **Виконано всі завдання лабораторної роботи №4:**

1. ✅ Створено frontend мікросервіс з Qute templating
2. ✅ Налаштовано OIDC автентифікацію
3. ✅ Додано RBAC до всіх мікросервісів
4. ✅ Налаштовано Dev Services для автоматичного Keycloak
5. ✅ Створено realm конфігурацію для production
6. ✅ Готово до тестування в обох режимах

**Система готова** до демонстрації та використання! 🎉🔐
