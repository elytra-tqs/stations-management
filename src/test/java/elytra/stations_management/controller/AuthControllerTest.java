package elytra.stations_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import elytra.stations_management.dto.AdminRegistrationRequest;
import elytra.stations_management.dto.DriverRegistrationRequest;
import elytra.stations_management.dto.OperatorRegistrationRequest;
import elytra.stations_management.models.*;
import elytra.stations_management.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private EVDriverService evDriverService;

    @MockBean
    private StationOperatorService stationOperatorService;

    @MockBean
    private AdminService adminService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationManager authenticationManager;

    private User testUser;
    private AuthRequest authRequest;
    private EVDriver testDriver;
    private StationOperator testOperator;
    private Admin testAdmin;

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

        testDriver = EVDriver.builder()
                .id(1L)
                .user(testUser)
                .build();

        testOperator = StationOperator.builder()
                .id(1L)
                .user(testUser)
                .station(Station.builder().id(1L).name("Test Station").build())
                .build();

        testAdmin = Admin.builder()
                .id(1L)
                .user(testUser)
                .build();
    }

    @Test
    void login_ShouldReturnTokenAndUserInfo_WhenValidCredentials() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken("testuser")).thenReturn("test-jwt-token-12345");
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);
        when(evDriverService.getDriverByUserId(1L)).thenReturn(testDriver);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token-12345"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.userType").value("EV_DRIVER"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.driverId").value(1));

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
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid username or password"));
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
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid username or password"));

        verify(authenticationManager).authenticate(any(Authentication.class));
    }

    @Test
    void login_ShouldReturnStationOperatorInfo_WhenOperatorHasStation() throws Exception {
        // Given
        User operatorUser = User.builder()
                .id(2L)
                .username("operator1")
                .email("operator@example.com")
                .userType(User.UserType.STATION_OPERATOR)
                .build();

        Station station = Station.builder()
                .id(1L)
                .name("Test Station")
                .build();

        StationOperator operator = StationOperator.builder()
                .id(1L)
                .user(operatorUser)
                .station(station)
                .build();

        AuthRequest operatorAuthRequest = new AuthRequest();
        operatorAuthRequest.setUsername("operator1");
        operatorAuthRequest.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken("operator1")).thenReturn("operator-jwt-token");
        when(userService.getUserByUsername("operator1")).thenReturn(operatorUser);
        when(stationOperatorService.getStationOperatorByUserId(2L)).thenReturn(operator);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(operatorAuthRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("operator-jwt-token"))
                .andExpect(jsonPath("$.username").value("operator1"))
                .andExpect(jsonPath("$.userType").value("STATION_OPERATOR"))
                .andExpect(jsonPath("$.userId").value(2))
                .andExpect(jsonPath("$.operatorId").value(1))
                .andExpect(jsonPath("$.stationId").value(1))
                .andExpect(jsonPath("$.stationName").value("Test Station"));

        verify(stationOperatorService).getStationOperatorByUserId(2L);
    }

    @Test
    void login_ShouldReturnAdminInfo_WhenUserIsAdmin() throws Exception {
        // Given
        User adminUser = User.builder()
                .id(3L)
                .username("admin1")
                .email("admin@example.com")
                .userType(User.UserType.ADMIN)
                .build();

        Admin admin = Admin.builder()
                .id(1L)
                .user(adminUser)
                .build();

        AuthRequest adminAuthRequest = new AuthRequest();
        adminAuthRequest.setUsername("admin1");
        adminAuthRequest.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken("admin1")).thenReturn("admin-jwt-token");
        when(userService.getUserByUsername("admin1")).thenReturn(adminUser);
        when(adminService.getAdminByUserId(3L)).thenReturn(admin);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminAuthRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("admin-jwt-token"))
                .andExpect(jsonPath("$.username").value("admin1"))
                .andExpect(jsonPath("$.userType").value("ADMIN"))
                .andExpect(jsonPath("$.userId").value(3))
                .andExpect(jsonPath("$.adminId").value(1));

        verify(adminService).getAdminByUserId(3L);
    }

    @Test
    void login_ShouldHandleMissingDriverInfo_WhenDriverNotFullySetup() throws Exception {
        // Given
        User driverUser = User.builder()
                .id(4L)
                .username("newdriver")
                .email("newdriver@example.com")
                .userType(User.UserType.EV_DRIVER)
                .build();

        AuthRequest driverAuthRequest = new AuthRequest();
        driverAuthRequest.setUsername("newdriver");
        driverAuthRequest.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken("newdriver")).thenReturn("driver-jwt-token");
        when(userService.getUserByUsername("newdriver")).thenReturn(driverUser);
        when(evDriverService.getDriverByUserId(4L))
                .thenThrow(new RuntimeException("Driver not found"));

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(driverAuthRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("driver-jwt-token"))
                .andExpect(jsonPath("$.username").value("newdriver"))
                .andExpect(jsonPath("$.userType").value("EV_DRIVER"))
                .andExpect(jsonPath("$.userId").value(4))
                .andExpect(jsonPath("$.driverId").doesNotExist());

        verify(evDriverService).getDriverByUserId(4L);
    }

    @Test
    void login_ShouldHandleMissingOperatorInfo_WhenOperatorNotFullySetup() throws Exception {
        // Given
        User operatorUser = User.builder()
                .id(5L)
                .username("newoperator")
                .email("newoperator@example.com")
                .userType(User.UserType.STATION_OPERATOR)
                .build();

        AuthRequest operatorAuthRequest = new AuthRequest();
        operatorAuthRequest.setUsername("newoperator");
        operatorAuthRequest.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken("newoperator")).thenReturn("operator-jwt-token");
        when(userService.getUserByUsername("newoperator")).thenReturn(operatorUser);
        when(stationOperatorService.getStationOperatorByUserId(5L))
                .thenThrow(new RuntimeException("Operator not found"));

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(operatorAuthRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("operator-jwt-token"))
                .andExpect(jsonPath("$.username").value("newoperator"))
                .andExpect(jsonPath("$.userType").value("STATION_OPERATOR"))
                .andExpect(jsonPath("$.userId").value(5))
                .andExpect(jsonPath("$.operatorId").doesNotExist())
                .andExpect(jsonPath("$.stationId").doesNotExist());

        verify(stationOperatorService).getStationOperatorByUserId(5L);
    }

    @Test
    void login_ShouldHandleMissingAdminInfo_WhenAdminNotFullySetup() throws Exception {
        // Given
        User adminUser = User.builder()
                .id(6L)
                .username("newadmin")
                .email("newadmin@example.com")
                .userType(User.UserType.ADMIN)
                .build();

        AuthRequest adminAuthRequest = new AuthRequest();
        adminAuthRequest.setUsername("newadmin");
        adminAuthRequest.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken("newadmin")).thenReturn("admin-jwt-token");
        when(userService.getUserByUsername("newadmin")).thenReturn(adminUser);
        when(adminService.getAdminByUserId(6L))
                .thenThrow(new RuntimeException("Admin not found"));

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminAuthRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("admin-jwt-token"))
                .andExpect(jsonPath("$.username").value("newadmin"))
                .andExpect(jsonPath("$.userType").value("ADMIN"))
                .andExpect(jsonPath("$.userId").value(6))
                .andExpect(jsonPath("$.adminId").doesNotExist());

        verify(adminService).getAdminByUserId(6L);
    }

    @Test
    void login_ShouldReturnOperatorInfo_WhenOperatorHasNoStation() throws Exception {
        // Given
        User operatorUser = User.builder()
                .id(7L)
                .username("operator_no_station")
                .email("operator_no_station@example.com")
                .userType(User.UserType.STATION_OPERATOR)
                .build();

        StationOperator operator = StationOperator.builder()
                .id(2L)
                .user(operatorUser)
                .station(null) // No station assigned
                .build();

        AuthRequest operatorAuthRequest = new AuthRequest();
        operatorAuthRequest.setUsername("operator_no_station");
        operatorAuthRequest.setPassword("password");

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authentication);
        when(jwtService.generateToken("operator_no_station")).thenReturn("operator-jwt-token");
        when(userService.getUserByUsername("operator_no_station")).thenReturn(operatorUser);
        when(stationOperatorService.getStationOperatorByUserId(7L)).thenReturn(operator);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(operatorAuthRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("operator-jwt-token"))
                .andExpect(jsonPath("$.username").value("operator_no_station"))
                .andExpect(jsonPath("$.userType").value("STATION_OPERATOR"))
                .andExpect(jsonPath("$.userId").value(7))
                .andExpect(jsonPath("$.operatorId").value(2))
                .andExpect(jsonPath("$.stationId").doesNotExist())
                .andExpect(jsonPath("$.stationName").doesNotExist());

        verify(stationOperatorService).getStationOperatorByUserId(7L);
    }

    @Test
    void registerDriver_ShouldCreateDriverAndReturnToken() throws Exception {
        DriverRegistrationRequest request = new DriverRegistrationRequest();
        request.setUser(User.builder()
                .username("newdriver")
                .password("password123")
                .email("driver@example.com")
                .firstName("New")
                .lastName("Driver")
                .build());
        request.setDriver(EVDriver.builder()
                .build());

        when(evDriverService.registerDriver(any(EVDriver.class), any(User.class)))
                .thenReturn(testDriver);
        when(jwtService.generateToken("newdriver")).thenReturn("new-driver-token");

        mockMvc.perform(post("/api/v1/auth/register/driver")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("new-driver-token"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.userType").value("EV_DRIVER"))
                .andExpect(jsonPath("$.driverId").value(1));
    }

    @Test
    void registerDriver_ShouldReturn400_WhenRegistrationFails() throws Exception {
        DriverRegistrationRequest request = new DriverRegistrationRequest();
        request.setUser(User.builder()
                .username("newdriver")
                .password("password123")
                .email("driver@example.com")
                .firstName("New")
                .lastName("Driver")
                .build());
        request.setDriver(EVDriver.builder()
                .build());

        when(evDriverService.registerDriver(any(EVDriver.class), any(User.class)))
                .thenThrow(new RuntimeException("Username already exists"));

        mockMvc.perform(post("/api/v1/auth/register/driver")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username already exists"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerOperator_ShouldCreateOperator_WhenAdmin() throws Exception {
        OperatorRegistrationRequest request = new OperatorRegistrationRequest();
        request.setUser(User.builder()
                .username("newoperator")
                .password("password123")
                .email("operator@example.com")
                .firstName("New")
                .lastName("Operator")
                .build());
        request.setOperator(StationOperator.builder().build());
        request.setStationId(1L);

        when(stationOperatorService.registerStationOperator(
                any(StationOperator.class), any(User.class), anyLong()))
                .thenReturn(testOperator);

        mockMvc.perform(post("/api/v1/auth/register/operator")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Station operator registered successfully"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.userType").value("STATION_OPERATOR"))
                .andExpect(jsonPath("$.operatorId").value(1))
                .andExpect(jsonPath("$.stationId").value(1));
    }

    @Test
    void registerOperator_ShouldReturn403_WhenNotAdmin() throws Exception {
        OperatorRegistrationRequest request = new OperatorRegistrationRequest();
        request.setUser(User.builder()
                .username("newoperator")
                .password("password123")
                .email("operator@example.com")
                .firstName("New")
                .lastName("Operator")
                .build());
        request.setOperator(StationOperator.builder().build());
        request.setStationId(1L);

        mockMvc.perform(post("/api/v1/auth/register/operator")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerAdmin_ShouldCreateAdmin_WhenAdmin() throws Exception {
        AdminRegistrationRequest request = new AdminRegistrationRequest();
        request.setUser(User.builder()
                .username("newadmin")
                .password("password123")
                .email("admin@example.com")
                .firstName("New")
                .lastName("Admin")
                .build());
        request.setAdmin(Admin.builder().build());

        when(adminService.registerAdmin(any(Admin.class), any(User.class)))
                .thenReturn(testAdmin);

        mockMvc.perform(post("/api/v1/auth/register/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Admin registered successfully"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.userType").value("ADMIN"))
                .andExpect(jsonPath("$.adminId").value(1));
    }

    @Test
    @WithMockUser(username = "testuser")
    void me_ShouldReturnCurrentUserInfo_WithValidToken() throws Exception {
        String token = "Bearer test-jwt-token-12345";
        
        when(jwtService.extractUsername("test-jwt-token-12345")).thenReturn("testuser");
        when(userService.getUserByUsername("testuser")).thenReturn(testUser);

        mockMvc.perform(get("/api/v1/auth/me")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.userType").value("EV_DRIVER"))
                .andExpect(jsonPath("$.userId").value(1));
    }
}