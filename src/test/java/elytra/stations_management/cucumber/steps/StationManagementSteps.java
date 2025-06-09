package elytra.stations_management.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.cucumber.datatable.DataTable;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class StationManagementSteps {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Response response;
    private String authToken;
    private Long stationId;
    private Long operatorId;
    private Map<String, Long> stationNameToId = new HashMap<>();
    
    @Given("I am logged in as a station operator")
    public void iAmLoggedInAsAStationOperator() {
        // Register station operator
        Map<String, Object> requestBody = Map.of(
            "name", "Station Operator",
            "email", "operator@example.com",
            "password", "Password123!",
            "role", "STATION_OPERATOR"
        );

        Response registerResponse = given()
            .contentType("application/json")
            .body(requestBody)
            .when()
            .post("/api/auth/register");
        
        authToken = registerResponse.jsonPath().getString("token");
        operatorId = registerResponse.jsonPath().getLong("id");
    }

    @When("I create a station with the following details:")
    public void iCreateAStationWithTheFollowingDetails(DataTable dataTable) {
        Map<String, String> stationData = dataTable.asMaps().get(0);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", stationData.get("name"));
        requestBody.put("address", stationData.get("address"));
        requestBody.put("city", stationData.get("city"));
        requestBody.put("latitude", Double.parseDouble(stationData.get("latitude")));
        requestBody.put("longitude", Double.parseDouble(stationData.get("longitude")));
        requestBody.put("isPublic", Boolean.parseBoolean(stationData.get("isPublic")));

        response = given()
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .body(requestBody)
            .when()
            .post("/api/stations");
    }

    @Then("the station should be created successfully")
    public void theStationShouldBeCreatedSuccessfully() {
        response.then().statusCode(201);
        stationId = response.jsonPath().getLong("id");
        String stationName = response.jsonPath().getString("name");
        stationNameToId.put(stationName, stationId);
    }

    @And("the station should be linked to my operator account")
    public void theStationShouldBeLinkedToMyOperatorAccount() {
        Long stationOperatorId = response.jsonPath().getLong("stationOperator.id");
        assertEquals(operatorId, stationOperatorId);
    }

    @And("the station should appear in the station list")
    public void theStationShouldAppearInTheStationList() {
        Response listResponse = given()
            .header("Authorization", "Bearer " + authToken)
            .when()
            .get("/api/stations");
        
        listResponse.then()
            .statusCode(200)
            .body("$", hasItem(hasEntry("id", stationId.intValue())));
    }

    @Then("the station creation should fail with status {int}")
    public void theStationCreationShouldFailWithStatus(int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Given("I have a station named {string}")
    public void iHaveAStationNamed(String stationName) {
        Map<String, Object> requestBody = Map.of(
            "name", stationName,
            "address", "123 Test St",
            "city", "Test City",
            "latitude", 37.7749,
            "longitude", -122.4194,
            "isPublic", true
        );

        Response createResponse = given()
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .body(requestBody)
            .when()
            .post("/api/stations");
        
        Long createdStationId = createResponse.jsonPath().getLong("id");
        stationNameToId.put(stationName, createdStationId);
    }

    @When("I update the station with:")
    public void iUpdateTheStationWith(DataTable dataTable) {
        Map<String, String> updateData = dataTable.asMaps().get(0);
        Long updateStationId = stationNameToId.values().iterator().next();
        
        Map<String, Object> requestBody = new HashMap<>();
        if (updateData.containsKey("name")) requestBody.put("name", updateData.get("name"));
        if (updateData.containsKey("address")) requestBody.put("address", updateData.get("address"));
        if (updateData.containsKey("city")) requestBody.put("city", updateData.get("city"));

        response = given()
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .body(requestBody)
            .when()
            .put("/api/stations/" + updateStationId);
    }

    @Then("the station should be updated successfully")
    public void theStationShouldBeUpdatedSuccessfully() {
        response.then().statusCode(200);
    }

    @And("the station details should reflect the changes")
    public void theStationDetailsShouldReflectTheChanges() {
        response.then()
            .body("name", equalTo("Updated Station Name"))
            .body("address", equalTo("789 New Address"))
            .body("city", equalTo("New City"));
    }

    @When("I delete the station")
    public void iDeleteTheStation() {
        Long deleteStationId = stationNameToId.get("Station to Delete");
        
        response = given()
            .header("Authorization", "Bearer " + authToken)
            .when()
            .delete("/api/stations/" + deleteStationId);
    }

    @Then("the station should be deleted successfully")
    public void theStationShouldBeDeletedSuccessfully() {
        response.then().statusCode(204);
    }

    @And("the station should not appear in the station list")
    public void theStationShouldNotAppearInTheStationList() {
        Long deletedStationId = stationNameToId.get("Station to Delete");
        
        Response listResponse = given()
            .header("Authorization", "Bearer " + authToken)
            .when()
            .get("/api/stations");
        
        listResponse.then()
            .statusCode(200)
            .body("$", not(hasItem(hasEntry("id", deletedStationId.intValue()))));
    }

    @When("I add a charger with the following details:")
    public void iAddAChargerWithTheFollowingDetails(DataTable dataTable) {
        Map<String, String> chargerData = dataTable.asMaps().get(0);
        Long addChargerStationId = stationNameToId.get("Charging Hub");
        
        Map<String, Object> requestBody = Map.of(
            "identifier", chargerData.get("identifier"),
            "power", Double.parseDouble(chargerData.get("power")),
            "type", chargerData.get("type"),
            "status", chargerData.get("status")
        );

        response = given()
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .body(requestBody)
            .when()
            .post("/api/stations/" + addChargerStationId + "/chargers");
    }

    @Then("the charger should be added successfully")
    public void theChargerShouldBeAddedSuccessfully() {
        response.then().statusCode(201);
    }

    @And("the station should have {int} charger(s)")
    public void theStationShouldHaveChargers(int expectedCount) {
        Long checkStationId = stationNameToId.values().iterator().next();
        
        Response stationResponse = given()
            .header("Authorization", "Bearer " + authToken)
            .when()
            .get("/api/stations/" + checkStationId);
        
        List<Object> chargers = stationResponse.jsonPath().getList("chargers");
        assertEquals(expectedCount, chargers.size());
    }

    @When("I add the following chargers:")
    public void iAddTheFollowingChargers(DataTable dataTable) {
        Long multiChargerStationId = stationNameToId.get("Multi-Charger Station");
        List<Map<String, String>> chargersList = dataTable.asMaps();
        
        for (Map<String, String> chargerData : chargersList) {
            Map<String, Object> requestBody = Map.of(
                "identifier", chargerData.get("identifier"),
                "power", Double.parseDouble(chargerData.get("power")),
                "type", chargerData.get("type"),
                "status", chargerData.get("status")
            );

            given()
                .header("Authorization", "Bearer " + authToken)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/stations/" + multiChargerStationId + "/chargers")
                .then()
                .statusCode(201);
        }
    }

    @Then("all chargers should be added successfully")
    public void allChargersShouldBeAddedSuccessfully() {
        // Verification is done in the previous step
    }

    @Given("I have a station with a charger {string}")
    public void iHaveAStationWithACharger(String chargerIdentifier) {
        // Create station
        Map<String, Object> stationBody = Map.of(
            "name", "Station with Charger",
            "address", "456 Charger St",
            "city", "Charger City",
            "latitude", 37.7749,
            "longitude", -122.4194,
            "isPublic", true
        );

        Response stationResponse = given()
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .body(stationBody)
            .when()
            .post("/api/stations");
        
        Long createdStationId = stationResponse.jsonPath().getLong("id");
        
        // Add charger
        Map<String, Object> chargerBody = Map.of(
            "identifier", chargerIdentifier,
            "power", 50.0,
            "type", "Type 2",
            "status", "AVAILABLE"
        );

        given()
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .body(chargerBody)
            .when()
            .post("/api/stations/" + createdStationId + "/chargers");
    }

    @When("I update the charger status to {string}")
    public void iUpdateTheChargerStatusTo(String newStatus) {
        // This would require finding the charger ID first
        // For simplicity, assuming we have an endpoint to update by identifier
        response = given()
            .header("Authorization", "Bearer " + authToken)
            .contentType("application/json")
            .body(Map.of("status", newStatus))
            .when()
            .put("/api/chargers/CHG-001/status");
    }

    @Then("the charger status should be updated successfully")
    public void theChargerStatusShouldBeUpdatedSuccessfully() {
        response.then().statusCode(200);
    }

    @And("the charger should show status {string}")
    public void theChargerShouldShowStatus(String expectedStatus) {
        response.then().body("status", equalTo(expectedStatus));
    }

    @Given("I have the following stations:")
    public void iHaveTheFollowingStations(DataTable dataTable) {
        List<Map<String, String>> stationsList = dataTable.asMaps();
        
        for (Map<String, String> stationData : stationsList) {
            Map<String, Object> requestBody = Map.of(
                "name", stationData.get("name"),
                "address", "Test Address",
                "city", stationData.get("city"),
                "latitude", 37.7749,
                "longitude", -122.4194,
                "isPublic", Boolean.parseBoolean(stationData.get("isPublic"))
            );

            Response createResponse = given()
                .header("Authorization", "Bearer " + authToken)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post("/api/stations");
            
            Long createdId = createResponse.jsonPath().getLong("id");
            stationNameToId.put(stationData.get("name"), createdId);
        }
    }

    @When("I request my stations list")
    public void iRequestMyStationsList() {
        response = given()
            .header("Authorization", "Bearer " + authToken)
            .when()
            .get("/api/stations/my-stations");
    }

    @Then("I should see {int} stations")
    public void iShouldSeeStations(int expectedCount) {
        List<Object> stations = response.jsonPath().getList("$");
        assertEquals(expectedCount, stations.size());
    }

    @And("all stations should belong to me")
    public void allStationsShouldBelongToMe() {
        List<Long> operatorIds = response.jsonPath().getList("stationOperator.id");
        assertTrue(operatorIds.stream().allMatch(id -> id.equals(operatorId)));
    }
}