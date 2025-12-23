#!/bin/bash

# E-commerce Order Management System - API Test Script

BASE_URL="http://localhost:8080/api/orders"

echo "=== Testing Order Management System ==="
echo ""

# 1. Create Order
echo "1. Creating new order..."
ORDER_RESPONSE=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
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
  }')
echo "$ORDER_RESPONSE" | jq .
ORDER_ID=$(echo "$ORDER_RESPONSE" | jq -r '.id')
echo "Created Order ID: $ORDER_ID"
echo ""

# 2. Get Order by ID
echo "2. Fetching order by ID..."
curl -s -X GET "$BASE_URL/$ORDER_ID" | jq .
echo ""

# 3. Get All Orders
echo "3. Fetching all orders..."
curl -s -X GET "$BASE_URL" | jq .
echo ""

# 4. Get Orders by Status
echo "4. Fetching PENDING orders..."
curl -s -X GET "$BASE_URL?status=PENDING" | jq .
echo ""

# 5. Update Order Status
echo "5. Updating order status to PROCESSING..."
curl -s -X PATCH "$BASE_URL/$ORDER_ID/status?status=PROCESSING" | jq .
echo ""

# 6. Create another order for cancellation test
echo "6. Creating order for cancellation..."
CANCEL_ORDER=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {
        "productName": "Keyboard",
        "quantity": 1,
        "price": 75.00
      }
    ]
  }')
CANCEL_ORDER_ID=$(echo "$CANCEL_ORDER" | jq -r '.id')
echo "Created Order ID for cancellation: $CANCEL_ORDER_ID"
echo ""

# 7. Cancel Order
echo "7. Cancelling order..."
curl -s -X POST "$BASE_URL/$CANCEL_ORDER_ID/cancel"
echo "Order cancelled"
echo ""

# 8. Verify cancellation
echo "8. Verifying cancelled order..."
curl -s -X GET "$BASE_URL/$CANCEL_ORDER_ID" | jq .
echo ""

echo "=== Test Complete ==="
