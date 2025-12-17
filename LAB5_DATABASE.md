# Лабораторна робота №5 - База даних PostgreSQL

## 📋 Етапи виконання

### 1. Підготовка бази даних

#### Встановлення та запуск PostgreSQL

**Варіант 1: Docker (рекомендовано)**
```powershell
# Запустіть Docker Desktop
# Потім виконайте:
.\start-databases.ps1
```

**Варіант 2: Локальна установка PostgreSQL**
1. Завантажте PostgreSQL 16 з https://www.postgresql.org/download/
2. Встановіть з паролем `postgres` для користувача `postgres`
3. Створіть 4 бази даних:
```sql
CREATE DATABASE order_db;
CREATE DATABASE warehouse_db;
CREATE DATABASE delivery_db;
CREATE DATABASE notification_db;
```

#### Налаштування портів
Кожен мікросервіс використовує окрему базу даних:
- **Order Service**: `localhost:5432/order_db`
- **Warehouse Service**: `localhost:5433/warehouse_db` (Docker) або `5432` (локально)
- **Delivery Service**: `localhost:5434/delivery_db` (Docker) або `5432` (локально)
- **Notification Service**: `localhost:5435/notification_db` (Docker) або `5432` (локально)

### 2. Конфігурація Hibernate ORM Panache

Кожен мікросервіс налаштовано з:
- **Hibernate ORM Panache** - спрощений доступ до БД
- **PostgreSQL JDBC Driver** - з'єднання з БД
- **JPA Entities** - сутності з анотаціями `@Entity`, `@Table`
- **Panache Repositories** - репозиторії з `PanacheRepository<T>`

#### Конфігурація (`application.properties`):
```properties
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=postgres
quarkus.datasource.password=postgres
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/order_db
quarkus.datasource.jdbc.max-size=16

quarkus.hibernate-orm.database.generation=drop-and-create
quarkus.hibernate-orm.log.sql=true
quarkus.hibernate-orm.sql-load-script=import.sql
```

### 3. Структура сутностей

#### Order Service
- **Order** (extends PanacheEntity)
  - Поля: customerName, customerEmail, customerPhone, status, deliveryAddress, totalPrice
  - OneToMany зв'язок з OrderItem
  - Статуси: CREATED, VALIDATED, WAREHOUSE_RESERVED, IN_DELIVERY, DELIVERED, etc.

- **OrderItem** (extends PanacheEntity)
  - Поля: productId, productName, quantity, pricePerUnit, totalPrice
  - ManyToOne зв'язок з Order

#### Warehouse Service
- **WarehouseItem** (extends PanacheEntity)
  - Поля: productId (unique), productName, category, quantityAvailable, quantityReserved
  - Методи: hasEnoughStock(), reserveStock(), releaseStock(), addStock()

#### Delivery Service
- **Delivery** (extends PanacheEntity)
  - Поля: orderId (unique), driverName, vehicleNumber, status, trackingNumber (unique)
  - Автоматична генерація trackingNumber через @PrePersist

#### Notification Service
- **Notification** (extends PanacheEntity)
  - Поля: recipient, type (EMAIL/SMS/PUSH), subject, message, status
  - Підтримка повторних спроб (retryCount)

### 4. Тестування операцій з БД

#### 4.1 Запуск мікросервісів
```powershell
# Запустіть бази даних (якщо використовуєте Docker)
.\start-databases.ps1

# Запустіть всі мікросервіси
.\start-all.ps1
```

#### 4.2 Тестування збереження (CREATE)

**Створити замовлення:**
```powershell
$body = @{
    customerName = "Тест Користувач"
    customerEmail = "test@example.com"
    customerPhone = "+380501234567"
    deliveryAddress = "вул. Тестова, 1, Київ"
    items = @(
        @{
            productId = 101
            productName = "Ноутбук"
            quantity = 1
            pricePerUnit = 25000.0
        }
    )
    totalPrice = 25000.0
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8081/api/orders" -Method POST -ContentType "application/json" -Body $body
```

**Створити повідомлення:**
```powershell
$notification = @{
    recipient = "test@example.com"
    type = "EMAIL"
    subject = "Тестове повідомлення"
    message = "Це тест збереження в БД"
    status = "PENDING"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8084/api/notifications" -Method POST -ContentType "application/json" -Body $notification
```

#### 4.3 Тестування читання (READ)

**Отримати всі замовлення:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8081/api/orders"
```

**Отримати замовлення по ID:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8081/api/orders/1"
```

**Отримати товари на складі:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8082/api/warehouse"
```

**Фільтрація по категорії:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8082/api/warehouse/category/Електроніка"
```

#### 4.4 Тестування оновлення (UPDATE)

**Оновити статус замовлення:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8081/api/orders/1/status?status=DELIVERED" -Method PUT
```

**Оновити місцезнаходження доставки:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8083/api/deliveries/1/location?location=Київ, вул. Хрещатик" -Method PUT
```

**Резервувати товар на складі:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8082/api/warehouse/reserve?productId=101&quantity=2" -Method POST
```

#### 4.5 Тестування видалення (DELETE)

**Видалити повідомлення:**
```powershell
Invoke-RestMethod -Uri "http://localhost:8084/api/notifications/1" -Method DELETE
```

**Перевірити видалення:**
```powershell
# Має повернути помилку 404
Invoke-RestMethod -Uri "http://localhost:8084/api/notifications/1"
```

### 5. Перевірка даних безпосередньо в БД

#### Підключення до PostgreSQL (Docker):
```powershell
# Order DB
docker exec -it order-postgres psql -U postgres -d order_db

# Warehouse DB
docker exec -it warehouse-postgres psql -U postgres -d warehouse_db
```

#### SQL запити:
```sql
-- Показати всі замовлення
SELECT * FROM orders;

-- Показати елементи замовлень
SELECT * FROM order_items;

-- Показати товари на складі
SELECT * FROM warehouse_items;

-- Показати доставки
SELECT * FROM deliveries;

-- Показати повідомлення
SELECT * FROM notifications;

-- Вихід
\q
```

### 6. Перевірка через Quarkus DevUI

1. Відкрийте DevUI кожного сервісу:
   - Order: http://localhost:8081/q/dev/
   - Warehouse: http://localhost:8082/q/dev/
   - Delivery: http://localhost:8083/q/dev/
   - Notification: http://localhost:8084/q/dev/

2. Перейдіть до розділу **"Hibernate ORM"**
3. Перегляньте:
   - Створені таблиці
   - SQL запити
   - Статистику підключень

### 7. Тестовий сценарій: Повний життєвий цикл замовлення

```powershell
# 1. Перевірити наявність товару
$stock = Invoke-RestMethod -Uri "http://localhost:8082/api/warehouse/product/101"
Write-Host "Доступно товару: $($stock.quantityAvailable - $stock.quantityReserved)"

# 2. Створити замовлення
$order = @{
    customerName = "Іван Петров"
    customerEmail = "ivan@example.com"
    customerPhone = "+380501111111"
    deliveryAddress = "вул. Головна, 10, Київ"
    items = @(@{productId = 101; productName = "Ноутбук"; quantity = 1; pricePerUnit = 25000.0})
    totalPrice = 25000.0
} | ConvertTo-Json

$newOrder = Invoke-RestMethod -Uri "http://localhost:8081/api/orders" -Method POST -ContentType "application/json" -Body $order
Write-Host "Створено замовлення ID: $($newOrder.id)"

# 3. Резервувати товар
Invoke-RestMethod -Uri "http://localhost:8082/api/warehouse/reserve?productId=101&quantity=1" -Method POST

# 4. Перевірити оновлення на складі
$updatedStock = Invoke-RestMethod -Uri "http://localhost:8082/api/warehouse/product/101"
Write-Host "Зарезервовано: $($updatedStock.quantityReserved)"

# 5. Створити доставку
$delivery = @{
    orderId = $newOrder.id
    driverName = "Петро Водій"
    driverPhone = "+380502222222"
    vehicleNumber = "AA 1234 BB"
    pickupAddress = "Склад, вул. Складська, 1"
    deliveryAddress = $newOrder.deliveryAddress
} | ConvertTo-Json

$newDelivery = Invoke-RestMethod -Uri "http://localhost:8083/api/deliveries" -Method POST -ContentType "application/json" -Body $delivery
Write-Host "Створено доставку: $($newDelivery.trackingNumber)"

# 6. Відправити повідомлення
Invoke-RestMethod -Uri "http://localhost:8084/api/notifications/send?recipient=$($newOrder.customerEmail)&type=EMAIL&subject=Замовлення створено&message=Ваше замовлення #$($newOrder.id) прийняте" -Method POST

# 7. Оновити статуси
Invoke-RestMethod -Uri "http://localhost:8081/api/orders/$($newOrder.id)/status?status=IN_DELIVERY" -Method PUT
Invoke-RestMethod -Uri "http://localhost:8083/api/deliveries/$($newDelivery.id)/status?status=IN_TRANSIT" -Method PUT

# 8. Завершити доставку
Invoke-RestMethod -Uri "http://localhost:8083/api/deliveries/$($newDelivery.id)/status?status=DELIVERED" -Method PUT
Invoke-RestMethod -Uri "http://localhost:8081/api/orders/$($newOrder.id)/status?status=DELIVERED" -Method PUT

# 9. Видалити товар зі складу
Invoke-RestMethod -Uri "http://localhost:8082/api/warehouse/remove?productId=101&quantity=1" -Method POST

Write-Host "`n✅ Тест завершено успішно!"
```

### 8. Очікувані результати

✅ **Створення (CREATE):**
- Дані зберігаються в PostgreSQL
- Автоматична генерація ID через `PanacheEntity`
- Каскадне збереження (Order → OrderItems)

✅ **Читання (READ):**
- `findAll()` повертає всі записи
- `findById()` повертає конкретний запис
- Фільтрація через кастомні методи

✅ **Оновлення (UPDATE):**
- Зміни автоматично зберігаються через Panache
- `@PreUpdate` викликається перед оновленням
- Транзакційність забезпечена Quarkus

✅ **Видалення (DELETE):**
- `deleteById()` видаляє запис
- Каскадне видалення (Order → OrderItems)
- Orphan removal для дочірніх сутностей

### 9. Troubleshooting

**Проблема:** Не можу підключитися до БД
- Перевірте чи запущений Docker Desktop
- Виконайте: `docker ps` для перевірки контейнерів
- Перевірте порти в `application.properties`

**Проблема:** Помилка "relation does not exist"
- Hibernate автоматично створює таблиці при старті
- Перевірте `quarkus.hibernate-orm.database.generation=drop-and-create`
- Перезапустіть сервіс

**Проблема:** Дублювання даних при рестарті
- Використовується `drop-and-create` - таблиці очищаються
- Для production використовуйте `update` або міграції (Flyway/Liquibase)

## 📊 Метрики після впровадження БД

- **Зберігання даних**: Постійне (persistent) замість in-memory
- **Transactional operations**: Підтримка ACID
- **Relationships**: OneToMany, ManyToOne через JPA
- **Query optimization**: Panache надає оптимізовані запити
- **Data validation**: JPA constraints (@Column, @Unique, etc.)

## 🎯 Висновок

Всі 4 мікросервіси успішно інтегровані з PostgreSQL через Hibernate ORM Panache. Кожен сервіс має власну базу даних, підтримує CRUD операції та забезпечує збереження даних між перезапусками.
