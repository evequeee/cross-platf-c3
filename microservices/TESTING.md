# 🧪 Тестування мікросервісів через Quarkus DevUI

Це докладна інструкція для тестування міжсервісної комунікації через Quarkus Dev UI та REST API.

## 📋 Перед початком

1. **Запустіть всі сервіси:**
```powershell
cd microservices
.\start-all.ps1
```

Або кожен окремо в різних терміналах:
```powershell
cd order-service && .\mvnw.cmd quarkus:dev
cd warehouse-service && .\mvnw.cmd quarkus:dev
cd delivery-service && .\mvnw.cmd quarkus:dev
cd notification-service && .\mvnw.cmd quarkus:dev
```

2. **Перевірте доступність:**
- Order Service: http://localhost:8081/q/dev
- Warehouse Service: http://localhost:8082/q/dev
- Delivery Service: http://localhost:8083/q/dev
- Notification Service: http://localhost:8084/q/dev

---

## 🔬 Тестові сценарії

### Сценарій 1: Перевірка фейкових даних

#### 1.1 Warehouse Service - Перегляд товарів
```powershell
# Всі товари
Invoke-RestMethod http://localhost:8082/api/warehouse

# Конкретний товар
Invoke-RestMethod http://localhost:8082/api/warehouse/product/101

# Товари по категорії
Invoke-RestMethod http://localhost:8082/api/warehouse/category/Електроніка
```

**Очікуваний результат:** Список товарів з наявністю, цінами, локацією на складі.

#### 1.2 Order Service - Існуючі замовлення
```powershell
Invoke-RestMethod http://localhost:8081/api/orders
```

**Очікуваний результат:** 3 тестові замовлення з різними статусами.

#### 1.3 Delivery Service - Активні доставки
```powershell
Invoke-RestMethod http://localhost:8083/api/delivery
```

**Очікуваний результат:** 4 доставки з різними статусами та tracking numbers.

#### 1.4 Notification Service - Історія повідомлень
```powershell
Invoke-RestMethod http://localhost:8084/api/notifications
```

**Очікуваний результат:** 5 повідомлень різних типів (EMAIL, SMS, PUSH).

---

### Сценарій 2: REST комунікація між сервісами

#### 2.1 Перевірка наявності товару (Order → Warehouse)
```powershell
# Перевірити чи є 1 ноутбук
Invoke-RestMethod "http://localhost:8082/api/warehouse/check/101?quantity=1"
# True

# Перевірити чи є 100 ноутбуків (перевищує наявність)
Invoke-RestMethod "http://localhost:8082/api/warehouse/check/101?quantity=100"
# False
```

#### 2.2 Резервування товару
```powershell
# Зарезервувати 2 миші
Invoke-RestMethod -Method Post "http://localhost:8082/api/warehouse/reserve/102?quantity=2"
```

**Очікуваний результат:** `{"message": "Товар успішно зарезервовано"}`

#### 2.3 Створення повідомлення
```powershell
$params = @{
    recipient = "test@example.com"
    type = "EMAIL"
    subject = "Тестове повідомлення"
    message = "Це тест REST комунікації"
}
Invoke-RestMethod -Method Post "http://localhost:8084/api/notifications/send" -Body $params
```

#### 2.4 Створення доставки
```powershell
$params = @{
    orderId = 1
    address = "вул. Тестова, 123, Київ"
}
Invoke-RestMethod -Method Post "http://localhost:8083/api/delivery" -Body $params
```

---

### Сценарій 3: Комплексна обробка замовлення

#### 3.1 Створення нового замовлення
```powershell
$order = @{
    customerName = "Олексій Тестовий"
    customerEmail = "oleksiy@test.com"
    customerPhone = "+380501234567"
    deliveryAddress = "вул. Київська, 10, Львів"
    totalPrice = 30000
    items = @(
        @{
            productId = 201
            productName = "Смартфон Samsung Galaxy S23"
            quantity = 1
            pricePerUnit = 28000
        },
        @{
            productId = 203
            productName = "Навушники Samsung Buds"
            quantity = 1
            pricePerUnit = 3500
        }
    )
} | ConvertTo-Json -Depth 10

$newOrder = Invoke-RestMethod -Method Post -Uri "http://localhost:8081/api/orders" `
    -Body $order -ContentType "application/json"

Write-Host "Створено замовлення #$($newOrder.id)"
```

#### 3.2 Обробка замовлення (запускає весь ланцюг)
```powershell
# Використайте ID створеного замовлення
Invoke-RestMethod -Method Post "http://localhost:8081/api/orders/$($newOrder.id)/process"
```

**Що відбувається під капотом:**
1. ✅ Order Service → Warehouse Service: перевірка наявності товарів
2. ✅ Order Service → Warehouse Service: резервування товарів
3. ✅ Order Service → Delivery Service: створення доставки
4. ✅ Order Service → Notification Service: відправка email клієнту

#### 3.3 Перевірка результатів
```powershell
# Оновлений статус замовлення
Invoke-RestMethod "http://localhost:8081/api/orders/$($newOrder.id)"

# Створена доставка
Invoke-RestMethod "http://localhost:8083/api/delivery/order/$($newOrder.id)"

# Відправлені повідомлення
Invoke-RestMethod "http://localhost:8084/api/notifications/recipient/oleksiy@test.com"
```

---

### Сценарій 4: gRPC комунікація

#### 4.1 Використання gRPC через REST ендпоінти

Order Service має спеціальні ендпоінти для демонстрації gRPC:

```powershell
# Перевірка складу через gRPC
Invoke-RestMethod "http://localhost:8081/api/orders/grpc/check-stock/101?quantity=1"

# Резервування через gRPC
Invoke-RestMethod -Method Post "http://localhost:8081/api/orders/grpc/reserve-stock/102?quantity=1"

# Створення доставки через gRPC
Invoke-RestMethod -Method Post "http://localhost:8081/api/orders/grpc/create-delivery?orderId=1&address=Test"
```

#### 4.2 Перегляд gRPC сервісів у DevUI

1. Відкрийте **Warehouse Service** DevUI: http://localhost:8082/q/dev
2. Знайдіть розділ **gRPC** у лівому меню
3. Ви побачите:
   - `WarehouseService` з методами:
     - `CheckStock`
     - `ReserveItems`
     - `ReleaseItems`
     - `GetItemInfo`

4. Відкрийте **Delivery Service** DevUI: http://localhost:8083/q/dev
5. У розділі gRPC:
   - `DeliveryService` з методами:
     - `CreateDelivery`
     - `UpdateDeliveryStatus`
     - `GetDeliveryByOrder`
     - `TrackDelivery`

---

### Сценарій 5: Відстеження доставки

#### 5.1 Отримання tracking number
```powershell
$delivery = Invoke-RestMethod "http://localhost:8083/api/delivery/1"
$trackingNumber = $delivery.trackingNumber
Write-Host "Tracking: $trackingNumber"
```

#### 5.2 Відстеження
```powershell
Invoke-RestMethod "http://localhost:8083/api/delivery/track/$trackingNumber"
```

#### 5.3 Оновлення статусу доставки
```powershell
# Змінити статус на IN_TRANSIT
Invoke-RestMethod -Method Put "http://localhost:8083/api/delivery/1/status?status=IN_TRANSIT"

# Оновити локацію
Invoke-RestMethod -Method Put "http://localhost:8083/api/delivery/1/location?location=Київ,%20вул.%20Хрещатик"
```

**Очікуваний результат:** Автоматична відправка SMS клієнту через Notification Service.

---

### Сценарій 6: Тестування DevUI функцій

#### 6.1 RESTEasy Reactive (у кожному сервісі)

1. Відкрийте DevUI будь-якого сервісу
2. Знайдіть **Endpoints** або **REST Resources**
3. Побачите список всіх REST ендпоінтів
4. Можна виконувати запити прямо з UI

#### 6.2 Configuration (application.properties)

1. У DevUI → **Configuration**
2. Перегляньте всі налаштування:
   - HTTP порти
   - gRPC порти
   - REST Client URLs
   - Logging levels

#### 6.3 ArC (CDI Container)

1. У DevUI → **ArC Beans**
2. Знайдіть:
   - `OrderService`
   - `WarehouseRepository`
   - `DeliveryService`
   - `NotificationService`
3. Перегляньте їх залежності та lifecycle

---

## 📊 Автоматичний тест-скрипт

Запустіть автоматичний тест:
```powershell
cd microservices
.\test-services.ps1
```

Цей скрипт:
1. ✅ Перевіряє доступність всіх сервісів
2. ✅ Тестує warehouse API
3. ✅ Створює тестове замовлення
4. ✅ Обробляє замовлення (тестує всі інтеграції)
5. ✅ Показує результати

---

## 🔍 Перевірка логів

У кожному терміналі з запущеним сервісом ви побачите:

**Order Service:**
```
INFO  [org.acm.log.ord.OrderService] Створення нового замовлення для клієнта: ...
INFO  [org.acm.log.ord.OrderService] Перевірка наявності товарів на складі
INFO  [org.acm.log.ord.OrderService] Резервування товарів на складі
INFO  [org.acm.log.ord.OrderService] Створення доставки
```

**Warehouse Service:**
```
INFO  [org.acm.log.war.grpc.WarehouseGrpcService] gRPC: Перевірка наявності продукту #101
INFO  [org.acm.log.war.grpc.WarehouseGrpcService] gRPC: Резервування продукту #101
```

**Delivery Service:**
```
INFO  [org.acm.log.del.DeliveryService] Створення доставки для замовлення #...
INFO  [org.acm.log.del.grpc.DeliveryGrpcService] gRPC: Створення доставки для замовлення #...
```

**Notification Service:**
```
INFO  [org.acm.log.not.NotificationService] Відправка EMAIL повідомлення до: ...
INFO  [org.acm.log.not.NotificationService] 📧 Email відправлено на: ...
```

---

## ✅ Критерії успішного тестування

- [ ] Всі 4 сервіси запускаються без помилок
- [ ] DevUI доступний для кожного сервісу
- [ ] REST ендпоінти повертають фейкові дані
- [ ] Створення замовлення працює
- [ ] Обробка замовлення запускає всі інтеграції
- [ ] gRPC методи викликаються (видно в логах)
- [ ] Warehouse резервує товари
- [ ] Delivery створює доставку
- [ ] Notification відправляє повідомлення

---

## 🐛 Troubleshooting

**Проблема:** Сервіс не стартує
```
Помилка: Port 8081 is already in use
```
**Рішення:** Змініть порт або закрийте процес:
```powershell
Get-Process -Id (Get-NetTCPConnection -LocalPort 8081).OwningProcess | Stop-Process
```

**Проблема:** REST Client timeout
```
Помилка: Connection refused
```
**Рішення:** Переконайтесь що сервіс запущений і порт правильний.

**Проблема:** gRPC помилка
```
Помилка: UNAVAILABLE: io exception
```
**Рішення:** Перевірте що gRPC порт доступний (9001-9003).

---

## 📚 Корисні посилання

- [Quarkus REST Guide](https://quarkus.io/guides/rest)
- [Quarkus gRPC Guide](https://quarkus.io/guides/grpc-getting-started)
- [Quarkus REST Client Guide](https://quarkus.io/guides/rest-client)
- [Quarkus Dev UI](https://quarkus.io/guides/dev-ui)
