# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Common Development Commands

### Build and Test
```bash
# Build and test
./mvnw clean install

# Run tests only
./mvnw test

# Run specific test class
./mvnw test -Dtest=StationControllerTest

# Run with coverage report
./mvnw clean test jacoco:report
# Coverage report available at: target/site/jacoco/index.html

# Package application
./mvnw clean package

# Skip tests during build
./mvnw clean package -DskipTests
```

### Running the Application
```bash
# Run with development profile (includes predefined JWT secret)
./mvnw spring-boot:run -Dspring.profiles.active=dev

# Run with custom JWT secret
export JWT_SECRET=your_secret_here
./mvnw spring-boot:run

# Access points
# API: http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
# H2 Console (dev only): http://localhost:8080/h2-console
```

## High-Level Architecture

### Technology Stack
- **Framework**: Spring Boot 3.2.3 with Java 21
- **Security**: JWT-based authentication with Spring Security
- **Database**: H2 (development), MySQL/PostgreSQL (production via TestContainers in tests)
- **Testing**: JUnit 5, Mockito, TestContainers, JaCoCo for coverage
- **API Documentation**: SpringDoc OpenAPI (Swagger)
- **Monitoring**: Micrometer with Prometheus metrics

### Architecture Layers

1. **Controller Layer** (`/controller`)
   - REST endpoints with `/api/v1` base path
   - Role-based access control (ADMIN, STATION_OPERATOR, EV_DRIVER)
   - Standardized request/response handling with proper HTTP status codes

2. **Service Layer** (`/services`)
   - Business logic with `@Transactional` support
   - JWT token generation and validation (JwtService)
   - User details service for Spring Security integration

3. **Repository Layer** (`/repositories`)
   - Spring Data JPA interfaces
   - Custom queries for complex operations

4. **Security Configuration**
   - JWT authentication filter (`JwtAuthFilter`)
   - Stateless session management
   - Public endpoints: `/api/auth/*`, `/swagger-ui/*`, `/h2-console/*`
   - Protected endpoints require valid JWT token with appropriate roles

5. **Exception Handling**
   - Global exception handler (`GlobalExceptionHandler`)
   - Custom exceptions for business logic violations
   - Consistent error response format

### Domain Model
- **User**: Base authentication entity with roles
- **Admin**: System administrator
- **StationOperator**: Manages stations and chargers
- **EVDriver**: Electric vehicle driver with cars and bookings
- **Station**: Charging location with coordinates
- **Charger**: Individual charging point with status (AVAILABLE, CHARGING, ERROR, MAINTENANCE)
- **Booking**: Reservation for charging time slots
- **Car**: Vehicle linked to drivers

### Testing Strategy
- **Unit Tests**: Mock-based testing for controllers and services
- **Integration Tests**: `@SpringBootTest` with TestContainers for database tests
- **Test Profiles**: Separate configurations for testing
- **Coverage**: JaCoCo integrated with SonarCloud

### Key Configuration Points
- JWT secret must be provided via `JWT_SECRET` environment variable
- Development profile includes predefined JWT secret (never use in production)
- Initial admin user created on startup (configurable via environment variables)
- CORS enabled for frontend integration
- Actuator endpoints exposed for monitoring