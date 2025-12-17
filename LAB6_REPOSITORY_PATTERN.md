# Лабораторна робота №6 - Repository Pattern

## 📋 Огляд реалізації

### Архітектура мікросервісів

У проєкті реалізовано **два різні підходи** до роботи з базою даних:

#### 🟢 **Перша половина (2 сервіси) - Hibernate ORM Panache**
- **Order Service** 
- **Warehouse Service**

#### 🔵 **Друга половина (2 сервіси) - Traditional Repository Pattern з JPA EntityManager**
- **Delivery Service**
- **Notification Service**

## 🏗️ Repository Pattern - Концепція

**Repository Pattern** - це патерн проектування, який забезпечує абстракцію між бізнес-логікою та рівнем доступу до даних (Data Access Layer).

### Переваги Repository Pattern:
✅ Централізація логіки доступу до даних  
✅ Простота тестування (легко створити mock репозиторії)  
✅ Відокремлення бізнес-логіки від деталей збереження  
✅ Можливість легко змінити джерело даних  
✅ Повторне використання запитів  

## 📊 Порівняння підходів

### Panache Repository (Order & Warehouse Services)

**Приклад Entity:**
```java
@Entity
@Table(name = "orders")
public class Order extends PanacheEntity {
    // Немає потреби в @Id - PanacheEntity надає id
    @Column(name = "customer_name")
    private String customerName;
    // ...
}
```

**Приклад Repository:**
```java
@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order> {
    
    public List<Order> findAllOrders() {
        return listAll(); // Panache метод
    }
    
    public Optional<Order> findOrderById(Long id) {
        return findByIdOptional(id); // Panache метод
    }
}
```

**Переваги Panache:**
- ✅ Менше boilerplate коду
- ✅ Вбудовані методи (findAll, findById, persist, etc.)
- ✅ Active Record або Repository pattern на вибір
- ✅ Зручні методи для запитів: `find("name", name)`

### Traditional JPA Repository (Delivery & Notification Services)

**Приклад Entity:**
```java
@Entity
@Table(name = "deliveries")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Потрібно явно оголосити ID
    
    @Column(name = "order_id")
    private Long orderId;
    // ...
}
```

**Приклад Repository:**
```java
@ApplicationScoped
public class DeliveryRepository {
    
    @PersistenceContext
    EntityManager entityManager;
    
    public List<Delivery> findAllDeliveries() {
        return entityManager.createQuery(
            "SELECT d FROM Delivery d", Delivery.class)
            .getResultList();
    }
    
    @Transactional
    public Delivery saveDelivery(Delivery delivery) {
        if (delivery.getId() == null) {
            entityManager.persist(delivery);
            return delivery;
        } else {
            return entityManager.merge(delivery);
        }
    }
}
```

**Переваги Traditional JPA:**
- ✅ Повний контроль над запитами
- ✅ Стандарт JPA - працює з будь-яким JPA провайдером
- ✅ Явне керування транзакціями через @Transactional
- ✅ Можливість оптимізації складних запитів

## 📁 Структура коду

### Delivery Service (Traditional JPA)

```
delivery-service/
├── src/main/java/org/acme/logistics/delivery/
│   ├── Delivery.java                    # Entity з @Id @GeneratedValue
│   ├── DeliveryRepository.java          # Repository з EntityManager
│   ├── DeliveryResource.java            # REST endpoints
│   ├── DeliveryGrpcService.java         # gRPC service
│   └── DeliveryStatus.java              # Enum
└── pom.xml                              # quarkus-hibernate-orm (не panache)
```

### Notification Service (Traditional JPA)

```
notification-service/
├── src/main/java/org/acme/logistics/notification/
│   ├── Notification.java                # Entity з @Id @GeneratedValue
│   ├── NotificationRepository.java      # Repository з EntityManager
│   ├── NotificationResource.java        # REST endpoints
│   ├── NotificationService.java         # Business logic
│   └── Notification*.java               # Enums (Type, Status)
└── pom.xml                              # quarkus-hibernate-orm
```

## 🔧 Налаштування

### pom.xml (Traditional JPA)
```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-hibernate-orm</artifactId>  <!-- НЕ panache -->
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-jdbc-postgresql</artifactId>
</dependency>
```

### application.properties (однакові для обох підходів)
```properties
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=postgres
quarkus.datasource.password=postgres
quarkus.hibernate-orm.database.generation=drop-and-create
quarkus.hibernate-orm.sql-load-script=import.sql
```

## 🧪 Тестування Repository Pattern

### 1. Запуск PostgreSQL баз даних
```powershell
.\start-databases.ps1
```

### 2. Запуск мікросервісів
```powershell
.\start-all.ps1
```

### 3. Тестування Traditional JPA Repository (Delivery Service)

#### Створення (CREATE) через EntityManager
```powershell
$delivery = @{
    orderId = 999
    driverName = "Тест Водій"
    driverPhone = "+380501111111"
    vehicleNumber = "TT 9999 TT"
    pickupAddress = "Склад Тест"
    deliveryAddress = "вул. Тестова, 1"
    estimatedDeliveryTime = (Get-Date).AddHours(2).ToString("yyyy-MM-ddTHH:mm:ss")
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8083/api/deliveries" -Method POST -ContentType "application/json" -Body $delivery
```

#### Читання (READ)
```powershell
# Всі доставки
Invoke-RestMethod -Uri "http://localhost:8083/api/deliveries"

# По ID
Invoke-RestMethod -Uri "http://localhost:8083/api/deliveries/1"

# По статусу
Invoke-RestMethod -Uri "http://localhost:8083/api/deliveries/status/IN_TRANSIT"

# По tracking number
Invoke-RestMethod -Uri "http://localhost:8083/api/deliveries/tracking/TRK1734456001"
```

#### Оновлення (UPDATE) через EntityManager.merge()
```powershell
Invoke-RestMethod -Uri "http://localhost:8083/api/deliveries/1/status?status=DELIVERED" -Method PUT
Invoke-RestMethod -Uri "http://localhost:8083/api/deliveries/1/location?location=Київ, доставлено" -Method PUT
```

#### Видалення (DELETE) через EntityManager.remove()
```powershell
Invoke-RestMethod -Uri "http://localhost:8083/api/deliveries/5" -Method DELETE
```

### 4. Тестування Traditional JPA Repository (Notification Service)

#### Створення через EntityManager.persist()
```powershell
$notification = @{
    recipient = "test@example.com"
    type = "EMAIL"
    subject = "Тест Repository Pattern"
    message = "Перевірка збереження через EntityManager"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8084/api/notifications" -Method POST -ContentType "application/json" -Body $notification
```

#### Читання з TypedQuery
```powershell
# Всі повідомлення
Invoke-RestMethod -Uri "http://localhost:8084/api/notifications"

# По статусу
Invoke-RestMethod -Uri "http://localhost:8084/api/notifications/status/PENDING"

# По типу
Invoke-RestMethod -Uri "http://localhost:8084/api/notifications/type/EMAIL"

# По отримувачу
Invoke-RestMethod -Uri "http://localhost:8084/api/notifications/recipient/test@example.com"
```

#### Видалення
```powershell
Invoke-RestMethod -Uri "http://localhost:8084/api/notifications/5" -Method DELETE
```

## 🔍 Перевірка в базі даних

### Підключення до PostgreSQL
```powershell
# Delivery DB
docker exec -it delivery-postgres psql -U postgres -d delivery_db

# Notification DB
docker exec -it notification-postgres psql -U postgres -d notification_db
```

### SQL запити для перевірки
```sql
-- Delivery Service
SELECT * FROM deliveries;
SELECT * FROM deliveries WHERE status = 'IN_TRANSIT';

-- Notification Service
SELECT * FROM notifications;
SELECT * FROM notifications WHERE status = 'PENDING' AND retry_count < 3;

-- Вихід
\q
```

## 📊 Ключові відмінності

| Аспект | Panache | Traditional JPA |
|--------|---------|-----------------|
| **Entity base class** | `extends PanacheEntity` | Власний `@Id` field |
| **Repository** | `implements PanacheRepository<T>` | `@PersistenceContext EntityManager` |
| **Методи пошуку** | `findAll()`, `find()`, `list()` | `entityManager.createQuery()` |
| **Збереження** | `persist(entity)` | `entityManager.persist()` / `merge()` |
| **Транзакції** | Автоматичні через Panache | `@Transactional` на методах |
| **JPQL запити** | Спрощені: `find("name", value)` | Повні TypedQuery |
| **Boilerplate** | Мінімальний | Більше коду |
| **Гнучкість** | Середня | Висока |

## 🎯 Приклад методів Repository

### DeliveryRepository (Traditional JPA)
```java
@ApplicationScoped
public class DeliveryRepository {
    
    @PersistenceContext
    EntityManager entityManager;

    // CREATE/UPDATE
    @Transactional
    public Delivery saveDelivery(Delivery delivery) {
        if (delivery.getId() == null) {
            entityManager.persist(delivery);  // Нова entity
            return delivery;
        } else {
            return entityManager.merge(delivery);  // Оновлення існуючої
        }
    }

    // READ - одна запис
    public Optional<Delivery> findDeliveryById(Long id) {
        Delivery delivery = entityManager.find(Delivery.class, id);
        return Optional.ofNullable(delivery);
    }

    // READ - список з умовою
    public List<Delivery> findByStatus(DeliveryStatus status) {
        TypedQuery<Delivery> query = entityManager.createQuery(
            "SELECT d FROM Delivery d WHERE d.status = :status", 
            Delivery.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    // DELETE
    @Transactional
    public void deleteDeliveryById(Long id) {
        Delivery delivery = entityManager.find(Delivery.class, id);
        if (delivery != null) {
            entityManager.remove(delivery);
        }
    }
}
```

## ✅ Переваги реалізації

### Traditional JPA Repository Pattern:
1. **Явність**: Всі операції з БД явно описані в коді
2. **Контроль**: Повний контроль над JPQL запитами та транзакціями
3. **Переносимість**: Стандарт JPA - працює з Hibernate, EclipseLink, тощо
4. **Оптимізація**: Можливість написати оптимальні запити для складних сценаріїв
5. **Розуміння**: Легше зрозуміти, що відбувається "під капотом"

### Panache Repository Pattern:
1. **Простота**: Менше коду для типових операцій
2. **Швидкість розробки**: Готові методи з коробки
3. **Quarkus native**: Оптимізовано для Quarkus
4. **Active Record**: Можливість викликати методи безпосередньо на entity

## 📝 Висновок

Проєкт успішно демонструє **два різні підходи** до реалізації Repository Pattern:

✅ **Order & Warehouse Services**: Використовують **Hibernate ORM Panache** для швидкої розробки з мінімальним boilerplate  
✅ **Delivery & Notification Services**: Використовують **традиційний JPA EntityManager** для повного контролю  

Обидва підходи:
- Забезпечують CRUD операції
- Підтримують транзакції
- Працюють з PostgreSQL
- Зберігають дані між перезапусками
- Реалізують Repository Pattern

Вибір підходу залежить від:
- Складності запитів
- Необхідності контролю
- Швидкості розробки
- Досвіду команди
