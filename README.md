# E-commerce Order Management System

A production-ready distributed order processing system built with Spring Boot, implementing SAGA pattern for distributed transactions, JWT security, and event-driven architecture.

## Table of Contents
- [Features](#features)
- [Architecture](#architecture)
- [Design Patterns](#design-patterns)
- [Tech Stack](#tech-stack)
- [Quick Start](#quick-start)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [SAGA Pattern](#saga-pattern)
- [Security](#security)
- [Configuration](#configuration)

## Features

### Core Features
- **Order Management**: Create, retrieve, update, and cancel orders with UUID-based identification
- **Order Status Tracking**: PENDING → PROCESSING → SHIPPED → DELIVERED → CANCELLED
- **Automatic Status Updates**: Background job updates PENDING orders to PROCESSING every 5 minutes
- **Order Cancellation**: Only PENDING orders can be cancelled with compensation flow
- **Status Filtering**: Query orders by status (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
- **Order Total Calculation**: Automatic calculation of order total from items

### Technical Features
- **SAGA Pattern**: Choreography-based distributed transaction management
- **Event-Driven Architecture**: Kafka-based event publishing and consumption
- **Idempotency**: Duplicate event detection and handling
- **Timeout Monitoring**: Automatic detection and compensation of stuck SAGAs
- **JWT Security**: Optional token-based authentication (disabled by default)
- **API Documentation**: Interactive Swagger UI and Postman collection
- **Docker Support**: Complete containerized deployment with Docker Compose

## Architecture

### Clean Architecture Layers

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ OrderController│ │ AuthController│ │ GlobalException│ │
│  │   (REST API)  │ │   (JWT Auth)  │ │    Handler     │ │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                     Business Layer                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │ OrderService │  │ SAGA Orchestr│ │ Event Listener│  │
│  │              │  │    ator      │ │               │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                   Persistence Layer                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │OrderRepository│ │SagaRepository│ │EventRepository│  │
│  │   (JPA/DB)   │ │   (JPA/DB)   │ │   (JPA/DB)   │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                   Infrastructure Layer                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │  PostgreSQL  │  │ Apache Kafka │  │    Docker    │  │
│  │   Database   │  │Event Streaming│ │  Containers  │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### Component Interaction

```
Client Request → OrderController → OrderService → OrderRepository → PostgreSQL
                                         ↓
                                  SAGA Orchestrator
                                         ↓
                                   Kafka Producer
                                         ↓
                              Kafka Topics (order-events)
                                         ↓
                                   Kafka Consumer
                                         ↓
                                  Event Listener
                                         ↓
                              Process/Compensate Order
```

## Design Patterns

### 1. SAGA Pattern (Choreography-Based)
**Purpose**: Manage distributed transactions across microservices

**Implementation**:
- **Order Created**: Publishes `OrderCreatedEvent` to Kafka topic `order-events`
- **Order Cancelled**: Publishes `OrderCancelledEvent` to Kafka topic `order-compensation`
- **State Management**: `SagaInstance` entity tracks SAGA state (STARTED, COMPLETED, COMPENSATING, COMPENSATED, FAILED)
- **Idempotency**: `ProcessedEvent` entity prevents duplicate event processing
- **Timeout Monitoring**: `SagaTimeoutMonitor` job checks for stuck SAGAs every minute (5-minute timeout)

**Benefits**:
- Loose coupling between services
- Automatic compensation on failure
- Event-driven scalability

### 2. Repository Pattern
**Purpose**: Abstract data access logic

**Implementation**:
- `OrderRepository`: Order CRUD operations
- `SagaInstanceRepository`: SAGA state management
- `ProcessedEventRepository`: Event idempotency tracking

**Benefits**:
- Separation of business logic from data access
- Easy to test and mock
- Database-agnostic design

### 3. DTO Pattern
**Purpose**: Transfer data between layers

**Implementation**:
- `CreateOrderRequest`: Input validation for order creation
- `OrderResponse`: Formatted output with calculated total
- `OrderItemDto`: Item details transfer

**Benefits**:
- Decouples API contract from domain model
- Input validation at API boundary
- Prevents over-fetching/under-fetching

### 4. Strategy Pattern (Security)
**Purpose**: Toggle security on/off via configuration

**Implementation**:
- `SecurityConfig`: Conditional security based on `security.enabled` property
- `JwtAuthenticationFilter`: JWT token validation when security enabled

**Benefits**:
- Easy testing without authentication
- Production-ready security when needed

### 5. Observer Pattern (Event-Driven)
**Purpose**: React to order lifecycle events

**Implementation**:
- `OrderEventListener`: Listens to Kafka topics
- `OrderSagaOrchestrator`: Publishes events

**Benefits**:
- Asynchronous processing
- Scalable event handling

## Tech Stack

- **Java 17**: Modern Java features
- **Spring Boot 3.4.1**: Application framework
- **Spring Data JPA**: Database access
- **Spring Kafka**: Event streaming
- **Spring Security**: JWT authentication
- **PostgreSQL 15**: Relational database with UUID support
- **Apache Kafka**: Event streaming platform
- **SpringDoc OpenAPI 2.7.0**: API documentation (Swagger)
- **Docker & Docker Compose**: Containerization
- **Gradle**: Build tool

## Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 17 (for local development)

### Using Docker Compose (Recommended)

```bash
# Start all services (PostgreSQL, Kafka, Zookeeper, Application)
docker-compose up -d

# View application logs
docker-compose logs -f oms-app

# Stop all services
docker-compose down

# Stop and remove volumes (clean restart)
docker-compose down -v
```

### Local Development

```bash
# Start infrastructure only
docker-compose up -d postgres kafka zookeeper

# Build application
./gradlew clean build

# Run application
./gradlew bootRun
```

## API Documentation

### Swagger UI
Interactive API documentation: **http://localhost:8080/swagger-ui.html**

### OpenAPI JSON
API specification: **http://localhost:8080/v3/api-docs**

### Postman Collection
Import `OMS-API.postman_collection.json` into Postman for testing.

### API Endpoints

#### 1. Create Order
```bash
POST /api/orders
Content-Type: application/json

{
  "userId": "user123",
  "items": [
    {
      "productName": "Laptop",
      "quantity": 1,
      "price": 999.99
    },
    {
      "productName": "Mouse",
      "quantity": 2,
      "price": 25.50
    }
  ]
}

Response:
{
  "id": "a83e2240-81d8-459e-8f87-08a95e13cc04",
  "userId": "user123",
  "status": "PENDING",
  "createdAt": "2025-12-23T15:08:05.904855876",
  "total": 1050.99,
  "items": [...]
}
```

#### 2. Get All Orders
```bash
GET /api/orders

# Filter by status
GET /api/orders?status=PENDING
GET /api/orders?status=PROCESSING
```

#### 3. Get Order by ID
```bash
GET /api/orders/{uuid}
```

#### 4. Update Order Status
```bash
PATCH /api/orders/{uuid}/status?status=PROCESSING
```

#### 5. Cancel Order
```bash
POST /api/orders/{uuid}/cancel
# Note: Only PENDING orders can be cancelled
```

## Database Schema

### Orders Table
```sql
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

### Order Items Table
```sql
CREATE TABLE order_item (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    order_id UUID NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);
```

### SAGA Instance Table
```sql
CREATE TABLE saga_instance (
    saga_id VARCHAR(255) PRIMARY KEY,
    order_id VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP
);
```

### Processed Events Table
```sql
CREATE TABLE processed_events (
    event_id VARCHAR(255) PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    processed_at TIMESTAMP NOT NULL
);
```

## SAGA Pattern

### Order Creation Flow
```
1. Client → POST /api/orders
2. OrderService creates order (status: PENDING)
3. OrderSagaOrchestrator.startOrderSaga()
4. Create SagaInstance (status: STARTED)
5. Publish OrderCreatedEvent to Kafka
6. OrderEventListener consumes event
7. Check idempotency (ProcessedEvent)
8. Process order logic
9. Update SagaInstance (status: COMPLETED)
10. Acknowledge Kafka message
```

### Order Cancellation Flow (Compensation)
```
1. Client → POST /api/orders/{id}/cancel
2. OrderService validates status (must be PENDING)
3. Update order status to CANCELLED
4. OrderSagaOrchestrator.compensateOrder()
5. Update SagaInstance (status: COMPENSATING)
6. Publish OrderCancelledEvent to Kafka
7. OrderEventListener consumes compensation event
8. Execute compensation logic
9. Update SagaInstance (status: COMPENSATED)
10. Acknowledge Kafka message
```

### Timeout Monitoring
```
SagaTimeoutMonitor (runs every 1 minute):
1. Query SAGAs with status=STARTED and startedAt < (now - 5 minutes)
2. For each timed-out SAGA:
   - Update status to FAILED
   - Trigger compensation flow
   - Publish compensation event
```

### Kafka Topics
- **order-events**: Order lifecycle events (OrderCreatedEvent)
- **order-compensation**: Compensation events (OrderCancelledEvent)

## Security

### JWT Authentication (Optional)

Security is **disabled by default** for easy testing. Enable via configuration:

```properties
security.enabled=true
```

### When Enabled
- All endpoints require JWT token except `/api/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`
- Login endpoint: `POST /api/auth/login`
- Token expiration: 24 hours (configurable)

### When Disabled
- All endpoints publicly accessible
- No authentication required

## Configuration

### application.properties

```properties
# Application
spring.application.name=oms-java

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/omsdb
spring.datasource.username=omsuser
spring.datasource.password=omspass
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

# Kafka
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=oms-group
spring.kafka.consumer.auto-offset-reset=earliest

# Scheduler (every 5 minutes)
order.status.update.cron=0 */5 * * * *

# Security (disabled by default)
security.enabled=false
security.jwt.secret=<your-secret-key>
security.jwt.expiration=86400000

# Swagger/OpenAPI
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

### Docker Compose Services

```yaml
services:
  postgres:    # PostgreSQL database on port 5432
  zookeeper:   # Kafka coordination service
  kafka:       # Event streaming on port 9092
  oms-app:     # Spring Boot application on port 8080
```

## Project Structure

```
oms-java/
├── src/main/java/com/epam/demo/omsjava/
│   ├── config/              # Configuration classes
│   │   ├── KafkaConfig.java
│   │   └── OpenApiConfig.java
│   ├── controller/          # REST API controllers
│   │   └── OrderController.java
│   ├── domain/              # Domain entities
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   └── OrderStatus.java
│   ├── dto/                 # Data transfer objects
│   │   ├── CreateOrderRequest.java
│   │   ├── OrderResponse.java
│   │   └── OrderItemDto.java
│   ├── exception/           # Custom exceptions
│   │   ├── OrderNotFoundException.java
│   │   ├── InvalidOrderStateException.java
│   │   └── GlobalExceptionHandler.java
│   ├── job/                 # Scheduled jobs
│   │   └── OrderStatusUpdateJob.java
│   ├── repository/          # Data access layer
│   │   ├── OrderRepository.java
│   │   └── SagaInstanceRepository.java
│   ├── saga/                # SAGA orchestration
│   │   ├── OrderSagaOrchestrator.java
│   │   ├── OrderEventListener.java
│   │   ├── SagaTimeoutMonitor.java
│   │   ├── OrderCreatedEvent.java
│   │   ├── OrderCancelledEvent.java
│   │   └── state/
│   │       ├── SagaInstance.java
│   │       ├── SagaStatus.java
│   │       └── ProcessedEvent.java
│   ├── security/            # JWT security
│   │   ├── SecurityConfig.java
│   │   ├── JwtUtil.java
│   │   └── JwtAuthenticationFilter.java
│   └── service/             # Business logic
│       ├── OrderService.java
│       └── impl/
│           └── OrderServiceImpl.java
├── src/main/resources/
│   └── application.properties
├── init.sql                 # Database initialization
├── compose.yaml             # Docker Compose configuration
├── Dockerfile               # Application container
├── build.gradle.kts         # Gradle build configuration
└── OMS-API.postman_collection.json  # Postman collection
```

## Order Status Flow

```
┌─────────┐
│ PENDING │ ← Initial state when order is created
└────┬────┘
     │ (Auto-updated every 5 minutes by scheduled job)
     ↓
┌────────────┐
│ PROCESSING │ ← Order is being processed
└─────┬──────┘
      │ (Manual update)
      ↓
┌─────────┐
│ SHIPPED │ ← Order has been shipped
└────┬────┘
     │ (Manual update)
     ↓
┌───────────┐
│ DELIVERED │ ← Final state - order delivered
└───────────┘

┌─────────┐
│ PENDING │
└────┬────┘
     │ (Cancel order API - only from PENDING)
     ↓
┌───────────┐
│ CANCELLED │ ← Order cancelled with compensation
└───────────┘
```

## Testing

```bash
# Run tests
./gradlew test

# Build without tests
./gradlew build -x test

# Clean build
./gradlew clean build
```

## Monitoring & Health

- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **PostgreSQL**: localhost:5432
- **Kafka**: localhost:9092

### Sample Data

The system automatically loads 5 sample orders on startup:
- Order 1: PENDING (user123) - Laptop + Mouse
- Order 2: PROCESSING (user456) - Keyboard + Monitor
- Order 3: SHIPPED (user789) - Headphones
- Order 4: DELIVERED (user123) - Webcam + Microphone
- Order 5: CANCELLED (user456) - USB Cable

## Key Features Summary

✅ **UUID-based Order IDs** - Globally unique identifiers
✅ **User ID Tracking** - Orders associated with users
✅ **Order Total Calculation** - Automatic calculation from items
✅ **Status Filtering** - Query orders by status
✅ **SAGA Pattern** - Distributed transaction management
✅ **Event-Driven** - Kafka-based event streaming
✅ **Idempotency** - Duplicate event prevention
✅ **Timeout Monitoring** - Automatic SAGA failure detection
✅ **JWT Security** - Optional token-based authentication
✅ **Swagger Documentation** - Interactive API docs
✅ **Postman Collection** - Ready-to-use API tests
✅ **Docker Support** - Complete containerized deployment
✅ **Clean Architecture** - Maintainable and testable code
✅ **Production Ready** - Error handling, validation, logging
