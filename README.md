<<<<<<< HEAD
# code-with-quarkus

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/code-with-quarkus-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
# cross-platf-c3
=======
# Cross-Platform Programming Lab Work #3
## Microservices Architecture with Quarkus

This project implements a logistics management system using microservices architecture with REST and gRPC communication.

## 📁 Project Structure

```
cross-platf-c3-main/
├── microservices/
│   ├── order-service/          # Port 8081, gRPC 9001
│   ├── warehouse-service/      # Port 8082, gRPC 9002
│   ├── delivery-service/       # Port 8083, gRPC 9003
│   └── notification-service/   # Port 8084
├── start-all.ps1              # Start all services
├── test-services.ps1          # Test all endpoints
├── QUICKSTART.md              # Quick start guide
├── TESTING.md                 # Testing scenarios
└── LAB_REPORT.md              # Complete lab report
```

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Maven 3.9+
- PowerShell (for automation scripts)

### Starting All Services

**Option 1: Using PowerShell script**
```powershell
.\start-all.ps1
```

**Option 2: Manually start each service**
```powershell
cd microservices/order-service
./mvnw quarkus:dev

cd ../warehouse-service
./mvnw quarkus:dev

cd ../delivery-service
./mvnw quarkus:dev

cd ../notification-service
./mvnw quarkus:dev
```

### DevUI Access
- Order Service: http://localhost:8081/q/dev/
- Warehouse Service: http://localhost:8082/q/dev/
- Delivery Service: http://localhost:8083/q/dev/
- Notification Service: http://localhost:8084/q/dev/

## 🏗️ Architecture

### Service Communication

```
Order Service (Orchestrator)
    ├─[REST]──> Warehouse Service
    ├─[REST]──> Delivery Service
    ├─[REST]──> Notification Service
    ├─[gRPC]──> Warehouse Service
    └─[gRPC]──> Delivery Service
```

### Technologies
- **Framework**: Quarkus 3.26.4
- **Language**: Java 21
- **Communication**: REST (Jakarta REST), gRPC (Protocol Buffers 3)
- **Reactive**: Mutiny
- **Build Tool**: Maven

## 📚 Documentation

- **[QUICKSTART.md](QUICKSTART.md)** - Step-by-step startup guide
- **[TESTING.md](TESTING.md)** - Testing scenarios and DevUI usage
- **[LAB_REPORT.md](LAB_REPORT.md)** - Complete lab report with metrics

## 🧪 Testing

Run automated tests:
```powershell
.\test-services.ps1
```

Manual testing via REST:
```powershell
# Create order
Invoke-RestMethod -Uri "http://localhost:8081/api/orders" -Method POST -ContentType "application/json" -Body '{"customerId":1,"itemId":1,"quantity":2}'

# Check warehouse stock
Invoke-RestMethod -Uri "http://localhost:8082/api/warehouse/1"

# Track delivery
Invoke-RestMethod -Uri "http://localhost:8083/api/deliveries/1"

# Get notifications
Invoke-RestMethod -Uri "http://localhost:8084/api/notifications"
```

## 📊 Project Metrics

- **Total Services**: 4
- **REST Endpoints**: 32+
- **gRPC Methods**: 8
- **Lines of Code**: 3500+
- **Proto Files**: 2

## 🎓 Lab Work Requirements

✅ 4 independent microservices  
✅ REST API communication  
✅ gRPC communication  
✅ Fake repositories with realistic data  
✅ DevUI testing capability  
✅ Complete documentation  

## 📝 License

This is a educational project for cross-platform programming course.
>>>>>>> 96ddf3a (lab 4 fix, labs 5 and 6)
