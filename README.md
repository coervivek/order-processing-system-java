# E-commerce Order Management System

A distributed order processing system built with Spring Boot, implementing SAGA pattern for distributed transactions.

## Architecture

- **Clean Architecture**: Separation of concerns with layers (Controller, Service, Repository, Domain)
- **SAGA Pattern**: Distributed transaction management using Kafka
- **Microservices Ready**: Event-driven architecture for scalability
- **Docker Support**: Containerized deployment with Docker Compose

## Tech Stack

- Java 17
- Spring Boot 4.0.1
- PostgreSQL
- Apache Kafka
- Docker & Docker Compose
- Gradle

## Prerequisites

- Docker & Docker Compose
- Java 17 (for local development)

## Quick Start

### Using Docker Compose

```bash
# Start all services (PostgreSQL, Kafka, Application)
docker-compose up -d

# View logs
docker-compose logs -f oms-app

# Stop all services
docker-compose down
```

### Local Development

```bash
# Start infrastructure only
docker-compose up -d postgres kafka zookeeper

# Run application
./gradlew bootRun
```

## API Endpoints

### Create Order
```bash
POST /api/orders
Content-Type: application/json

{
  "items": [
    {
      "productName": "Laptop",
      "quantity": 1,
      "price": 999.99
    }
  ]
}
```

### Get Order by ID
```bash
GET /api/orders/{id}
```

### Get All Orders
```bash
GET /api/orders
GET /api/orders?status=PENDING
```

### Update Order Status
```bash
PATCH /api/orders/{id}/status?status=PROCESSING
```

### Cancel Order
```bash
POST /api/orders/{id}/cancel
```

## Order Status Flow

1. **PENDING** → Initial state when order is created
2. **PROCESSING** → Auto-updated every 5 minutes by background job
3. **SHIPPED** → Manually updated
4. **DELIVERED** → Final state
5. **CANCELLED** → Only from PENDING state

## SAGA Pattern Implementation

The system uses choreography-based SAGA pattern:

1. **Order Created** → Publishes `OrderCreatedEvent` to Kafka
2. **Compensation** → On cancellation, publishes compensation event
3. **Event Topics**:
   - `order-events`: Order lifecycle events
   - `order-compensation`: Rollback events

## Project Structure

```
src/main/java/com/epam/demo/omsjava/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── domain/          # Domain entities
├── dto/             # Data transfer objects
├── exception/       # Custom exceptions
├── job/             # Scheduled jobs
├── repository/      # Data access layer
├── saga/            # SAGA orchestration
└── service/         # Business logic
```

## Configuration

Key properties in `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/omsdb

# Kafka
spring.kafka.bootstrap-servers=localhost:9092

# Scheduler (every 5 minutes)
order.status.update.cron=0 */5 * * * *
```

## Testing

```bash
# Run tests
./gradlew test

# Build
./gradlew build
```

## Monitoring

- Application runs on: `http://localhost:8080`
- PostgreSQL: `localhost:5432`
- Kafka: `localhost:9092`
