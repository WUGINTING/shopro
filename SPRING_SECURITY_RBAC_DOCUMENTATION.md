# Spring Security Implementation with Role-Based Access Control (RBAC)

## Overview
This document describes the Spring Security implementation with JWT-based authentication and role-based access control (RBAC) for the E-commerce application.

## Architecture

### Components
1. **User Entity**: Stores user information including username, email, password, and role
2. **Role Enum**: Defines available roles (ADMIN, MANAGER, STAFF, CUSTOMER)
3. **JWT Service**: Handles JWT token generation and validation
4. **Security Configuration**: Configures Spring Security with stateless JWT authentication
5. **Authentication Filter**: Intercepts requests and validates JWT tokens

## Roles and Permissions

### Role Hierarchy
The system implements four distinct roles with varying levels of access:

#### 1. ADMIN (Administrator)
**Full system access with all privileges**

Permissions:
- ✅ Create, read, update, and delete products
- ✅ Activate/deactivate products
- ✅ Create, read, update orders
- ✅ Manage all system resources
- ✅ Access to all API endpoints
- ✅ Delete operations (highest privilege)

**Use Case**: System administrators who need complete control over the platform.

#### 2. MANAGER
**Product and order management with business operations**

Permissions:
- ✅ Create, read, update products (cannot delete)
- ✅ Activate/deactivate products
- ✅ Create, read, update orders
- ✅ View reports and statistics
- ❌ Delete products (ADMIN only)

**Use Case**: Store managers who handle day-to-day operations and product catalog management.

#### 3. STAFF
**Order processing and customer service**

Permissions:
- ✅ Create, read, update orders
- ✅ View product information
- ❌ Create, update, or delete products
- ❌ Activate/deactivate products

**Use Case**: Staff members who process orders and handle customer requests.

#### 4. CUSTOMER
**Basic browsing and shopping**

Permissions:
- ✅ Browse products (view only)
- ✅ Search products
- ✅ View product details
- ❌ Create, update, or delete products
- ❌ Manage orders (customer-specific orders only in future implementation)

**Use Case**: Regular customers who browse and purchase products.

## API Endpoints

### Public Endpoints (No Authentication Required)
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /swagger-ui/**` - API documentation
- `GET /v3/api-docs/**` - OpenAPI specification

### Protected Endpoints (Authentication Required)

#### Product Management (`/api/products`)
| Endpoint | Method | Required Role(s) | Description |
|----------|--------|------------------|-------------|
| `/api/products` | POST | ADMIN, MANAGER | Create product |
| `/api/products/{id}` | PUT | ADMIN, MANAGER | Update product |
| `/api/products/{id}` | DELETE | ADMIN | Delete product |
| `/api/products/{id}` | GET | All authenticated | View product details |
| `/api/products` | GET | All authenticated | List products |
| `/api/products/{id}/activate` | PUT | ADMIN, MANAGER | Activate product |
| `/api/products/{id}/deactivate` | PUT | ADMIN, MANAGER | Deactivate product |

#### Order Management (`/api/orders`)
| Endpoint | Method | Required Role(s) | Description |
|----------|--------|------------------|-------------|
| `/api/orders` | POST | ADMIN, MANAGER, STAFF | Create order |
| `/api/orders/{id}` | PUT | ADMIN, MANAGER, STAFF | Update order |
| `/api/orders/{id}` | GET | ADMIN, MANAGER, STAFF | View order details |

## Authentication Flow

### 1. User Registration
```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "role": "CUSTOMER"
}
```

**Response:**
```json
{
  "code": 200,
  "message": "註冊成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "role": "CUSTOMER"
  }
}
```

### 2. User Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "code": 200,
  "message": "登錄成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "role": "ADMIN"
  }
}
```

### 3. Accessing Protected Resources
Include the JWT token in the Authorization header:

```bash
GET /api/products
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Sample Users for Testing

The system initializes with the following test users:

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| admin | admin123 | ADMIN | Full system access |
| manager | manager123 | MANAGER | Product and order management |
| staff | staff123 | STAFF | Order processing |
| customer | customer123 | CUSTOMER | Browse and view products |

## Testing Examples

### Example 1: ADMIN - Create Product
```bash
# Login as admin
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Use token to create product
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "price": 100.00,
    "description": "Test Description"
  }'
```

**Expected Result**: ✅ Success - Product created

### Example 2: MANAGER - Create Product
```bash
# Login as manager
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"manager","password":"manager123"}'

# Use token to create product
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "price": 100.00,
    "description": "Test Description"
  }'
```

**Expected Result**: ✅ Success - Product created

### Example 3: STAFF - Create Product
```bash
# Login as staff
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"staff","password":"staff123"}'

# Attempt to create product
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "price": 100.00,
    "description": "Test Description"
  }'
```

**Expected Result**: ❌ 403 Forbidden - Insufficient permissions

### Example 4: MANAGER - Delete Product
```bash
# Login as manager
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"manager","password":"manager123"}'

# Attempt to delete product
curl -X DELETE http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer <token>"
```

**Expected Result**: ❌ 403 Forbidden - Only ADMIN can delete products

### Example 5: CUSTOMER - View Products
```bash
# Login as customer
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"customer","password":"customer123"}'

# View products
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer <token>"
```

**Expected Result**: ✅ Success - List of products returned

## Security Features

### 1. Password Encryption
- Passwords are encrypted using BCrypt algorithm
- Salt rounds: 10 (default)
- Never stored in plain text

### 2. JWT Token Configuration
- Algorithm: HS256
- Expiration: 24 hours (configurable via `jwt.expiration`)
- Secret key: Configurable via `jwt.secret` property

### 3. Stateless Sessions
- No server-side session storage
- All authentication state in JWT token
- Scalable for distributed systems

### 4. CORS Configuration
- Configured for API access
- Customizable allowed origins

## Configuration Properties

Add these properties to `application.properties`:

```properties
# JWT Configuration
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=86400000

# Security logging (optional)
logging.level.org.springframework.security=DEBUG
```

## Error Handling

### Common HTTP Status Codes
- `200 OK` - Successful operation
- `401 Unauthorized` - Missing or invalid JWT token
- `403 Forbidden` - Insufficient permissions for the operation
- `404 Not Found` - Resource not found
- `400 Bad Request` - Invalid request data

### Error Response Format
```json
{
  "code": 403,
  "message": "Access Denied",
  "data": null
}
```

## Best Practices

1. **Token Storage**
   - Store JWT tokens securely on client side
   - Use HttpOnly cookies for web applications
   - Never expose tokens in URLs

2. **Token Refresh**
   - Implement token refresh mechanism for better UX
   - Short-lived access tokens + long-lived refresh tokens

3. **Role Assignment**
   - Carefully assign roles based on least privilege principle
   - Regular audit of user roles and permissions

4. **Security Updates**
   - Keep Spring Security dependencies up to date
   - Monitor security advisories

## Future Enhancements

1. **Role Hierarchies**
   - Implement role inheritance
   - More granular permissions

2. **Refresh Tokens**
   - Implement refresh token mechanism
   - Improve user experience

3. **OAuth2 Integration**
   - Social login (Google, Facebook)
   - SSO capabilities

4. **Audit Logging**
   - Track user actions
   - Security event monitoring

5. **Rate Limiting**
   - Prevent brute force attacks
   - API throttling per user/role

## Support

For questions or issues related to authentication and authorization, please contact the development team or create an issue in the project repository.
