# Frontend Service

Головний мікросервіс з графічним інтерфейсом для системи логістики.

## Запуск
```bash
./mvnw quarkus:dev
```

## URL
- Головна: http://localhost:8080
- Dashboard: http://localhost:8080/dashboard
- DevUI: http://localhost:8080/q/dev/

## Функції
- Веб-інтерфейс для користувачів
- Інтеграція з усіма мікросервісами через REST
- OIDC авторизація через Keycloak
