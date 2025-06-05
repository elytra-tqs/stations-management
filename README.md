# Stations Management API

Electric Vehicle Charging Stations Management System - Backend API

## Prerequisites

- Java 21
- Maven 3.8+
- PostgreSQL (for production) or H2 (for development)

## Environment Configuration

### JWT Secret Setup

The application requires a JWT secret for authentication. **The secret is not hardcoded for security reasons.**

#### Development Setup

**Option 1: Using the dev profile (Easiest for local development)**

Run the application with the dev profile:
```bash
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

This uses a predefined development secret. **Never use this in production!**

**Option 2: Using environment variables (Recommended)**

1. Copy the example environment file:
   ```bash
   cp .env.example .env
   ```

2. Generate a secure JWT secret:
   ```bash
   openssl rand -base64 64 | tr -d '\n'
   ```

3. Add the generated secret to your `.env` file:
   ```
   JWT_SECRET=your_generated_secret_here
   ```

4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

#### Production Setup

Set the JWT_SECRET environment variable:
```bash
export JWT_SECRET=your_secure_production_secret
```

## Running the Application

### Development
```bash
# With dev profile (includes dev JWT secret)
./mvnw spring-boot:run -Dspring.profiles.active=dev

# With environment variables
./mvnw spring-boot:run
```

### Testing
```bash
# Run all tests
./mvnw test

# Run with coverage report
./mvnw clean test jacoco:report
# Coverage report available at: target/site/jacoco/index.html
```

### Building
```bash
# Build JAR
./mvnw clean package

# Skip tests during build
./mvnw clean package -DskipTests
```

## API Documentation

Once the application is running, you can access:

- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/v3/api-docs
- H2 Console (dev only): http://localhost:8080/h2-console

## Docker

```bash
# Build image
docker build -t stations-management .

# Run with environment variable
docker run -p 8080:8080 -e JWT_SECRET=your_secret stations-management
```

## Security Notes

- **Never commit the `.env` file** - It's in `.gitignore`
- **Always use a strong, unique JWT secret** in production
- The application will not start without JWT_SECRET being set (except in dev profile)
- Default secrets in test/dev profiles are for local development only
