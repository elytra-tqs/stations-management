package elytra.stations_management.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.cucumber.datatable.DataTable;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;
import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthenticationSteps {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Response response;
    private String authToken;
    private RequestSpecification request;

    @Given("the system is initialized with clean database")
    public void theSystemIsInitializedWithCleanDatabase() {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.basePath = "/api/v1";
        
        // Clean database tables in correct order to avoid foreign key constraints
        // For H2, we need to use DELETE instead of TRUNCATE with foreign keys
        jdbcTemplate.execute("DELETE FROM bookings");
        jdbcTemplate.execute("DELETE FROM cars");
        jdbcTemplate.execute("DELETE FROM chargers");
        jdbcTemplate.execute("DELETE FROM stations");
        jdbcTemplate.execute("DELETE FROM evdriver");
        jdbcTemplate.execute("DELETE FROM station_operators");
        jdbcTemplate.execute("DELETE FROM admins");
        jdbcTemplate.execute("DELETE FROM users");
    }

    @When("I register with the following details:")
    public void iRegisterWithTheFollowingDetails(DataTable dataTable) {
        Map<String, String> userData = dataTable.asMaps().get(0);
        String role = userData.get("role");
        
        Map<String, Object> userObject = new HashMap<>();
        String[] names = userData.get("name").split(" ", 2);
        userObject.put("username", userData.get("email").split("@")[0]);
        userObject.put("email", userData.get("email"));
        userObject.put("password", userData.get("password"));
        userObject.put("firstName", names[0]);
        userObject.put("lastName", names.length > 1 ? names[1] : "");
        
        Map<String, Object> requestBody = new HashMap<>();
        String endpoint = "";
        
        if ("EV_DRIVER".equals(role)) {
            requestBody.put("user", userObject);
            requestBody.put("driver", new HashMap<>());
            endpoint = "/auth/register/driver";
        } else if ("STATION_OPERATOR".equals(role)) {
            requestBody.put("user", userObject);
            requestBody.put("operator", new HashMap<>());
            endpoint = "/auth/register/operator";
        } else if ("ADMIN".equals(role)) {
            requestBody.put("user", userObject);
            requestBody.put("admin", new HashMap<>());
            endpoint = "/auth/register/admin";
        }

        response = given()
            .contentType("application/json")
            .body(requestBody)
            .when()
            .post(endpoint);
    }

    @Then("the registration should be successful")
    public void theRegistrationShouldBeSuccessful() {
        response.then().statusCode(200);
    }

    @And("the response should contain a JWT token")
    public void theResponseShouldContainAJWTToken() {
        authToken = response.jsonPath().getString("token");
        assertNotNull(authToken);
        assertFalse(authToken.isEmpty());
    }

    @And("the user should be saved in the database with role {string}")
    public void theUserShouldBeSavedInTheDatabaseWithRole(String expectedRole) {
        String username = response.jsonPath().getString("username");
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE username = ? AND user_type = ?",
            Integer.class,
            username,
            expectedRole
        );
        assertEquals(1, count);
    }

    @Given("a user exists with email {string}")
    public void aUserExistsWithEmail(String email) {
        Map<String, Object> userObject = Map.of(
            "username", email.split("@")[0],
            "email", email,
            "password", "Password123!",
            "firstName", "Existing",
            "lastName", "User"
        );
        
        Map<String, Object> requestBody = Map.of(
            "user", userObject,
            "driver", new HashMap<>()
        );

        given()
            .contentType("application/json")
            .body(requestBody)
            .when()
            .post("/auth/register/driver");
    }

    @Then("the registration should fail with status {int}")
    public void theRegistrationShouldFailWithStatus(int statusCode) {
        response.then().statusCode(statusCode);
    }

    @And("the error message should contain {string}")
    public void theErrorMessageShouldContain(String expectedMessage) {
        String errorMessage = response.jsonPath().getString("message");
        if (errorMessage == null) {
            errorMessage = response.jsonPath().getString("error");
        }
        assertTrue(errorMessage != null && errorMessage.contains(expectedMessage),
            "Expected error message to contain: " + expectedMessage + ", but got: " + errorMessage);
    }

    @Given("a user exists with email {string} and password {string}")
    public void aUserExistsWithEmailAndPassword(String email, String password) {
        Map<String, Object> userObject = Map.of(
            "username", email.split("@")[0],
            "email", email,
            "password", password,
            "firstName", "Test",
            "lastName", "User"
        );
        
        Map<String, Object> requestBody = Map.of(
            "user", userObject,
            "driver", new HashMap<>()
        );

        given()
            .contentType("application/json")
            .body(requestBody)
            .when()
            .post("/auth/register/driver");
    }

    @When("I login with email {string} and password {string}")
    public void iLoginWithEmailAndPassword(String email, String password) {
        Map<String, Object> requestBody = Map.of(
            "username", email.split("@")[0],
            "password", password
        );

        response = given()
            .contentType("application/json")
            .body(requestBody)
            .when()
            .post("/auth/login");
    }

    @Then("the login should be successful")
    public void theLoginShouldBeSuccessful() {
        response.then().statusCode(200);
    }

    @And("the response should contain the user details")
    public void theResponseShouldContainTheUserDetails() {
        response.then()
            .body("email", notNullValue())
            .body("name", notNullValue())
            .body("role", notNullValue());
    }

    @Then("the login should fail with status {int}")
    public void theLoginShouldFailWithStatus(int statusCode) {
        response.then().statusCode(statusCode);
    }

    @Given("I am logged in as an EV Driver")
    public void iAmLoggedInAsAnEVDriver() {
        Map<String, Object> requestBody = Map.of(
            "name", "EV Driver User",
            "email", "evdriver@example.com",
            "password", "Password123!",
            "role", "EV_DRIVER"
        );

        // Register
        given()
            .contentType("application/json")
            .body(requestBody)
            .when()
            .post("/auth/register");

        // Login
        Map<String, Object> loginBody = Map.of(
            "email", "evdriver@example.com",
            "password", "Password123!"
        );

        Response loginResponse = given()
            .contentType("application/json")
            .body(loginBody)
            .when()
            .post("/auth/login");

        authToken = loginResponse.jsonPath().getString("token");
    }

    @When("I access my profile endpoint")
    public void iAccessMyProfileEndpoint() {
        response = given()
            .header("Authorization", "Bearer " + authToken)
            .when()
            .get("/auth/me");
    }

    @Then("the request should be successful")
    public void theRequestShouldBeSuccessful() {
        response.then().statusCode(200);
    }

    @And("the response should contain my user details")
    public void theResponseShouldContainMyUserDetails() {
        response.then()
            .body("email", equalTo("evdriver@example.com"))
            .body("name", equalTo("EV Driver User"))
            .body("role", equalTo("EV_DRIVER"));
    }

    @When("I access a protected endpoint without authentication")
    public void iAccessAProtectedEndpointWithoutAuthentication() {
        response = given()
            .when()
            .get("/auth/me");
    }

    @Then("the request should fail with status {int}")
    public void theRequestShouldFailWithStatus(int statusCode) {
        response.then().statusCode(statusCode);
    }

    @When("I access the admin users endpoint")
    public void iAccessTheAdminUsersEndpoint() {
        response = given()
            .header("Authorization", "Bearer " + authToken)
            .when()
            .get("/admin/users");
    }
}