# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Common Development Commands

### Build and Test
```bash
# Run application with dev profile (includes JWT secret)
./mvnw spring-boot:run -Dspring.profiles.active=dev

# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=StationControllerTest

# Run with coverage report
./mvnw clean test jacoco:report
# Coverage report at: target/site/jacoco/index.html

# Build JAR
./mvnw clean package

# Skip tests during build
./mvnw clean package -DskipTests
```

### Docker
```bash
# Build image
docker build -t stations-management .

# Run with JWT secret
docker run -p 8080:8080 -e JWT_SECRET=your_secret stations-management
```

## High-Level Architecture

### Authentication Flow
The system uses JWT-based authentication with Spring Security:
1. Users authenticate via `/api/auth/register` or `/api/auth/login`
2. JwtService generates tokens using the JWT_SECRET
3. JwtAuthFilter validates tokens on protected endpoints
4. UserInfoDetails provides principal information from JWT claims

### Multi-Role User System
The User hierarchy supports three distinct roles:
- **Admin**: Full system access, user management
- **StationOperator**: Manages stations and chargers
- **EVDriver**: Books charging sessions, manages cars

Each role extends the base User entity with specific fields and repositories.

### Booking System Architecture
The booking flow integrates multiple components:
1. **Validation**: BookingService checks charger availability and time conflicts
2. **Car Association**: Bookings must be linked to a registered car
3. **User Tracking**: Each booking tracks the user who created it
4. **Status Management**: Chargers update status based on active bookings

### Exception Handling Strategy
Global exception handling via `@RestControllerAdvice`:
- Domain-specific exceptions (InvalidBookingException, UserException, etc.)
- Consistent error response format
- HTTP status code mapping
- Validation error aggregation

### Testing Patterns
Tests follow consistent patterns:
- **Controllers**: MockMvc with mocked services, security context
- **Services**: Mockito for repository mocking, business logic validation
- **Repositories**: TestContainers with MySQL for integration tests
- **Security**: TestSecurityConfig provides authentication bypass for tests

### Configuration Profiles
- **default**: Production-ready, requires JWT_SECRET environment variable
- **dev**: Development with hardcoded JWT secret, H2 console enabled
- **test**: In-memory H2, disabled security for unit tests
- **mysql-test**: TestContainers MySQL for integration tests