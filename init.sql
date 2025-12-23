-- init.sql - Executed automatically by PostgreSQL on startup

-- Create tables
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(50) NOT NULL CHECK (status IN ('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS order_item (
    id BIGSERIAL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    order_id BIGINT NOT NULL,
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS saga_instance (
    saga_id VARCHAR(255) PRIMARY KEY,
    order_id BIGINT,
    status VARCHAR(50) NOT NULL CHECK (status IN ('STARTED', 'COMPLETED', 'COMPENSATING', 'COMPENSATED', 'FAILED')),
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS processed_events (
    event_id VARCHAR(255) PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    processed_at TIMESTAMP NOT NULL
);

-- Insert sample orders
INSERT INTO orders (id, status, created_at) VALUES
(1, 'PENDING', NOW() - INTERVAL '2 hours'),
(2, 'PROCESSING', NOW() - INTERVAL '1 hour'),
(3, 'SHIPPED', NOW() - INTERVAL '30 minutes'),
(4, 'DELIVERED', NOW() - INTERVAL '10 minutes'),
(5, 'CANCELLED', NOW() - INTERVAL '5 minutes');

-- Insert order items
INSERT INTO order_item (product_name, quantity, price, order_id) VALUES
('Laptop', 1, 999.99, 1),
('Mouse', 2, 25.50, 1),
('Keyboard', 1, 75.00, 2),
('Monitor', 1, 299.99, 2),
('Headphones', 1, 150.00, 3),
('Webcam', 1, 89.99, 4),
('Microphone', 1, 120.00, 4),
('USB Cable', 3, 10.00, 5);

-- Reset sequences
SELECT setval('orders_id_seq', (SELECT MAX(id) FROM orders));
SELECT setval('order_item_id_seq', (SELECT MAX(id) FROM order_item));
