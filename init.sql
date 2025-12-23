-- init.sql - Executed automatically by PostgreSQL on startup

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create tables
CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS order_item (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    order_id UUID NOT NULL,
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS saga_instance (
    saga_id VARCHAR(255) PRIMARY KEY,
    order_id VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS processed_events (
    event_id VARCHAR(255) PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    processed_at TIMESTAMP NOT NULL
);

-- Insert sample orders with explicit UUIDs
INSERT INTO orders (id, user_id, status, created_at) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'user123', 'PENDING', NOW() - INTERVAL '2 hours'),
('550e8400-e29b-41d4-a716-446655440002', 'user456', 'PROCESSING', NOW() - INTERVAL '1 hour'),
('550e8400-e29b-41d4-a716-446655440003', 'user789', 'SHIPPED', NOW() - INTERVAL '30 minutes'),
('550e8400-e29b-41d4-a716-446655440004', 'user123', 'DELIVERED', NOW() - INTERVAL '10 minutes'),
('550e8400-e29b-41d4-a716-446655440005', 'user456', 'CANCELLED', NOW() - INTERVAL '5 minutes')
ON CONFLICT DO NOTHING;

-- Insert order items
INSERT INTO order_item (product_name, quantity, price, order_id) VALUES
('Laptop', 1, 999.99, '550e8400-e29b-41d4-a716-446655440001'),
('Mouse', 2, 25.50, '550e8400-e29b-41d4-a716-446655440001'),
('Keyboard', 1, 75.00, '550e8400-e29b-41d4-a716-446655440002'),
('Monitor', 1, 299.99, '550e8400-e29b-41d4-a716-446655440002'),
('Headphones', 1, 150.00, '550e8400-e29b-41d4-a716-446655440003'),
('Webcam', 1, 89.99, '550e8400-e29b-41d4-a716-446655440004'),
('Microphone', 1, 120.00, '550e8400-e29b-41d4-a716-446655440004'),
('USB Cable', 3, 10.00, '550e8400-e29b-41d4-a716-446655440005')
ON CONFLICT DO NOTHING;
