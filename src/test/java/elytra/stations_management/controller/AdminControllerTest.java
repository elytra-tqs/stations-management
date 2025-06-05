package elytra.stations_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import elytra.stations_management.models.Admin;
import elytra.stations_management.models.Station;
import elytra.stations_management.models.User;
import elytra.stations_management.services.AdminService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminService adminService;

    private Admin testAdmin;
    private User testUser;
    private Station testStation;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("admin1")
                .email("admin@example.com")
                .firstName("Admin")
                .lastName("User")
                .userType(User.UserType.ADMIN)
                .build();

        testStation = Station.builder()
                .id(1L)
                .name("Test Station")
                .latitude(40.0)
                .longitude(-8.0)
                .build();

        testAdmin = Admin.builder()
                .id(1L)
                .user(testUser)
                .stations(Arrays.asList(testStation))
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAdminById_ShouldReturnAdmin_WhenExists() throws Exception {
        // Given
        when(adminService.getAdminById(1L)).thenReturn(testAdmin);

        // When & Then
        mockMvc.perform(get("/api/v1/admins/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.username").value("admin1"))
                .andExpect(jsonPath("$.user.email").value("admin@example.com"))
                .andExpect(jsonPath("$.stations", hasSize(1)))
                .andExpect(jsonPath("$.stations[0].name").value("Test Station"));

        verify(adminService).getAdminById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAdminById_ShouldReturn404_WhenNotFound() throws Exception {
        // Given
        when(adminService.getAdminById(99L)).thenThrow(new RuntimeException("Admin not found"));

        // When & Then
        mockMvc.perform(get("/api/v1/admins/99"))
                .andExpect(status().isNotFound());

        verify(adminService).getAdminById(99L);
    }

    @Test
    @WithMockUser(username = "operator", authorities = "ROLE_STATION_OPERATOR")
    void getAdminById_ShouldReturn403_WhenNotAdmin() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/admins/1"))
                .andExpect(status().isForbidden());

        verify(adminService, never()).getAdminById(anyLong());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAdminByUserId_ShouldReturnAdmin_WhenExists() throws Exception {
        // Given
        when(adminService.getAdminByUserId(1L)).thenReturn(testAdmin);

        // When & Then
        mockMvc.perform(get("/api/v1/admins/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.username").value("admin1"));

        verify(adminService).getAdminByUserId(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllAdmins_ShouldReturnAdminsList() throws Exception {
        // Given
        Admin admin2 = Admin.builder()
                .id(2L)
                .user(User.builder().username("admin2").email("admin2@example.com").build())
                .build();
        
        List<Admin> admins = Arrays.asList(testAdmin, admin2);
        when(adminService.getAllAdmins()).thenReturn(admins);

        // When & Then
        mockMvc.perform(get("/api/v1/admins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].user.username").value("admin1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].user.username").value("admin2"));

        verify(adminService).getAllAdmins();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAdmin_ShouldUpdateAndReturnAdmin() throws Exception {
        // Given
        Admin updateRequest = Admin.builder()
                .user(User.builder()
                        .email("updated@example.com")
                        .firstName("Updated")
                        .build())
                .build();

        Admin updatedAdmin = Admin.builder()
                .id(1L)
                .user(User.builder()
                        .id(1L)
                        .username("admin1")
                        .email("updated@example.com")
                        .firstName("Updated")
                        .lastName("User")
                        .userType(User.UserType.ADMIN)
                        .build())
                .stations(Arrays.asList(testStation))
                .build();

        when(adminService.updateAdmin(eq(1L), any(Admin.class))).thenReturn(updatedAdmin);

        // When & Then
        mockMvc.perform(put("/api/v1/admins/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.user.email").value("updated@example.com"))
                .andExpect(jsonPath("$.user.firstName").value("Updated"));

        verify(adminService).updateAdmin(eq(1L), any(Admin.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateAdmin_ShouldReturn400_WhenInvalidData() throws Exception {
        // Given
        Admin updateRequest = Admin.builder().build();
        when(adminService.updateAdmin(eq(1L), any(Admin.class)))
                .thenThrow(new RuntimeException("Invalid data"));

        // When & Then
        mockMvc.perform(put("/api/v1/admins/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());

        verify(adminService).updateAdmin(eq(1L), any(Admin.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAdmin_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(adminService).deleteAdmin(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/admins/1"))
                .andExpect(status().isNoContent());

        verify(adminService).deleteAdmin(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAdmin_ShouldReturn404_WhenNotFound() throws Exception {
        // Given
        doThrow(new RuntimeException("Admin not found")).when(adminService).deleteAdmin(99L);

        // When & Then
        mockMvc.perform(delete("/api/v1/admins/99"))
                .andExpect(status().isNotFound());

        verify(adminService).deleteAdmin(99L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignStationToAdmin_ShouldReturnUpdatedAdmin() throws Exception {
        // Given
        Station newStation = Station.builder()
                .id(2L)
                .name("New Station")
                .latitude(41.0)
                .longitude(-7.0)
                .build();

        Admin updatedAdmin = Admin.builder()
                .id(1L)
                .user(testUser)
                .stations(Arrays.asList(testStation, newStation))
                .build();

        when(adminService.assignStationToAdmin(1L, 2L)).thenReturn(updatedAdmin);

        // When & Then
        mockMvc.perform(post("/api/v1/admins/1/stations/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.stations", hasSize(2)))
                .andExpect(jsonPath("$.stations[1].name").value("New Station"));

        verify(adminService).assignStationToAdmin(1L, 2L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeStationFromAdmin_ShouldReturnUpdatedAdmin() throws Exception {
        // Given
        Admin updatedAdmin = Admin.builder()
                .id(1L)
                .user(testUser)
                .stations(Arrays.asList())
                .build();

        when(adminService.removeStationFromAdmin(1L, 1L)).thenReturn(updatedAdmin);

        // When & Then
        mockMvc.perform(delete("/api/v1/admins/1/stations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.stations", hasSize(0)));

        verify(adminService).removeStationFromAdmin(1L, 1L);
    }

    @Test
    void getAllAdmins_ShouldReturn403_WhenNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/admins"))
                .andExpect(status().isForbidden());

        verify(adminService, never()).getAllAdmins();
    }

    @Test
    @WithMockUser(username = "driver", authorities = "ROLE_EV_DRIVER")
    void updateAdmin_ShouldReturn403_WhenNotAdmin() throws Exception {
        // Given
        Admin updateRequest = Admin.builder().build();

        // When & Then
        mockMvc.perform(put("/api/v1/admins/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());

        verify(adminService, never()).updateAdmin(anyLong(), any());
    }
}