#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Base URL
BASE_URL="http://localhost:8080/api/v1"

echo -e "${YELLOW}Starting Booking API Tests...${NC}\n"

# First, start the application (you need to run this in a separate terminal)
echo -e "${YELLOW}Make sure the application is running with:${NC}"
echo -e "${GREEN}./mvnw spring-boot:run -Dspring.profiles.active=dev${NC}\n"
echo "Press Enter when ready..."
read

# 1. Login as admin to get JWT token
echo -e "${YELLOW}1. Logging in as admin...${NC}"
ADMIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }')

ADMIN_TOKEN=$(echo $ADMIN_RESPONSE | grep -o '"[^"]*"' | sed -n 2p | tr -d '"')
echo -e "${GREEN}Admin JWT Token: ${ADMIN_TOKEN:0:50}...${NC}\n"

# 2. Create an EV Driver user
echo -e "${YELLOW}2. Creating EV Driver user...${NC}"
DRIVER_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/register/driver" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "username": "testdriver",
    "password": "password123",
    "email": "testdriver@example.com",
    "firstName": "Test",
    "lastName": "Driver"
  }')
echo -e "${GREEN}Driver created: $DRIVER_RESPONSE${NC}\n"

# 3. Login as driver to get their JWT token
echo -e "${YELLOW}3. Logging in as driver...${NC}"
DRIVER_LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testdriver",
    "password": "password123"
  }')

DRIVER_TOKEN=$(echo $DRIVER_LOGIN_RESPONSE | grep -o '"[^"]*"' | sed -n 2p | tr -d '"')
echo -e "${GREEN}Driver JWT Token: ${DRIVER_TOKEN:0:50}...${NC}\n"

# 4. Get driver ID
echo -e "${YELLOW}4. Getting driver user ID...${NC}"
DRIVER_ID_RESPONSE=$(curl -s -X GET "$BASE_URL/drivers/userid" \
  -H "Authorization: Bearer $DRIVER_TOKEN")
DRIVER_USER_ID=$(echo $DRIVER_ID_RESPONSE | grep -o '[0-9]*')
echo -e "${GREEN}Driver User ID: $DRIVER_USER_ID${NC}\n"

# 5. Create a station operator
echo -e "${YELLOW}5. Creating Station Operator...${NC}"
OPERATOR_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/register/operator" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "username": "testoperator",
    "password": "password123",
    "email": "testoperator@example.com",
    "firstName": "Test",
    "lastName": "Operator"
  }')
echo -e "${GREEN}Operator created: $OPERATOR_RESPONSE${NC}\n"

# 6. Login as operator
echo -e "${YELLOW}6. Logging in as operator...${NC}"
OPERATOR_LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testoperator",
    "password": "password123"
  }')

OPERATOR_TOKEN=$(echo $OPERATOR_LOGIN_RESPONSE | grep -o '"[^"]*"' | sed -n 2p | tr -d '"')
echo -e "${GREEN}Operator JWT Token: ${OPERATOR_TOKEN:0:50}...${NC}\n"

# 7. Create a station
echo -e "${YELLOW}7. Creating a station...${NC}"
STATION_RESPONSE=$(curl -s -X POST "$BASE_URL/stations" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $OPERATOR_TOKEN" \
  -d '{
    "name": "Test Charging Station",
    "address": "123 Test Street, Test City",
    "latitude": 40.7128,
    "longitude": -74.0060
  }')
STATION_ID=$(echo $STATION_RESPONSE | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
echo -e "${GREEN}Station created with ID: $STATION_ID${NC}\n"

# 8. Create chargers for the station
echo -e "${YELLOW}8. Creating chargers...${NC}"
CHARGER1_RESPONSE=$(curl -s -X POST "$BASE_URL/stations/$STATION_ID/chargers" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $OPERATOR_TOKEN" \
  -d '{
    "type": "Type 2",
    "power": 22.0,
    "status": "AVAILABLE"
  }')
CHARGER1_ID=$(echo $CHARGER1_RESPONSE | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
echo -e "${GREEN}Charger 1 created with ID: $CHARGER1_ID${NC}"

CHARGER2_RESPONSE=$(curl -s -X POST "$BASE_URL/stations/$STATION_ID/chargers" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $OPERATOR_TOKEN" \
  -d '{
    "type": "CCS",
    "power": 50.0,
    "status": "AVAILABLE"
  }')
CHARGER2_ID=$(echo $CHARGER2_RESPONSE | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
echo -e "${GREEN}Charger 2 created with ID: $CHARGER2_ID${NC}\n"

# 9. Create a booking
echo -e "${YELLOW}9. Creating a booking...${NC}"
# Calculate start and end times (1 hour from now for 2 hours)
START_TIME=$(date -u -v+1H '+%Y-%m-%dT%H:%M:%S')
END_TIME=$(date -u -v+3H '+%Y-%m-%dT%H:%M:%S')

BOOKING_RESPONSE=$(curl -s -X POST "$BASE_URL/bookings" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -d "{
    \"startTime\": \"$START_TIME\",
    \"endTime\": \"$END_TIME\",
    \"userId\": $DRIVER_USER_ID,
    \"chargerId\": $CHARGER1_ID
  }")
BOOKING_ID=$(echo $BOOKING_RESPONSE | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
echo -e "${GREEN}Booking created: $BOOKING_RESPONSE${NC}\n"

# 10. Get all bookings
echo -e "${YELLOW}10. Getting all bookings...${NC}"
curl -s -X GET "$BASE_URL/bookings" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq '.'
echo -e "\n"

# 11. Get booking by ID
echo -e "${YELLOW}11. Getting booking by ID...${NC}"
curl -s -X GET "$BASE_URL/bookings/$BOOKING_ID" \
  -H "Authorization: Bearer $DRIVER_TOKEN" | jq '.'
echo -e "\n"

# 12. Get bookings by user
echo -e "${YELLOW}12. Getting bookings by user...${NC}"
curl -s -X GET "$BASE_URL/bookings/user/$DRIVER_USER_ID" \
  -H "Authorization: Bearer $DRIVER_TOKEN" | jq '.'
echo -e "\n"

# 13. Get bookings by charger
echo -e "${YELLOW}13. Getting bookings by charger...${NC}"
curl -s -X GET "$BASE_URL/bookings/charger/$CHARGER1_ID" \
  -H "Authorization: Bearer $OPERATOR_TOKEN" | jq '.'
echo -e "\n"

# 14. Update booking status to CONFIRMED
echo -e "${YELLOW}14. Updating booking status to CONFIRMED...${NC}"
curl -s -X PUT "$BASE_URL/bookings/$BOOKING_ID/status" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $OPERATOR_TOKEN" \
  -d '"CONFIRMED"' | jq '.'
echo -e "\n"

# 15. Try to create overlapping booking (should fail)
echo -e "${YELLOW}15. Testing overlapping booking (should fail)...${NC}"
OVERLAP_RESPONSE=$(curl -s -X POST "$BASE_URL/bookings" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -d "{
    \"startTime\": \"$START_TIME\",
    \"endTime\": \"$END_TIME\",
    \"userId\": $DRIVER_USER_ID,
    \"chargerId\": $CHARGER1_ID
  }")
echo -e "${RED}Overlapping booking response: $OVERLAP_RESPONSE${NC}\n"

# 16. Create booking on different charger (should succeed)
echo -e "${YELLOW}16. Creating booking on different charger...${NC}"
BOOKING2_RESPONSE=$(curl -s -X POST "$BASE_URL/bookings" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -d "{
    \"startTime\": \"$START_TIME\",
    \"endTime\": \"$END_TIME\",
    \"userId\": $DRIVER_USER_ID,
    \"chargerId\": $CHARGER2_ID
  }")
echo -e "${GREEN}Second booking created: $BOOKING2_RESPONSE${NC}\n"

# 17. Cancel first booking
echo -e "${YELLOW}17. Cancelling first booking...${NC}"
curl -s -X PUT "$BASE_URL/bookings/$BOOKING_ID/status" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DRIVER_TOKEN" \
  -d '"CANCELLED"' | jq '.'
echo -e "\n"

# 18. Delete second booking
BOOKING2_ID=$(echo $BOOKING2_RESPONSE | grep -o '"id":[0-9]*' | grep -o '[0-9]*')
echo -e "${YELLOW}18. Deleting second booking...${NC}"
curl -s -X DELETE "$BASE_URL/bookings/$BOOKING2_ID" \
  -H "Authorization: Bearer $DRIVER_TOKEN"
echo -e "${GREEN}Booking deleted${NC}\n"

echo -e "${GREEN}All tests completed!${NC}"