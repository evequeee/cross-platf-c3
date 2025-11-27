# Production Deployment Guide

## 1. Build Production Package

```powershell
.\mvnw.cmd clean package -DskipTests
```

Це створить `target/quarkus-app/quarkus-run.jar`

## 2. Start Production Keycloak

```powershell
docker-compose -f docker-compose-keycloak-prod.yml up -d
```

Почекайте ~60 секунд поки Keycloak повністю запуститься.

Перевірте статус:
```powershell
docker-compose -f docker-compose-keycloak-prod.yml ps
docker logs logistics-keycloak-prod --tail 50
```

## 3. Run Application in Production Mode

```powershell
java -jar target/quarkus-app/quarkus-run.jar
```

## 4. Verify Production Deployment

### Перевірка Keycloak
- **URL**: http://localhost:8180/
- **Admin Console**: http://localhost:8180/admin
- **Credentials**: admin / AdminStrongPassword123! (або з вашого .env)

### Перевірка Application
- **Frontend**: http://localhost:8080/
- **Dashboard**: http://localhost:8080/dashboard (потребує логін)
- **Login Info**: http://localhost:8080/login

### Тестові користувачі
- **alice** / **alice** (роль: user)
- **admin** / **admin** (роль: admin)

## 5. Health Checks

```powershell
# Application health
Invoke-WebRequest -Uri http://localhost:8080/q/health

# Keycloak health
Invoke-WebRequest -Uri http://localhost:8180/health/ready
```

## 6. API Testing with Authentication

### Get Access Token
```powershell
$response = Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8180/realms/quarkus/protocol/openid-connect/token" `
  -ContentType "application/x-www-form-urlencoded" `
  -Body @{
    client_id = "logistics-app"
    client_secret = "secret"
    username = "alice"
    password = "alice"
    grant_type = "password"
  }

$token = $response.access_token
```

### Test Protected Endpoint
```powershell
Invoke-RestMethod -Method Get `
  -Uri "http://localhost:8080/api/orders" `
  -Headers @{Authorization = "Bearer $token"}
```

## 7. Stop Production Services

```powershell
# Stop Quarkus (Ctrl+C in terminal)

# Stop Keycloak
docker-compose -f docker-compose-keycloak-prod.yml down

# Stop and remove volumes (WARNING: deletes all data)
docker-compose -f docker-compose-keycloak-prod.yml down -v
```

## Environment Variables (Optional)

Create `.env` file:
```env
KC_ADMIN_PASSWORD=YourSecureAdminPassword123!
KC_DB_PASSWORD=YourSecureDBPassword123!
KC_HOSTNAME=yourdomain.com
```

## Logs

```powershell
# Application logs (in console where java -jar runs)

# Keycloak logs
docker logs -f logistics-keycloak-prod

# Postgres logs
docker logs -f keycloak-postgres
```

## Troubleshooting

### Keycloak won't start
```powershell
docker-compose -f docker-compose-keycloak-prod.yml logs keycloak
```

### Application can't connect to Keycloak
- Check if Keycloak is ready: http://localhost:8180/health/ready
- Verify realm imported: http://localhost:8180/realms/quarkus/.well-known/openid-configuration

### Permission denied errors
- Verify user roles in Keycloak Admin Console
- Check token contains correct roles: decode JWT at https://jwt.io

## Performance Tips

1. **Use native image** for faster startup:
   ```powershell
   .\mvnw.cmd package -Pnative
   .\target\code-with-quarkus-1.0.0-SNAPSHOT-runner.exe
   ```

2. **Increase JVM memory** for production:
   ```powershell
   java -Xms512m -Xmx2g -jar target/quarkus-app/quarkus-run.jar
   ```

3. **Enable caching** in application.properties

## Security Checklist

- ✅ Changed default admin password
- ✅ Used strong encryption secret
- ✅ Enabled HTTPS (configure KC_HOSTNAME_STRICT_HTTPS=true)
- ✅ Database credentials in environment variables
- ✅ Firewall rules for ports 8080, 8180
- ✅ Regular backups of Keycloak database
