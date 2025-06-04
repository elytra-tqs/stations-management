package elytra.stations_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import elytra.stations_management.models.AuthRequest;
import elytra.stations_management.models.User;
import elytra.stations_management.services.JwtService;
import elytra.stations_management.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationManager authenticationManager;

    private User testUser;
    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .userType(User.UserType.EV_DRIVER)
                .build();

        authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password123");
    }

    @Test
    void welcomeEndpoint_ShouldReturnWelcomeMessage() throws Exception {
        mockMvc.perform(get("/api/v1/auth/welcome"))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome this endpoint is not secure"));
    }

    @Test
    void addNewUser_ShouldCreateUser_WhenValidData() throws Exception {
        User newUser = User.builder()
                .username("newuser")
                .password("password123")
                .email("new@example.com")
                .firstName("New")
                .lastName("User")
                .userType(User.UserType.EV_DRIVER)
                .build();

        when(userService.registerUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/v1/auth/addNewUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.userType").value("EV_DRIVER"));
    }

    @Test
    void generateToken_ShouldReturnToken_WhenValidCredentials() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken("testuser")).thenReturn("test-jwt-token-12345");

        mockMvc.perform(post("/api/v1/auth/generateToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("test-jwt-token-12345"));

        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(jwtService).generateToken("testuser");
    }

    @Test
    void generateToken_ShouldReturn401_WhenInvalidCredentials() throws Exception {
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/v1/auth/generateToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllUsers_ShouldReturnUsersList() throws Exception {
        List<User> users = Arrays.asList(testUser, 
                User.builder()
                    .id(2L)
                    .username("user2")
                    .email("user2@example.com")
                    .firstName("User")
                    .lastName("Two")
                    .userType(User.UserType.STATION_OPERATOR)
                    .build());

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/auth/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/v1/auth/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void login_ShouldReturnTokenAndUsername_WhenValidCredentials() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken("testuser")).thenReturn("test-jwt-token-12345");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token-12345"))
                .andExpect(jsonPath("$.username").value("testuser"));

        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(jwtService).generateToken("testuser");
    }

    @Test
    void login_ShouldReturn401_WhenInvalidCredentials() throws Exception {
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void generateToken_ShouldReturn401_WhenAuthenticationNotAuthenticated() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authentication);

        mockMvc.perform(post("/api/v1/auth/generateToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());

        verify(authenticationManager).authenticate(any(Authentication.class));
    }

    @Test
    void login_ShouldReturn401_WhenAuthenticationNotAuthenticated() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authentication);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());

        verify(authenticationManager).authenticate(any(Authentication.class));
    }

    @Test
    void addNewUser_ShouldReturn500_WhenServiceThrowsException() throws Exception {
        User newUser = User.builder()
                .username("newuser")
                .password("password123")
                .email("new@example.com")
                .firstName("New")
                .lastName("User")
                .userType(User.UserType.EV_DRIVER)
                .build();

        when(userService.registerUser(any(User.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/v1/auth/addNewUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isInternalServerError());

        verify(userService).registerUser(any(User.class));
    }
}