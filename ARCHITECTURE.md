# System Architecture

## Clean Architecture Layers

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│                   (OrderController)                      │
│  - REST API endpoints                                    │
│  - Request/Response DTOs                                 │
│  - Input validation                                      │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                   Application Layer                      │
│                   (OrderService)                         │
│  - Business logic                                        │
│  - Transaction management                                │
│  - SAGA orchestration                                    │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                    Domain Layer                          │
│              (Order, OrderItem, OrderStatus)             │
│  - Business entities                                     │
│  - Domain rules                                          │
│  - Value objects                                         │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                 Infrastructure Layer                     │
│         (OrderRepository, Kafka, PostgreSQL)             │
│  - Data persistence                                      │
│  - External integrations                                 │
│  - Message brokers                                       │
└─────────────────────────────────────────────────────────┘
```

## SAGA Pattern Flow

### Order Creation Flow
```
1. Client → POST /api/orders
2. OrderController → OrderService.createOrder()
3. OrderService → Save Order (PENDING)
4. OrderService → OrderSagaOrchestrator.startOrderSaga()
5. OrderSagaOrchestrator → Publish OrderCreatedEvent to Kafka
6. Return OrderResponse to Client
```

### Order Cancellation Flow (Compensation)
```
1. Client → POST /api/orders/{id}/cancel
2. OrderController → OrderService.cancelOrder()
3. OrderService → Validate order is PENDING
4. OrderService → Update status to CANCELLED
5. OrderService → OrderSagaOrchestrator.compensateOrder()
6. OrderSagaOrchestrator → Publish compensation event to Kafka
```

## Distributed Transaction Pattern

The system implements **Choreography-based SAGA**:

- Each service publishes events after completing local transactions
- Other services listen to events and react accordingly
- Compensation logic handles rollbacks
- No central coordinator (decentralized)

## Microservices Integration Points

```
┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│   Order      │         │   Payment    │         │  Inventory   │
│   Service    │────────▶│   Service    │────────▶│   Service    │
│              │  Event  │              │  Event  │              │
└──────────────┘         └──────────────┘         └──────────────┘
       │                        │                        │
       │                        │                        │
       └────────────────────────┴────────────────────────┘
                         Kafka Topics
                    (order-events, order-compensation)
```

## Database Schema

```sql
Order
├── id (PK)
├── status (ENUM)
└── created_at

OrderItem
├── id (PK)
├── order_id (FK)
├── product_name
├── quantity
└── price
```

## Scheduled Jobs

- **OrderStatusJob**: Runs every 5 minutes
  - Finds all PENDING orders
  - Updates them to PROCESSING
  - Configurable via `order.status.update.cron`

## Error Handling

- **OrderNotFoundException**: 404 - Order not found
- **InvalidOrderStateException**: 400 - Invalid state transition
- **GlobalExceptionHandler**: Centralized exception handling
