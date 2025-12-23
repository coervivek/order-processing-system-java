# Database Migration & Initialization ✅

## Overview

Automatic database initialization using **Flyway migrations** and **PostgreSQL init scripts**.

---

## Implementation

### 1. Flyway Migrations ✅

**Dependencies Added:**
```kotlin
implementation("org.flywaydb:flyway-core")
implementation("org.flywaydb:flyway-database-postgresql")
```

**Configuration:**
```properties
spring.jpa.hibernate.ddl-auto=none
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration
```

### 2. Migration Scripts ✅

**Location:** `src/main/resources/db/migration/`

**V1__Initial_Schema.sql** - Creates tables:
- `orders` - Order management
- `order_item` - Order line items
- `saga_instance` - SAGA state tracking
- `processed_events` - Idempotency
- Indexes for performance

**V2__Seed_Data.sql** - Inserts sample data:
- 5 sample orders (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED)
- 8 order items across orders
- Resets sequences

### 3. PostgreSQL Init Script ✅

**File:** `init.sql` (root directory)

**Docker Compose Integration:**
```yaml
postgres:
  volumes:
    - ./init.sql:/docker-entrypoint-initdb.d/init.sql
```

**Executes automatically** when PostgreSQL container starts for the first time.

---

## Sample Data Loaded

### Orders
| ID | Status | Created | Items |
|----|--------|---------|-------|
| 1 | PENDING | 2h ago | 2 |
| 2 | PROCESSING | 1h ago | 2 |
| 3 | SHIPPED | 30m ago | 1 |
| 4 | DELIVERED | 10m ago | 2 |
| 5 | CANCELLED | 5m ago | 1 |

### Order Items
- Order 1: Laptop ($999.99), Mouse x2 ($25.50)
- Order 2: Keyboard ($75.00), Monitor ($299.99)
- Order 3: Headphones ($150.00)
- Order 4: Webcam ($89.99), Microphone ($120.00)
- Order 5: USB Cable x3 ($10.00)

---

## Usage

### Automatic Initialization

**On Docker Startup:**
```bash
docker-compose up -d
```

✅ PostgreSQL starts
✅ init.sql executes (creates tables + inserts data)
✅ Application starts
✅ Flyway validates schema
✅ Sample data available immediately

### Manual Migration

**Run Flyway manually:**
```bash
./gradlew flywayMigrate
```

**Clean and rebuild:**
```bash
./gradlew flywayClean flywayMigrate
```

---

## Migration Files

### V1__Initial_Schema.sql
```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE order_item (
    id BIGSERIAL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    order_id BIGINT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE TABLE saga_instance (...);
CREATE TABLE processed_events (...);

-- Indexes
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_saga_status ON saga_instance(status);
```

### V2__Seed_Data.sql
```sql
INSERT INTO orders (id, status, created_at) VALUES
(1, 'PENDING', NOW() - INTERVAL '2 hours'),
(2, 'PROCESSING', NOW() - INTERVAL '1 hour'),
...

INSERT INTO order_item (product_name, quantity, price, order_id) VALUES
('Laptop', 1, 999.99, 1),
...

SELECT setval('orders_id_seq', (SELECT MAX(id) FROM orders));
```

---

## Verification

### Check Flyway History
```sql
SELECT * FROM flyway_schema_history;
```

### Check Sample Data
```bash
curl http://localhost:8080/api/orders | jq 'length'
# Should return: 5

curl http://localhost:8080/api/orders | jq '.[0]'
# Should return first order with items
```

### Database Query
```sql
SELECT id, status, created_at FROM orders;
SELECT * FROM order_item WHERE order_id = 1;
```

---

## Adding New Migrations

### Step 1: Create Migration File
```bash
# Format: V{version}__{description}.sql
touch src/main/resources/db/migration/V3__Add_Customer_Table.sql
```

### Step 2: Write SQL
```sql
-- V3__Add_Customer_Table.sql
CREATE TABLE customer (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL
);

ALTER TABLE orders ADD COLUMN customer_id BIGINT;
ALTER TABLE orders ADD FOREIGN KEY (customer_id) REFERENCES customer(id);
```

### Step 3: Restart Application
```bash
docker-compose restart oms-app
```

✅ Flyway automatically applies new migration

---

## Configuration Options

### Disable Flyway
```properties
spring.flyway.enabled=false
```

### Change Migration Location
```properties
spring.flyway.locations=classpath:db/migration,classpath:db/seeds
```

### Clean Database on Startup (DEV ONLY)
```properties
spring.flyway.clean-on-validation-error=true
```

⚠️ **Never use in production!**

---

## Best Practices

✅ **Version Control**: All migrations in Git
✅ **Never Modify**: Don't change executed migrations
✅ **Incremental**: Create new migration for changes
✅ **Idempotent**: Use `IF NOT EXISTS` where possible
✅ **Test**: Test migrations on dev environment first
✅ **Rollback Plan**: Have rollback scripts ready

---

## Troubleshooting

### Migration Failed
```bash
# Check Flyway history
docker exec oms-postgres psql -U omsuser -d omsdb -c "SELECT * FROM flyway_schema_history;"

# Repair Flyway
./gradlew flywayRepair

# Or manually fix
docker exec oms-postgres psql -U omsuser -d omsdb -c "DELETE FROM flyway_schema_history WHERE success = false;"
```

### Reset Database
```bash
# Stop and remove volumes
docker-compose down -v

# Start fresh
docker-compose up -d
```

---

## Files Created

- ✅ `src/main/resources/db/migration/V1__Initial_Schema.sql`
- ✅ `src/main/resources/db/migration/V2__Seed_Data.sql`
- ✅ `init.sql` (PostgreSQL init script)
- ✅ `FlywayConfig.java` (Configuration bean)
- ✅ Updated `build.gradle.kts` (Flyway dependencies)
- ✅ Updated `application.properties` (Flyway settings)
- ✅ Updated `compose.yaml` (Init script mount)

---

## Summary

✅ **Flyway integrated** for version-controlled migrations
✅ **PostgreSQL init script** for automatic data loading
✅ **Sample data** ready on startup
✅ **Schema management** automated
✅ **Production-ready** migration strategy

**On every fresh start:**
1. PostgreSQL creates database
2. init.sql creates tables and inserts data
3. Application starts
4. Flyway validates schema
5. System ready with sample data
