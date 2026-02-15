# Haripriya Clinic Management System - Authentication & Authorization Module

## Overview

This is a complete authentication and authorization backend module for the Haripriya Clinic Management System built with Spring Boot 4.0.2 and Spring Security 7.x. The system implements JWT-based authentication for internal users (Admin, Doctor, Receptionist) with role-based access control (RBAC).

## Technology Stack

- **Spring Boot**: 4.0.2
- **Spring Security**: 7.x
- **Java**: 17
- **PostgreSQL**: Database
- **JWT**: io.jsonwebtoken 0.11.5
- **Lombok**: For reducing boilerplate code
- **JPA/Hibernate**: For ORM

## Database Configuration

### PostgreSQL Connection Details
```properties
JDBC URL: jdbc:postgresql://localhost:5436/postgres
Username: postgres
Password: root
Schema: TestBackendProject
```

### Database Setup

1. **Create Schema** (if not exists):
```sql
CREATE SCHEMA IF NOT EXISTS "TestBackendProject";
```

2. **Run the initialization script**:
   - The `schema.sql` file in `src/main/resources/` contains the complete database setup
   - Spring Boot will auto-create tables based on JPA entities (ddl-auto=update)

3. **Default Admin User**:
   - Email: `admin@haripriya.com`
   - Password: You'll need to create this via the registration endpoint or manually insert a BCrypt hash

## Architecture

The project follows a clean layered architecture:

```
├── controller/          # REST API endpoints
├── service/            # Business logic layer
│   └── impl/          # Service implementations
├── repository/         # Data access layer
├── entity/            # JPA entities
├── dto/               # Data Transfer Objects
├── security/          # Security components (JWT, filters, UserDetails)
├── config/            # Configuration classes
├── exception/         # Custom exceptions and global handler
└── enums/             # Enumerations (Role, AuthProvider)
```

## User Roles

The system supports four roles:

1. **ADMIN** - Full system access
2. **DOCTOR** - Access to patient records and prescriptions
3. **RECEPTIONIST** - Access to appointments and billing
4. **PATIENT** - (Future: Will use Mobile OTP authentication)

## API Endpoints

### Authentication Endpoints (Public)

#### 1. Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "admin@haripriya.com",
  "password": "admin123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "type": "Bearer",
    "user": {
      "id": 1,
      "fullName": "System Administrator",
      "email": "admin@haripriya.com",
      "phone": "9999999999",
      "role": "ADMIN",
      "authProvider": "INTERNAL",
      "isActive": true,
      "createdAt": "2026-02-15T05:00:00",
      "updatedAt": "2026-02-15T05:00:00"
    }
  },
  "timestamp": "2026-02-15T05:30:00"
}
```

#### 2. Register (Admin Only)
```http
POST /auth/register
Authorization: Bearer <admin-jwt-token>
Content-Type: application/json

{
  "fullName": "Dr. John Doe",
  "email": "john.doe@haripriya.com",
  "phone": "9876543210",
  "password": "doctor123",
  "role": "DOCTOR"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 2,
    "fullName": "Dr. John Doe",
    "email": "john.doe@haripriya.com",
    "phone": "9876543210",
    "role": "DOCTOR",
    "authProvider": "INTERNAL",
    "isActive": true,
    "createdAt": "2026-02-15T05:35:00",
    "updatedAt": "2026-02-15T05:35:00"
  },
  "timestamp": "2026-02-15T05:35:00"
}
```

#### 3. Get Current User
```http
GET /auth/me
Authorization: Bearer <jwt-token>
```

**Response:**
```json
{
  "success": true,
  "message": "User details retrieved successfully",
  "data": {
    "id": 1,
    "fullName": "System Administrator",
    "email": "admin@haripriya.com",
    "phone": "9999999999",
    "role": "ADMIN",
    "authProvider": "INTERNAL",
    "isActive": true,
    "createdAt": "2026-02-15T05:00:00",
    "updatedAt": "2026-02-15T05:00:00"
  },
  "timestamp": "2026-02-15T05:30:00"
}
```

### Protected Endpoints (Examples)

#### Admin Dashboard
```http
GET /api/admin/dashboard
Authorization: Bearer <admin-jwt-token>
```

#### Doctor Dashboard
```http
GET /api/doctor/dashboard
Authorization: Bearer <doctor-or-admin-jwt-token>
```

#### Receptionist Dashboard
```http
GET /api/receptionist/dashboard
Authorization: Bearer <receptionist-or-admin-jwt-token>
```

## Security Features

### JWT Token Configuration

- **Secret Key**: Configured in `application.properties`
- **Expiration**: 24 hours (86400000 ms)
- **Algorithm**: HS512
- **Claims**: userId, email, role

### Password Encryption

- **Algorithm**: BCrypt
- **Strength**: Default (10 rounds)

### Session Management

- **Type**: Stateless (JWT-based)
- **No server-side sessions**

### CORS Configuration

Currently disabled for development. For production, configure CORS in `SecurityConfig`:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

## Error Handling

The system uses a global exception handler that returns standardized error responses:

### Error Response Format
```json
{
  "timestamp": "2026-02-15T05:30:00",
  "status": 401,
  "message": "Invalid email or password",
  "path": "/auth/login"
}
```

### Exception Types

1. **AuthenticationException** (401) - Invalid credentials or authentication failures
2. **ResourceNotFoundException** (404) - Resource not found
3. **ValidationException** (400) - Validation errors
4. **AccessDeniedException** (403) - Insufficient permissions

## Running the Application

### Prerequisites

1. Java 17 or higher
2. Maven 3.6+
3. PostgreSQL running on port 5436

### Steps

1. **Clone the repository** (if applicable)

2. **Configure database**:
   - Ensure PostgreSQL is running on `localhost:5436`
   - Create the schema: `CREATE SCHEMA IF NOT EXISTS "TestBackendProject";`

3. **Update application.properties** (if needed):
   - Database credentials
   - JWT secret key (for production, use a strong secret)

4. **Build the project**:
```bash
./mvnw clean install
```

5. **Run the application**:
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## Creating the First Admin User

Since registration requires admin authentication, you need to create the first admin user manually:

### Option 1: Direct Database Insert

1. Generate a BCrypt hash for your password (use an online tool or run this in a Java console):
```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hashedPassword = encoder.encode("your-password");
System.out.println(hashedPassword);
```

2. Insert into database:
```sql
SET search_path TO "TestBackendProject";

INSERT INTO users (full_name, email, phone, password, role, auth_provider, is_active, created_at, updated_at)
VALUES (
    'System Administrator',
    'admin@haripriya.com',
    '9999999999',
    '$2a$10$YOUR_BCRYPT_HASH_HERE',
    'ADMIN',
    'INTERNAL',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
```

### Option 2: Temporarily Disable Security

1. Comment out `@PreAuthorize("hasRole('ADMIN')")` in `AuthController.register()`
2. Start the application
3. Call `/auth/register` to create an admin user
4. Restore the annotation
5. Restart the application

## Testing with cURL

### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@haripriya.com",
    "password": "admin123"
  }'
```

### Register New User (requires admin token)
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "fullName": "Dr. Jane Smith",
    "email": "jane.smith@haripriya.com",
    "phone": "9876543210",
    "password": "doctor123",
    "role": "DOCTOR"
  }'
```

### Get Current User
```bash
curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Testing with Postman

1. **Import the collection** (create one with the endpoints above)
2. **Set up environment variables**:
   - `baseUrl`: `http://localhost:8080`
   - `token`: (will be set after login)
3. **Login** and save the token
4. **Use the token** in subsequent requests

## Future Enhancements

1. **Patient OTP Authentication**:
   - Mobile OTP generation and verification
   - SMS integration
   - OTP expiration and retry logic

2. **Refresh Tokens**:
   - Implement refresh token mechanism
   - Token rotation

3. **Password Reset**:
   - Forgot password flow
   - Email verification

4. **Account Lockout**:
   - Failed login attempt tracking
   - Temporary account lockout

5. **Audit Logging**:
   - Track all authentication attempts
   - User activity logging

6. **Multi-factor Authentication (MFA)**:
   - TOTP-based 2FA
   - SMS-based 2FA

## Project Structure

```
src/
├── main/
│   ├── java/com/haripriya/haripriya_backend/
│   │   ├── config/
│   │   │   └── SecurityConfig.java
│   │   ├── controller/
│   │   │   ├── AdminController.java
│   │   │   ├── AuthController.java
│   │   │   ├── DoctorController.java
│   │   │   └── ReceptionistController.java
│   │   ├── dto/
│   │   │   ├── ApiResponse.java
│   │   │   ├── AuthResponse.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── RegisterRequest.java
│   │   │   └── UserResponse.java
│   │   ├── entity/
│   │   │   └── User.java
│   │   ├── enums/
│   │   │   ├── AuthProvider.java
│   │   │   └── Role.java
│   │   ├── exception/
│   │   │   ├── AuthenticationException.java
│   │   │   ├── CustomException.java
│   │   │   ├── ErrorResponse.java
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── ResourceNotFoundException.java
│   │   │   └── ValidationException.java
│   │   ├── repository/
│   │   │   └── UserRepository.java
│   │   ├── security/
│   │   │   ├── CustomUserDetailsService.java
│   │   │   ├── JwtAuthenticationEntryPoint.java
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   ├── JwtTokenProvider.java
│   │   │   └── UserPrincipal.java
│   │   ├── service/
│   │   │   ├── AuthService.java
│   │   │   └── impl/
│   │   │       └── AuthServiceImpl.java
│   │   └── HaripriyaBackendApplication.java
│   └── resources/
│       ├── application.properties
│       └── schema.sql
└── test/
    └── java/com/haripriya/haripriya_backend/
```

## Troubleshooting

### Common Issues

1. **Database Connection Failed**:
   - Verify PostgreSQL is running on port 5436
   - Check credentials in `application.properties`
   - Ensure schema exists

2. **JWT Token Invalid**:
   - Check token expiration (24 hours)
   - Verify the secret key matches
   - Ensure Bearer prefix is included

3. **403 Forbidden**:
   - Verify user has the required role
   - Check `@PreAuthorize` annotations

4. **401 Unauthorized**:
   - Verify credentials are correct
   - Check if user account is active
   - Ensure correct auth provider (INTERNAL)

## License

[Add your license information here]

## Contact

[Add contact information here]
