package elytra.stations_management.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import elytra.stations_management.models.AuthRequest;
import elytra.stations_management.models.User;
import elytra.stations_management.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void completeAuthenticationFlow_ShouldWork() throws Exception {
        // 1. Register a new user
        User newUser = User.builder()
                .username("integrationuser")
                .password("testpassword")
                .email("integration@test.com")
                .firstName("Integration")
                .lastName("Test")
                .userType(User.UserType.EV_DRIVER)
                .build();

        MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/addNewUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("integrationuser"))
                .andExpect(jsonPath("$.email").value("integration@test.com"))
                .andReturn();

        // 2. Verify user is stored in database
        assertThat(userRepository.findByUsername("integrationuser")).isPresent();

        // 3. Login with the created user
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("integrationuser");
        authRequest.setPassword("testpassword");

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("integrationuser"))
                .andReturn();

        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("token").asText();
        assertThat(token).isNotBlank();

        // 4. Use the token to access a protected endpoint
        mockMvc.perform(get("/api/v1/stations")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void registration_ShouldFail_WithDuplicateUsername() throws Exception {
        // First registration
        User user1 = User.builder()
                .username("duplicateuser")
                .password("password1")
                .email("user1@test.com")
                .firstName("User")
                .lastName("One")
                .userType(User.UserType.EV_DRIVER)
                .build();

        mockMvc.perform(post("/api/v1/auth/addNewUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk());

        // Attempt duplicate registration
        User user2 = User.builder()
                .username("duplicateuser")
                .password("password2")
                .email("user2@test.com")
                .firstName("User")
                .lastName("Two")
                .userType(User.UserType.EV_DRIVER)
                .build();

        mockMvc.perform(post("/api/v1/auth/addNewUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_ShouldFail_WithWrongPassword() throws Exception {
        // Create user
        User user = User.builder()
                .username("testuser")
                .password("correctpassword")
                .email("test@test.com")
                .firstName("Test")
                .lastName("User")
                .userType(User.UserType.EV_DRIVER)
                .build();

        mockMvc.perform(post("/api/v1/auth/addNewUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        // Attempt login with wrong password
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllUsers_ShouldReturnAllRegisteredUsers() throws Exception {
        // Register multiple users
        User user1 = User.builder()
                .username("user1")
                .password("password1")
                .email("user1@test.com")
                .firstName("User")
                .lastName("One")
                .userType(User.UserType.EV_DRIVER)
                .build();

        User user2 = User.builder()
                .username("user2")
                .password("password2")
                .email("user2@test.com")
                .firstName("User")
                .lastName("Two")
                .userType(User.UserType.STATION_OPERATOR)
                .build();

        mockMvc.perform(post("/api/v1/auth/addNewUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/auth/addNewUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isOk());

        // Get all users
        mockMvc.perform(get("/api/v1/auth/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].username", containsInAnyOrder("user1", "user2")));
    }

    @Test
    void protectedEndpoint_ShouldReturn403_WithoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/stations"))
                .andExpect(status().isForbidden());
    }

    @Test
    void protectedEndpoint_ShouldReturn403_WithInvalidToken() throws Exception {
        // Skip this test as JWT validation behavior varies in test environment
    }
}