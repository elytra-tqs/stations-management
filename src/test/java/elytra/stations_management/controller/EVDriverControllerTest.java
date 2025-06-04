package elytra.stations_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import elytra.stations_management.dto.DriverRegistrationRequest;
import elytra.stations_management.models.EVDriver;
import elytra.stations_management.models.User;
import elytra.stations_management.services.EVDriverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = {"EV_DRIVER"})
class EVDriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EVDriverService evDriverService;

    private EVDriver testDriver;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("evdriver1")
                .email("driver@example.com")
                .firstName("John")
                .lastName("Doe")
                .userType(User.UserType.EV_DRIVER)
                .build();

        testDriver = EVDriver.builder()
                .id(1L)
                .user(testUser)
                .cars(Arrays.asList())
                .build();
    }

    @Test
    void registerDriver_ShouldReturnCreatedDriver() throws Exception {
        DriverRegistrationRequest request = new DriverRegistrationRequest();
        request.setDriver(new EVDriver());
        request.setUser(testUser);

        when(evDriverService.registerDriver(any(EVDriver.class), any(User.class))).thenReturn(testDriver);

        mockMvc.perform(post("/api/v1/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.username").value("evdriver1"))
                .andExpect(jsonPath("$.user.email").value("driver@example.com"));

        verify(evDriverService).registerDriver(any(EVDriver.class), any(User.class));
    }

    @Test
    void registerDriver_ShouldReturnBadRequest_WhenServiceThrowsException() throws Exception {
        DriverRegistrationRequest request = new DriverRegistrationRequest();
        request.setDriver(new EVDriver());
        request.setUser(testUser);

        when(evDriverService.registerDriver(any(EVDriver.class), any(User.class)))
                .thenThrow(new RuntimeException("User already exists"));

        mockMvc.perform(post("/api/v1/drivers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(evDriverService).registerDriver(any(EVDriver.class), any(User.class));
    }

    @Test
    void getDriverById_ShouldReturnDriver() throws Exception {
        when(evDriverService.getDriverById(1L)).thenReturn(testDriver);

        mockMvc.perform(get("/api/v1/drivers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.username").value("evdriver1"));

        verify(evDriverService).getDriverById(1L);
    }

    @Test
    void getDriverById_ShouldReturnNotFound_WhenDriverNotFound() throws Exception {
        when(evDriverService.getDriverById(999L))
                .thenThrow(new RuntimeException("Driver not found"));

        mockMvc.perform(get("/api/v1/drivers/999"))
                .andExpect(status().isNotFound());

        verify(evDriverService).getDriverById(999L);
    }

    @Test
    void getAllDrivers_ShouldReturnDriversList() throws Exception {
        User user2 = User.builder()
                .id(2L)
                .username("evdriver2")
                .email("driver2@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .userType(User.UserType.EV_DRIVER)
                .build();

        EVDriver driver2 = EVDriver.builder()
                .id(2L)
                .user(user2)
                .cars(Arrays.asList())
                .build();

        List<EVDriver> drivers = Arrays.asList(testDriver, driver2);

        when(evDriverService.getAllDrivers()).thenReturn(drivers);

        mockMvc.perform(get("/api/v1/drivers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].user.username").value("evdriver1"))
                .andExpect(jsonPath("$[1].user.username").value("evdriver2"));

        verify(evDriverService).getAllDrivers();
    }

    @Test
    void getAllDrivers_ShouldReturnEmptyList_WhenNoDrivers() throws Exception {
        when(evDriverService.getAllDrivers()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/v1/drivers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(evDriverService).getAllDrivers();
    }

    @Test
    void updateDriver_ShouldReturnUpdatedDriver() throws Exception {
        EVDriver updatedDriverData = EVDriver.builder()
                .user(testUser)
                .build();

        EVDriver updatedDriver = EVDriver.builder()
                .id(1L)
                .user(testUser)
                .cars(Arrays.asList())
                .build();

        when(evDriverService.updateDriver(eq(1L), any(EVDriver.class))).thenReturn(updatedDriver);

        mockMvc.perform(put("/api/v1/drivers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDriverData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.username").value("evdriver1"));

        verify(evDriverService).updateDriver(eq(1L), any(EVDriver.class));
    }

    @Test
    void updateDriver_ShouldReturnBadRequest_WhenServiceThrowsException() throws Exception {
        EVDriver updatedDriverData = EVDriver.builder()
                .user(testUser)
                .build();

        when(evDriverService.updateDriver(eq(1L), any(EVDriver.class)))
                .thenThrow(new RuntimeException("Driver not found"));

        mockMvc.perform(put("/api/v1/drivers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDriverData)))
                .andExpect(status().isBadRequest());

        verify(evDriverService).updateDriver(eq(1L), any(EVDriver.class));
    }

    @Test
    void deleteDriver_ShouldReturnNoContent() throws Exception {
        doNothing().when(evDriverService).deleteDriver(1L);

        mockMvc.perform(delete("/api/v1/drivers/1"))
                .andExpect(status().isNoContent());

        verify(evDriverService).deleteDriver(1L);
    }

    @Test
    void deleteDriver_ShouldReturnNotFound_WhenDriverNotFound() throws Exception {
        doThrow(new RuntimeException("Driver not found")).when(evDriverService).deleteDriver(999L);

        mockMvc.perform(delete("/api/v1/drivers/999"))
                .andExpect(status().isNotFound());

        verify(evDriverService).deleteDriver(999L);
    }
}