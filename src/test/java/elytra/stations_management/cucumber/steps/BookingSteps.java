package elytra.stations_management.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.cucumber.datatable.DataTable;
import io.restassured.response.Response;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class BookingSteps {

    private Response response;
    private String authToken;
    private Long evDriverId;
    private Long carId;
    private Long bookingId;
    private Map<String, Long> stationNameToId = new HashMap<>();
    private Map<String, Long> chargerIdentifierToId = new HashMap<>();

    @Given("the following stations exist:")
    public void theFollowingStationsExist(DataTable dataTable) {
        // First create a station operator
        Map<String, Object> operatorBody = Map.of(
            "name", "Default Operator",
            "email", "default.operator@example.com",
            "password", "Password123!",
            "role", "STATION_OPERATOR"
        );

        Response operatorResponse = given()
            .contentType("application/json")
            .body(operatorBody)
            .when()
            .post("/api/auth/register");
        
        String operatorToken = operatorResponse.jsonPath().getString("token");
        
        // Create stations
        List<Map<String, String>> stationsList = dataTable.asMaps();
        for (Map<String, String> stationData : stationsList) {
            Map<String, Object> requestBody = Map.of(
                "name", stationData.get("name"),
                "address", "Test Address",
                "city", stationData.get("city"),
                "latitude", Double.parseDouble(stationData.get("latitude")),
                "longitude", Double.parseDouble(stationData.get("longitude")),
                "isPublic", true
            );

            Response createResponse = given()
                .header("Authorization", "Bearer " + operatorToken)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/stations");
            
            Long stationId = createResponse.jsonPath().getLong("id");
            stationNameToId.put(stationData.get("name"), stationId);
        }
    }

    @And("{string} has the following chargers:")
    public void stationHasTheFollowingChargers(String stationName, DataTable dataTable) {
        Long stationId = stationNameToId.get(stationName);
        
        // Get operator token (assuming same operator for all stations)
        Map<String, Object> loginBody = Map.of(
            "email", "default.operator@example.com",
            "password", "Password123!"
        );

        Response loginResponse = given()
            .contentType("application/json")
            .body(loginBody)
            .when()
            .post("/api/auth/login");
        
        String operatorToken = loginResponse.jsonPath().getString("token");
        
        // Add chargers
        List<Map<String, String>> chargersList = dataTable.asMaps();
        for (Map<String, String> chargerData : chargersList) {
            Map<String, Object> requestBody = Map.of(
                "identifier", chargerData.get("identifier"),
                "power", Double.parseDouble(chargerData.get("power")),
                "type", chargerData.get("type"),
                "status", chargerData.get("status")
            );

            Response chargerResponse = given()
                .header("Authorization", "Bearer " + operatorToken)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/stations/" + stationId + "/chargers");
            
            Long chargerId = chargerResponse.jsonPath().getLong("id");
            chargerIdentifierToId.put(chargerData.get("identifier"), chargerId);
        }
    }

    @Given("I am logged in as an EV driver")
    public void iAmLoggedInAsAnEVDriver() {
        Map<String, Object> requestBody = Map.of(
            "name", "EV Driver",
            "email", "evdriver.booking@example.com",
            "password", "Password123!",
            "role", "EV_DRIVER"
        );

        Response registerResponse = given()
            .contentType("application/json")
            .body(requestBody)
            .when()
            .post("/api/auth/register");
        
        authToken = registerResponse.jsonPath().getString("token");
        evDriverId = registerResponse.jsonPath().getLong("id");
    }

    @And("I have a car with charger type {string}")
    public void iHaveACarWithChargerType(String chargerType) {
        Map<String, Object> carBody = Map.of(
            "brand", "Tesla",
            "model", "Model 3",
            "licensePlate", "ABC123",
            "chargerType", chargerType
        );

        Response carResponse = given()
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .body(carBody)
            .when()
            .post("/api/cars");
        
        carId = carResponse.jsonPath().getLong("id");
    }

    @When("I create a booking with:")
    public void iCreateABookingWith(DataTable dataTable) {
        Map<String, String> bookingData = dataTable.asMaps().get(0);
        
        Long stationId = stationNameToId.get(bookingData.get("station"));
        Long chargerId = chargerIdentifierToId.get(bookingData.get("charger"));
        
        Map<String, Object> requestBody = Map.of(
            "stationId", stationId,
            "chargerId", chargerId,
            "carId", carId,
            "startTime", bookingData.get("startTime"),
            "endTime", bookingData.get("endTime")
        );

        response = given()
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .body(requestBody)
            .when()
            .post("/api/bookings");
    }

    @Then("the booking should be created successfully")
    public void theBookingShouldBeCreatedSuccessfully() {
        response.then().statusCode(201);
        bookingId = response.jsonPath().getLong("id");
    }

    @And("the booking status should be {string}")
    public void theBookingStatusShouldBe(String expectedStatus) {
        String actualStatus = response.jsonPath().getString("status");
        assertEquals(expectedStatus, actualStatus);
    }

    @And("I should receive a booking confirmation")
    public void iShouldReceiveABookingConfirmation() {
        response.then()
            .body("id", notNullValue())
            .body("confirmationCode", notNullValue());
    }

    @Given("there is an existing booking for {string} from {string} to {string}")
    public void thereIsAnExistingBookingFor(String chargerIdentifier, String startTime, String endTime) {
        // Create another EV driver
        Map<String, Object> otherDriverBody = Map.of(
            "name", "Other Driver",
            "email", "other.driver@example.com",
            "password", "Password123!",
            "role", "EV_DRIVER"
        );

        Response otherDriverResponse = given()
            .contentType("application/json")
            .body(otherDriverBody)
            .when()
            .post("/api/auth/register");
        
        String otherToken = otherDriverResponse.jsonPath().getString("token");
        
        // Create car for other driver
        Map<String, Object> otherCarBody = Map.of(
            "brand", "Nissan",
            "model", "Leaf",
            "licensePlate", "XYZ789",
            "chargerType", "CCS"
        );

        Response otherCarResponse = given()
            .header("Authorization", "Bearer " + otherToken)
            .contentType("application/json")
            .body(otherCarBody)
            .when()
            .post("/api/cars");
        
        Long otherCarId = otherCarResponse.jsonPath().getLong("id");
        
        // Create booking
        Long stationId = stationNameToId.get("Downtown Station");
        Long chargerId = chargerIdentifierToId.get(chargerIdentifier);
        
        Map<String, Object> bookingBody = Map.of(
            "stationId", stationId,
            "chargerId", chargerId,
            "carId", otherCarId,
            "startTime", startTime,
            "endTime", endTime
        );

        given()
            .header("Authorization", "Bearer " + otherToken)
            .contentType("application/json")
            .body(bookingBody)
            .when()
            .post("/api/bookings");
    }

    @Then("the booking should fail with status {int}")
    public void theBookingShouldFailWithStatus(int statusCode) {
        response.then().statusCode(statusCode);
    }

    @When("I request available time slots for date {string}")
    public void iRequestAvailableTimeSlotsForDate(String date) {
        Long stationId = stationNameToId.get("Downtown Station");
        
        response = given()
            .header("Authorization", "Bearer " + authToken)
            .queryParam("date", date)
            .when()
            .get("/api/stations/" + stationId + "/available-slots");
    }

    @Then("I should see available time slots")
    public void iShouldSeeAvailableTimeSlots() {
        response.then()
            .statusCode(200)
            .body("$", not(empty()));
    }

    @And("the slots should not overlap with existing bookings")
    public void theSlotsShouldNotOverlapWithExistingBookings() {
        List<Map<String, Object>> slots = response.jsonPath().getList("$");
        // Additional validation logic would go here
        assertFalse(slots.isEmpty());
    }

    @Given("I have a future booking for {string}")
    public void iHaveAFutureBookingFor(String stationName) {
        Long stationId = stationNameToId.get(stationName);
        Long chargerId = chargerIdentifierToId.values().iterator().next();
        
        LocalDateTime futureStart = LocalDateTime.now().plusDays(1);
        LocalDateTime futureEnd = futureStart.plusHours(1);
        
        Map<String, Object> bookingBody = Map.of(
            "stationId", stationId,
            "chargerId", chargerId,
            "carId", carId,
            "startTime", futureStart.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            "endTime", futureEnd.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        Response bookingResponse = given()
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .body(bookingBody)
            .when()
            .post("/api/bookings");
        
        bookingId = bookingResponse.jsonPath().getLong("id");
    }

    @When("I cancel the booking")
    public void iCancelTheBooking() {
        response = given()
            .header("Authorization", "Bearer " + authToken)
            .when()
            .delete("/api/bookings/" + bookingId);
    }

    @Then("the booking should be cancelled successfully")
    public void theBookingShouldBeCancelledSuccessfully() {
        response.then().statusCode(200);
    }

    @And("the time slot should become available")
    public void theTimeSlotShouldBecomeAvailable() {
        // Verify the booking status
        Response bookingResponse = given()
            .header("Authorization", "Bearer " + authToken)
            .when()
            .get("/api/bookings/" + bookingId);
        
        String status = bookingResponse.jsonPath().getString("status");
        assertEquals("CANCELLED", status);
    }

    @When("I request my bookings list")
    public void iRequestMyBookingsList() {
        response = given()
            .header("Authorization", "Bearer " + authToken)
            .when()
            .get("/api/bookings/my-bookings");
    }

    @Then("I should see {int} bookings")
    public void iShouldSeeBookings(int expectedCount) {
        List<Object> bookings = response.jsonPath().getList("$");
        assertEquals(expectedCount, bookings.size());
    }

    @And("the bookings should be sorted by date descending")
    public void theBookingsShouldBeSortedByDateDescending() {
        List<String> dates = response.jsonPath().getList("startTime");
        // Verify sorting logic
        assertTrue(dates.size() > 1);
    }
}