#!/bin/bash

# Simple booking API test script without jq dependency

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Base URL
BASE_URL="http://localhost:8080/api/v1"

echo -e "${YELLOW}Simple Booking API Tests${NC}\n"
echo -e "${YELLOW}First, start the application in another terminal:${NC}"
echo -e "${GREEN}./mvnw spring-boot:run -Dspring.profiles.active=dev${NC}\n"

# Function to extract token from response
extract_token() {
    echo "$1" | sed -n 's/.*"token":"\([^"]*\)".*/\1/p'
}

# Function to extract id from response
extract_id() {
    echo "$1" | sed -n 's/.*"id":\([0-9]*\).*/\1/p'
}

echo -e "\n${YELLOW}=== Test 1: Login as admin ===${NC}"
ADMIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }')
ADMIN_TOKEN=$(extract_token "$ADMIN_RESPONSE")
echo "Response: $ADMIN_RESPONSE"
echo -e "${GREEN}Admin token obtained${NC}\n"

echo -e "${YELLOW}=== Test 2: Create EV Driver ===${NC}"
curl -X POST "$BASE_URL/auth/register/driver" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "username": "driver1",
    "password": "pass123",
    "email": "driver1@test.com",
    "firstName": "John",
    "lastName": "Doe"
  }'
echo -e "\n"

echo -e "${YELLOW}=== Test 3: Login as driver ===${NC}"
DRIVER_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "driver1",
    "password": "pass123"
  }')
DRIVER_TOKEN=$(extract_token "$DRIVER_RESPONSE")
echo "Response: $DRIVER_RESPONSE"
echo -e "${GREEN}Driver token obtained${NC}\n"

echo -e "${YELLOW}=== Test 4: Get driver user ID ===${NC}"
DRIVER_ID_RESPONSE=$(curl -s -X GET "$BASE_URL/drivers/userid" \
  -H "Authorization: Bearer $DRIVER_TOKEN")
DRIVER_USER_ID=$(echo "$DRIVER_ID_RESPONSE" | sed 's/[^0-9]//g')
echo "Driver User ID: $DRIVER_USER_ID"
echo -e "\n"

echo -e "${YELLOW}=== Test 5: Create Station Operator ===${NC}"
curl -X POST "$BASE_URL/auth/register/operator" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "username": "operator1",
    "password": "pass123",
    "email": "operator1@test.com",
    "firstName": "Jane",
    "lastName": "Smith"
  }'
echo -e "\n"

echo -e "${YELLOW}=== Test 6: Login as operator ===${NC}"
OPERATOR_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "operator1",
    "password": "pass123"
  }')
OPERATOR_TOKEN=$(extract_token "$OPERATOR_RESPONSE")
echo "Response: $OPERATOR_RESPONSE"
echo -e "${GREEN}Operator token obtained${NC}\n"

echo -e "${YELLOW}=== Test 7: Create Station ===${NC}"
STATION_RESPONSE=$(curl -s -X POST "$BASE_URL/stations" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $OPERATOR_TOKEN" \
  -d '{
    "name": "Downtown Charging Hub",
    "address": "456 Main St, City Center",
    "latitude": 40.7589,
    "longitude": -73.9851
  }')
STATION_ID=$(extract_id "$STATION_RESPONSE")
echo "Station Response: $STATION_RESPONSE"
echo "Station ID: $STATION_ID"
echo -e "\n"

echo -e "${YELLOW}=== Test 8: Create Charger ===${NC}"
CHARGER_RESPONSE=$(curl -s -X POST "$BASE_URL/stations/$STATION_ID/chargers" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $OPERATOR_TOKEN" \
  -d '{
    "type": "Type 2",
    "power": 22.0,
    "status": "AVAILABLE"
  }')
CHARGER_ID=$(extract_id "$CHARGER_RESPONSE")
echo "Charger Response: $CHARGER_RESPONSE"
echo "Charger ID: $CHARGER_ID"
echo -e "\n"

echo -e "${YELLOW}=== Test 9: Create Booking ===${NC}"
# Using fixed future dates to avoid timezone issues
BOOKING_RESPONSE=$(curl -s -X POST "$BASE_URL/bookings" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -d "{
    \"startTime\": \"2025-06-06T14:00:00\",
    \"endTime\": \"2025-06-06T16:00:00\",
    \"userId\": $DRIVER_USER_ID,
    \"chargerId\": $CHARGER_ID
  }")
BOOKING_ID=$(extract_id "$BOOKING_RESPONSE")
echo "Booking Response: $BOOKING_RESPONSE"
echo "Booking ID: $BOOKING_ID"
echo -e "\n"

echo -e "${YELLOW}=== Test 10: Get All Bookings ===${NC}"
curl -X GET "$BASE_URL/bookings" \
  -H "Authorization: Bearer $ADMIN_TOKEN"
echo -e "\n\n"

echo -e "${YELLOW}=== Test 11: Get Booking by ID ===${NC}"
curl -X GET "$BASE_URL/bookings/$BOOKING_ID" \
  -H "Authorization: Bearer $DRIVER_TOKEN"
echo -e "\n\n"

echo -e "${YELLOW}=== Test 12: Get Bookings by User ===${NC}"
curl -X GET "$BASE_URL/bookings/user/$DRIVER_USER_ID" \
  -H "Authorization: Bearer $DRIVER_TOKEN"
echo -e "\n\n"

echo -e "${YELLOW}=== Test 13: Update Booking Status ===${NC}"
curl -X PUT "$BASE_URL/bookings/$BOOKING_ID/status" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $OPERATOR_TOKEN" \
  -d '"CONFIRMED"'
echo -e "\n\n"

echo -e "${YELLOW}=== Test 14: Try Overlapping Booking (should fail) ===${NC}"
curl -X POST "$BASE_URL/bookings" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -d "{
    \"startTime\": \"2025-06-06T15:00:00\",
    \"endTime\": \"2025-06-06T17:00:00\",
    \"userId\": $DRIVER_USER_ID,
    \"chargerId\": $CHARGER_ID
  }"
echo -e "\n\n"

echo -e "${YELLOW}=== Test 15: Cancel Booking ===${NC}"
curl -X PUT "$BASE_URL/bookings/$BOOKING_ID/status" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -d '"CANCELLED"'
echo -e "\n\n"

echo -e "${YELLOW}=== Test 16: Delete Booking ===${NC}"
curl -X DELETE "$BASE_URL/bookings/$BOOKING_ID" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -v
echo -e "\n"

echo -e "${GREEN}Tests completed!${NC}"