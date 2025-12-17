# 🚀 Швидкий старт - Logistics Microservices

## Крок 1: Перевірка вимог

Переконайтесь, що у вас встановлено:
- ✅ Java 21 або новіше
- ✅ Maven 3.9+
- ✅ PowerShell (для Windows)

Перевірка:
```powershell
java -version
mvn -version
```

---

## Крок 2: Запуск сервісів

### Варіант А: Автоматичний запуск (рекомендовано)

```powershell
cd G:\programming\cross-platf-c3-main\microservices
.\start-all.ps1
```

Цей скрипт відкриє 4 вікна PowerShell, по одному для кожного сервісу.

### Варіант Б: Ручний запуск

Відкрийте 4 термінали та виконайте:

**Термінал 1 - Notification Service:**
```powershell
cd G:\programming\cross-platf-c3-main\microservices\notification-service
.\mvnw.cmd quarkus:dev
```
Чекайте поки побачите: `Listening on: http://0.0.0.0:8084`

**Термінал 2 - Warehouse Service:**
```powershell
cd G:\programming\cross-platf-c3-main\microservices\warehouse-service
.\mvnw.cmd quarkus:dev
```
Чекайте: `Listening on: http://0.0.0.0:8082`

**Термінал 3 - Delivery Service:**
```powershell
cd G:\programming\cross-platf-c3-main\microservices\delivery-service
.\mvnw.cmd quarkus:dev
```
Чекайте: `Listening on: http://0.0.0.0:8083`

**Термінал 4 - Order Service:**
```powershell
cd G:\programming\cross-platf-c3-main\microservices\order-service
.\mvnw.cmd quarkus:dev
```
Чекайте: `Listening on: http://0.0.0.0:8081`

---

## Крок 3: Перевірка доступності

Відкрийте браузер і перейдіть до DevUI кожного сервісу:

1. 📦 **Order Service**: http://localhost:8081/q/dev
2. 🏭 **Warehouse Service**: http://localhost:8082/q/dev
3. 🚚 **Delivery Service**: http://localhost:8083/q/dev
4. 📧 **Notification Service**: http://localhost:8084/q/dev

Якщо всі відкриваються - система готова! ✅

---

## Крок 4: Швидкий тест

Запустіть автоматичний тест:
```powershell
cd G:\programming\cross-platf-c3-main\microservices
.\test-services.ps1
```

Або виконайте простий запит вручну:
```powershell
# Переглянути всі замовлення
Invoke-RestMethod http://localhost:8081/api/orders

# Переглянути товари на складі
Invoke-RestMethod http://localhost:8082/api/warehouse

# Переглянути доставки
Invoke-RestMethod http://localhost:8083/api/delivery

# Переглянути повідомлення
Invoke-RestMethod http://localhost:8084/api/notifications
```

---

## Крок 5: Створення тестового замовлення

```powershell
$order = @{
    customerName = "Тест Тестовий"
    customerEmail = "test@example.com"
    customerPhone = "+380501234567"
    deliveryAddress = "вул. Тестова, 1, Київ"
    totalPrice = 27500
    items = @(
        @{
            productId = 101
            productName = "Ноутбук Lenovo ThinkPad"
            quantity = 1
            pricePerUnit = 25000
        },
        @{
            productId = 102
            productName = "Миша Logitech MX Master"
            quantity = 1
            pricePerUnit = 2500
        }
    )
} | ConvertTo-Json -Depth 10

$newOrder = Invoke-RestMethod -Method Post `
    -Uri "http://localhost:8081/api/orders" `
    -Body $order `
    -ContentType "application/json"

Write-Host "✅ Замовлення створено з ID: $($newOrder.id)" -ForegroundColor Green
```

---

## Крок 6: Обробка замовлення (тестування інтеграції)

```powershell
# Замініть 4 на ID вашого замовлення з попереднього кроку
Invoke-RestMethod -Method Post "http://localhost:8081/api/orders/4/process"
```

**Що відбудеться:**
1. 🔍 Перевірка наявності товарів на складі
2. 📦 Резервування товарів
3. 🚚 Створення доставки
4. 📧 Відправка email клієнту

Перевірте результат:
```powershell
# Оновлений статус замовлення
Invoke-RestMethod "http://localhost:8081/api/orders/4"

# Створена доставка
Invoke-RestMethod "http://localhost:8083/api/delivery/order/4"

# Відправлені повідомлення
Invoke-RestMethod "http://localhost:8084/api/notifications"
```

---

## Крок 7: Тестування gRPC (опціонально)

Order Service має спеціальні REST ендпоінти для демонстрації gRPC:

```powershell
# Перевірка складу через gRPC
Invoke-RestMethod "http://localhost:8081/api/orders/grpc/check-stock/101?quantity=1"

# Резервування через gRPC
Invoke-RestMethod -Method Post "http://localhost:8081/api/orders/grpc/reserve-stock/102?quantity=1"

# Створення доставки через gRPC
Invoke-RestMethod -Method Post "http://localhost:8081/api/orders/grpc/create-delivery?orderId=1&address=Київ"
```

---

## 🎯 Основні ендпоінти

### Order Service (8081)
```
GET    /api/orders              - всі замовлення
POST   /api/orders              - створити замовлення
POST   /api/orders/{id}/process - обробити замовлення
GET    /api/orders/grpc/*       - gRPC демо ендпоінти
```

### Warehouse Service (8082)
```
GET    /api/warehouse                      - всі товари
GET    /api/warehouse/check/{id}?quantity= - перевірка наявності
POST   /api/warehouse/reserve/{id}?quantity= - резервування
```

### Delivery Service (8083)
```
GET    /api/delivery                    - всі доставки
GET    /api/delivery/track/{tracking}   - відстеження
POST   /api/delivery?orderId=&address=  - створити доставку
```

### Notification Service (8084)
```
GET    /api/notifications              - всі повідомлення
POST   /api/notifications/send?params  - відправити
```

---

## 🛑 Зупинка сервісів

Просто закрийте вікна терміналів або натисніть `Ctrl+C` в кожному терміналі.

---

## 📚 Наступні кроки

1. Детальні інструкції: `TESTING.md`
2. Архітектура: `README.md`
3. Окремі сервіси: кожен має свій README в директорії

---

## ❓ Проблеми?

**Порт зайнятий:**
```powershell
# Знайти процес
Get-NetTCPConnection -LocalPort 8081

# Зупинити
Stop-Process -Id <PID>
```

**Maven помилка:**
```powershell
# Очистити та перезібрати
.\mvnw.cmd clean install -DskipTests
```

**gRPC не працює:**
- Перевірте що порти 9001-9003 вільні
- Перезапустіть сервіси

---

## ✅ Успіх!

Якщо ви дійшли сюди і все працює - вітаємо! 🎉

Ваша мікросервісна система готова до використання та тестування.
