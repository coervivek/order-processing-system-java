#!/bin/bash

BASE_URL="http://localhost:8080"

echo "=========================================="
echo "JWT Security Testing"
echo "=========================================="
echo ""

echo "=== Test 1: Security DISABLED (default) ==="
echo "Accessing API without token..."
RESPONSE=$(curl -s -X GET "$BASE_URL/api/orders")
if [ $? -eq 0 ]; then
    echo "✅ SUCCESS: API accessible without authentication"
    echo "Orders count: $(echo $RESPONSE | jq 'length')"
else
    echo "❌ FAILED: Could not access API"
fi
echo ""

echo "=== Test 2: Enable Security ==="
echo "To enable security, set in application.properties:"
echo "  security.enabled=true"
echo ""
echo "Or run with profile:"
echo "  java -jar app.jar --spring.profiles.active=secure"
echo ""

echo "=== Test 3: Login to get JWT token (when security enabled) ==="
echo "curl -X POST $BASE_URL/api/auth/login \\"
echo "  -H 'Content-Type: application/json' \\"
echo "  -d '{\"username\":\"admin\",\"password\":\"password\"}'"
echo ""

echo "=== Test 4: Access API with JWT token (when security enabled) ==="
echo "TOKEN=\$(curl -s -X POST $BASE_URL/api/auth/login \\"
echo "  -H 'Content-Type: application/json' \\"
echo "  -d '{\"username\":\"admin\",\"password\":\"password\"}' | jq -r '.token')"
echo ""
echo "curl -X GET $BASE_URL/api/orders \\"
echo "  -H 'Authorization: Bearer \$TOKEN'"
echo ""

echo "=========================================="
echo "Configuration Options"
echo "=========================================="
echo ""
echo "Disable Security (default):"
echo "  security.enabled=false"
echo ""
echo "Enable Security:"
echo "  security.enabled=true"
echo ""
echo "Default Credentials (when enabled):"
echo "  Username: admin"
echo "  Password: password"
echo ""
