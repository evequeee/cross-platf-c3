# Notification Service - Logistics Microservice

Мікросервіс управління повідомленнями в логістичній системі.

## Функціональність
- Відправка Email, SMS, Push та In-App повідомлень
- Управління статусами повідомлень
- Автоматичні повторні спроби при невдачі
- Історія всіх повідомлень

## Запуск
```bash
./mvnw quarkus:dev
```

## Порт
- HTTP: 8084

## REST Endpoints
- GET /api/notifications - всі повідомлення
- GET /api/notifications/{id} - повідомлення за ID
- GET /api/notifications/recipient/{recipient} - за отримувачем
- GET /api/notifications/status/{status} - за статусом
- GET /api/notifications/type/{type} - за типом
- GET /api/notifications/pending - очікують відправки
- POST /api/notifications/send - відправити повідомлення
- POST /api/notifications - створити повідомлення
- POST /api/notifications/{id}/retry - повторити відправку
- PUT /api/notifications/{id}/status - оновити статус

## Типи повідомлень
- EMAIL - Email повідомлення
- SMS - SMS повідомлення
- PUSH - Push notification
- IN_APP - Повідомлення в додатку

## Статуси
- PENDING - Очікує відправки
- SENT - Успішно відправлено
- DELIVERED - Доставлено отримувачу
- FAILED - Не вдалося відправити
- CANCELLED - Скасовано

## Приклад використання
```bash
# Відправка email
curl -X POST "http://localhost:8084/api/notifications/send?recipient=user@example.com&type=EMAIL&subject=Test&message=Hello"

# Відправка SMS
curl -X POST "http://localhost:8084/api/notifications/send?recipient=+380501234567&type=SMS&subject=Alert&message=Your order is ready"
```
