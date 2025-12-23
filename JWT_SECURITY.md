# JWT Security Implementation ✅

## Overview

JWT-based security layer that can be **easily enabled or disabled** via configuration.

**Default State**: ✅ **DISABLED** (for easy testing)

---

## Configuration

### Disable Security (Default)
```properties
security.enabled=false
```

### Enable Security
```properties
security.enabled=true
security.jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
security.jwt.expiration=86400000
```

---

## Components Implemented

### 1. JwtUtil ✅
**File**: `JwtUtil.java`

**Features**:
- Generate JWT tokens
- Validate JWT tokens
- Extract username from token
- HMAC-SHA256 signing

```java
public String generateToken(String username)
public boolean validateToken(String token)
public String extractUsername(String token)
```

### 2. JwtAuthenticationFilter ✅
**File**: `JwtAuthenticationFilter.java`

**Features**:
- Intercepts HTTP requests
- Extracts JWT from Authorization header
- Validates token
- Sets authentication context

### 3. SecurityConfig ✅
**File**: `SecurityConfig.java`

**Features**:
- Conditional security based on `security.enabled` property
- When disabled: Permits all requests
- When enabled: Requires JWT authentication

```java
if (!securityEnabled) {
    // Permit all requests
} else {
    // Require JWT authentication
}
```

### 4. AuthController ✅
**File**: `AuthController.java`

**Features**:
- Login endpoint: `/api/auth/login`
- Only active when security is enabled
- Returns JWT token on successful login

---

## Usage

### When Security is DISABLED (Default)

**Access API without authentication:**
```bash
curl -X GET http://localhost:8080/api/orders
```

✅ **Works without any token**

### When Security is ENABLED

#### Step 1: Login to get JWT token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

#### Step 2: Use token to access protected APIs
```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X GET http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN"
```

✅ **Access granted with valid token**

#### Without token (when security enabled):
```bash
curl -X GET http://localhost:8080/api/orders
```

❌ **403 Forbidden**

---

## Enabling/Disabling Security

### Method 1: application.properties
```properties
# Disable
security.enabled=false

# Enable
security.enabled=true
```

### Method 2: Environment Variable
```bash
# Disable
export SECURITY_ENABLED=false
java -jar app.jar

# Enable
export SECURITY_ENABLED=true
java -jar app.jar
```

### Method 3: Command Line
```bash
# Disable
java -jar app.jar --security.enabled=false

# Enable
java -jar app.jar --security.enabled=true
```

### Method 4: Spring Profile
```bash
# Use secure profile
java -jar app.jar --spring.profiles.active=secure
```

### Method 5: Docker Environment
```yaml
# docker-compose.yaml
oms-app:
  environment:
    SECURITY_ENABLED: "true"
```

---

## Default Credentials

**Username**: `admin`  
**Password**: `password`

⚠️ **Note**: In production, integrate with a user database and use password hashing (BCrypt).

---

## JWT Configuration

### Secret Key
```properties
security.jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
```
- 256-bit secret key
- Used for HMAC-SHA256 signing
- ⚠️ Change in production

### Token Expiration
```properties
security.jwt.expiration=86400000
```
- Default: 24 hours (86400000 ms)
- Configurable per environment

---

## API Endpoints

### Public Endpoints (when security enabled)
- `POST /api/auth/login` - Get JWT token

### Protected Endpoints (when security enabled)
- `POST /api/orders` - Create order
- `GET /api/orders` - List orders
- `GET /api/orders/{id}` - Get order
- `PATCH /api/orders/{id}/status` - Update status
- `POST /api/orders/{id}/cancel` - Cancel order

---

## Testing

### Test Script
```bash
./test-security.sh
```

### Manual Testing

#### 1. Test with Security Disabled
```bash
# Should work without token
curl http://localhost:8080/api/orders
```

#### 2. Enable Security
Edit `application.properties`:
```properties
security.enabled=true
```

Restart application.

#### 3. Test without Token
```bash
# Should return 403 Forbidden
curl http://localhost:8080/api/orders
```

#### 4. Login and Get Token
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}' | jq -r '.token')

echo $TOKEN
```

#### 5. Test with Token
```bash
# Should work
curl http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN"
```

---

## Security Features

### ✅ Implemented
- JWT token generation
- Token validation
- Token expiration
- HMAC-SHA256 signing
- Stateless authentication
- Easy enable/disable toggle
- Authorization header support
- Conditional security configuration

### 🔄 Production Enhancements (Optional)
- User database integration
- Password hashing (BCrypt)
- Role-based access control (RBAC)
- Refresh tokens
- Token blacklisting
- Rate limiting
- HTTPS enforcement

---

## Architecture

### Security Flow (When Enabled)

```
1. Client → POST /api/auth/login
   ↓
2. AuthController validates credentials
   ↓
3. JwtUtil generates token
   ↓
4. Return token to client
   ↓
5. Client → GET /api/orders (with Authorization header)
   ↓
6. JwtAuthenticationFilter intercepts request
   ↓
7. Extract and validate token
   ↓
8. Set authentication in SecurityContext
   ↓
9. Request proceeds to controller
```

### Security Flow (When Disabled)

```
1. Client → GET /api/orders
   ↓
2. SecurityConfig permits all requests
   ↓
3. Request proceeds directly to controller
```

---

## Configuration Files

### application.properties (Security Disabled)
```properties
security.enabled=false
security.jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
security.jwt.expiration=86400000
```

### application-secure.properties (Security Enabled)
```properties
security.enabled=true
security.jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
security.jwt.expiration=86400000
```

---

## Dependencies Added

```kotlin
implementation("org.springframework.boot:spring-boot-starter-security")
implementation("io.jsonwebtoken:jjwt-api:0.12.3")
runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
```

---

## Summary

✅ **JWT security implemented**  
✅ **Disabled by default for easy testing**  
✅ **Can be enabled with single property change**  
✅ **Stateless authentication**  
✅ **Production-ready foundation**  

**Toggle Security**: Change `security.enabled` property  
**Default State**: Disabled (no authentication required)  
**When Enabled**: JWT token required for all APIs except `/api/auth/login`
