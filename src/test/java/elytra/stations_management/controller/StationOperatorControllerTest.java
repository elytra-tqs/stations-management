package elytra.stations_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import elytra.stations_management.dto.OperatorRegistrationRequest;
import elytra.stations_management.models.Station;
import elytra.stations_management.models.StationOperator;
import elytra.stations_management.models.User;
import elytra.stations_management.services.StationOperatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StationOperatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StationOperatorService stationOperatorService;

    private StationOperator testOperator;
    private User testUser;
    private Station testStation;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("operator1")
                .email("operator@example.com")
                .firstName("Operator")
                .lastName("User")
                .userType(User.UserType.STATION_OPERATOR)
                .build();

        testStation = Station.builder()
                .id(1L)
                .name("Test Station")
                .latitude(40.0)
                .longitude(-8.0)
                .build();

        testOperator = StationOperator.builder()
                .id(1L)
                .user(testUser)
                .station(testStation)
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerStationOperator_ShouldCreateOperator() throws Exception {
        // Given
        OperatorRegistrationRequest request = new OperatorRegistrationRequest();
        request.setOperator(StationOperator.builder().build());
        request.setUser(User.builder()
                .username("newoperator")
                .password("password123")
                .email("newop@example.com")
                .build());
        request.setStationId(1L);

        when(stationOperatorService.registerStationOperator(any(), any(), eq(1L)))
                .thenReturn(testOperator);

        // When & Then
        mockMvc.perform(post("/api/v1/station-operators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.username").value("operator1"));

        verify(stationOperatorService).registerStationOperator(any(), any(), eq(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerStationOperator_ShouldReturn400_WhenRegistrationFails() throws Exception {
        // Given
        OperatorRegistrationRequest request = new OperatorRegistrationRequest();
        request.setOperator(StationOperator.builder().build());
        request.setUser(User.builder().username("newoperator").build());
        request.setStationId(1L);

        when(stationOperatorService.registerStationOperator(any(), any(), eq(1L)))
                .thenThrow(new RuntimeException("Station already has an operator"));

        // When & Then
        mockMvc.perform(post("/api/v1/station-operators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(stationOperatorService).registerStationOperator(any(), any(), eq(1L));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "STATION_OPERATOR"})
    void getStationOperatorById_ShouldReturnOperator_WhenExists() throws Exception {
        // Given
        when(stationOperatorService.getStationOperatorById(1L)).thenReturn(testOperator);

        // When & Then
        mockMvc.perform(get("/api/v1/station-operators/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.username").value("operator1"))
                .andExpect(jsonPath("$.user.email").value("operator@example.com"));

        verify(stationOperatorService).getStationOperatorById(1L);
    }

    @Test
    @WithMockUser(roles = "STATION_OPERATOR")
    void getStationOperatorByUserId_ShouldReturnOperator() throws Exception {
        // Given
        when(stationOperatorService.getStationOperatorByUserId(1L)).thenReturn(testOperator);

        // When & Then
        mockMvc.perform(get("/api/v1/station-operators/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.id").value(1));

        verify(stationOperatorService).getStationOperatorByUserId(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getStationOperatorByStationId_ShouldReturnOperator() throws Exception {
        // Given
        when(stationOperatorService.getStationOperatorByStationId(1L)).thenReturn(testOperator);

        // When & Then
        mockMvc.perform(get("/api/v1/station-operators/station/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                ;

        verify(stationOperatorService).getStationOperatorByStationId(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllStationOperators_ShouldReturnOperatorsList() throws Exception {
        // Given
        StationOperator operator2 = StationOperator.builder()
                .id(2L)
                .user(User.builder().username("operator2").build())
                .build();
        
        List<StationOperator> operators = Arrays.asList(testOperator, operator2);
        when(stationOperatorService.getAllStationOperators()).thenReturn(operators);

        // When & Then
        mockMvc.perform(get("/api/v1/station-operators"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].user.username").value("operator1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].user.username").value("operator2"));

        verify(stationOperatorService).getAllStationOperators();
    }

    @Test
    @WithMockUser(roles = "STATION_OPERATOR")
    void claimStation_ShouldAssignStation() throws Exception {
        // Given
        StationOperator operatorWithStation = StationOperator.builder()
                .id(2L)
                .user(testUser)
                .station(testStation)
                .build();

        when(stationOperatorService.claimStation(2L, 1L)).thenReturn(operatorWithStation);

        // When & Then
        mockMvc.perform(post("/api/v1/station-operators/2/claim-station/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Station claimed successfully"))
                .andExpect(jsonPath("$.operatorId").value(2))
                .andExpect(jsonPath("$.stationId").value(1))
                .andExpect(jsonPath("$.stationName").value("Test Station"));

        verify(stationOperatorService).claimStation(2L, 1L);
    }

    @Test
    @WithMockUser(roles = "STATION_OPERATOR")
    void claimStation_ShouldReturn400_WhenClaimFails() throws Exception {
        // Given
        when(stationOperatorService.claimStation(1L, 2L))
                .thenThrow(new RuntimeException("Operator already manages a station"));

        // When & Then
        mockMvc.perform(post("/api/v1/station-operators/1/claim-station/2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Operator already manages a station"));

        verify(stationOperatorService).claimStation(1L, 2L);
    }

    @Test
    @WithMockUser(roles = "STATION_OPERATOR")
    void releaseStation_ShouldRemoveStation() throws Exception {
        // Given
        StationOperator operatorWithoutStation = StationOperator.builder()
                .id(1L)
                .user(testUser)
                .station(null)
                .build();

        when(stationOperatorService.releaseStation(1L)).thenReturn(operatorWithoutStation);

        // When & Then
        mockMvc.perform(post("/api/v1/station-operators/1/release-station"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Station released successfully"))
                .andExpect(jsonPath("$.operatorId").value(1));

        verify(stationOperatorService).releaseStation(1L);
    }

    @Test
    @WithMockUser(roles = "STATION_OPERATOR")
    void getAvailableStations_ShouldReturnStationsList() throws Exception {
        // Given
        Station station2 = Station.builder()
                .id(2L)
                .name("Available Station")
                .latitude(41.0)
                .longitude(-7.0)
                .build();
        
        List<Station> availableStations = Arrays.asList(station2);
        when(stationOperatorService.getAvailableStations()).thenReturn(availableStations);

        // When & Then
        mockMvc.perform(get("/api/v1/station-operators/available-stations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Available Station"))
                .andExpect(jsonPath("$[0].latitude").value(41.0))
                .andExpect(jsonPath("$[0].longitude").value(-7.0));

        verify(stationOperatorService).getAvailableStations();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateStationOperator_ShouldUpdateAndReturnOperator() throws Exception {
        // Given
        StationOperator updateRequest = StationOperator.builder()
                .user(User.builder().email("updated@example.com").build())
                .build();

        StationOperator updatedOperator = StationOperator.builder()
                .id(1L)
                .user(User.builder()
                        .id(1L)
                        .username("operator1")
                        .email("updated@example.com")
                        .userType(User.UserType.STATION_OPERATOR)
                        .build())
                .station(testStation)
                .build();

        when(stationOperatorService.updateStationOperator(eq(1L), any()))
                .thenReturn(updatedOperator);

        // When & Then
        mockMvc.perform(put("/api/v1/station-operators/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.email").value("updated@example.com"));

        verify(stationOperatorService).updateStationOperator(eq(1L), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteStationOperator_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(stationOperatorService).deleteStationOperator(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/station-operators/1"))
                .andExpect(status().isNoContent());

        verify(stationOperatorService).deleteStationOperator(1L);
    }

    @Test
    void getAllStationOperators_ShouldReturn403_WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/station-operators"))
                .andExpect(status().isForbidden());

        verify(stationOperatorService, never()).getAllStationOperators();
    }

    @Test
    @WithMockUser(username = "driver", authorities = "ROLE_EV_DRIVER")
    void registerStationOperator_ShouldReturn403_WhenNotAdmin() throws Exception {
        // Given
        OperatorRegistrationRequest request = new OperatorRegistrationRequest();

        // When & Then
        mockMvc.perform(post("/api/v1/station-operators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(stationOperatorService, never()).registerStationOperator(any(), any(), any());
    }

    @Test
    @WithMockUser(roles = "STATION_OPERATOR")
    void getStationOperatorById_ShouldReturn404_WhenNotFound() throws Exception {
        // Given
        when(stationOperatorService.getStationOperatorById(99L))
                .thenThrow(new RuntimeException("Station operator not found"));

        // When & Then
        mockMvc.perform(get("/api/v1/station-operators/99"))
                .andExpect(status().isNotFound());

        verify(stationOperatorService).getStationOperatorById(99L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getStationOperatorByStationId_ShouldReturn404_WhenNotFound() throws Exception {
        // Given
        when(stationOperatorService.getStationOperatorByStationId(99L))
                .thenThrow(new RuntimeException("No operator found for station"));

        // When & Then
        mockMvc.perform(get("/api/v1/station-operators/station/99"))
                .andExpect(status().isNotFound());

        verify(stationOperatorService).getStationOperatorByStationId(99L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateStationOperator_ShouldReturn400_WhenInvalidData() throws Exception {
        // Given
        StationOperator invalidUpdate = StationOperator.builder()
                .user(User.builder().build())
                .build();

        when(stationOperatorService.updateStationOperator(eq(1L), any()))
                .thenThrow(new RuntimeException("Invalid update data"));

        // When & Then
        mockMvc.perform(put("/api/v1/station-operators/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdate)))
                .andExpect(status().isBadRequest());

        verify(stationOperatorService).updateStationOperator(eq(1L), any());
    }

    @Test
    @WithMockUser(roles = "STATION_OPERATOR")
    void releaseStation_ShouldReturn400_WhenNoStationAssigned() throws Exception {
        // Given
        when(stationOperatorService.releaseStation(2L))
                .thenThrow(new RuntimeException("Operator doesn't manage any station"));

        // When & Then
        mockMvc.perform(post("/api/v1/station-operators/2/release-station"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Operator doesn't manage any station"));

        verify(stationOperatorService).releaseStation(2L);
    }

    @Test
    @WithMockUser(roles = "STATION_OPERATOR")
    void getStationOperatorByUserId_ShouldReturn404_WhenNotFound() throws Exception {
        // Given
        when(stationOperatorService.getStationOperatorByUserId(99L))
                .thenThrow(new RuntimeException("Station operator not found for user"));

        // When & Then
        mockMvc.perform(get("/api/v1/station-operators/user/99"))
                .andExpect(status().isNotFound());

        verify(stationOperatorService).getStationOperatorByUserId(99L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void registerStationOperator_ShouldCreateOperatorWithoutStation_WhenStationIdNull() throws Exception {
        // Given
        OperatorRegistrationRequest request = new OperatorRegistrationRequest();
        request.setOperator(StationOperator.builder().build());
        request.setUser(User.builder()
                .username("operatornostation")
                .password("password123")
                .email("nostation@example.com")
                .build());
        request.setStationId(null);

        StationOperator operatorWithoutStation = StationOperator.builder()
                .id(3L)
                .user(User.builder()
                        .id(3L)
                        .username("operatornostation")
                        .email("nostation@example.com")
                        .userType(User.UserType.STATION_OPERATOR)
                        .build())
                .station(null)
                .build();

        when(stationOperatorService.registerStationOperator(any(), any(), isNull()))
                .thenReturn(operatorWithoutStation);

        // When & Then
        mockMvc.perform(post("/api/v1/station-operators")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.user.username").value("operatornostation"))
                .andExpect(jsonPath("$.station").doesNotExist());

        verify(stationOperatorService).registerStationOperator(any(), any(), isNull());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteStationOperator_ShouldReturn404_WhenNotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Station operator not found"))
                .when(stationOperatorService).deleteStationOperator(99L);

        // When & Then
        mockMvc.perform(delete("/api/v1/station-operators/99"))
                .andExpect(status().isNotFound());

        verify(stationOperatorService).deleteStationOperator(99L);
    }

    @Test
    @WithMockUser(username = "driver", roles = "EV_DRIVER")
    void getAllStationOperators_ShouldReturn403_WhenNotAuthorized() throws Exception {
        mockMvc.perform(get("/api/v1/station-operators"))
                .andExpect(status().isForbidden());

        verify(stationOperatorService, times(0)).getAllStationOperators();
    }

    @Test
    @WithMockUser(username = "driver", authorities = "ROLE_EV_DRIVER")
    void claimStation_ShouldReturn403_WhenNotAuthorized() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/station-operators/1/claim-station/1"))
                .andExpect(status().isForbidden());

        verify(stationOperatorService, never()).claimStation(anyLong(), anyLong());
    }

    @Test
    @WithMockUser(username = "driver", authorities = "ROLE_EV_DRIVER")
    void deleteStationOperator_ShouldReturn403_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/station-operators/1"))
                .andExpect(status().isForbidden());

        verify(stationOperatorService, never()).deleteStationOperator(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateStationOperator_ShouldReturn400_WhenOperatorNotFound() throws Exception {
        // Given
        StationOperator updateRequest = StationOperator.builder()
                .user(User.builder().email("updated@example.com").build())
                .build();

        when(stationOperatorService.updateStationOperator(eq(99L), any()))
                .thenThrow(new RuntimeException("Station operator not found"));

        // When & Then
        mockMvc.perform(put("/api/v1/station-operators/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());

        verify(stationOperatorService).updateStationOperator(eq(99L), any());
    }
}