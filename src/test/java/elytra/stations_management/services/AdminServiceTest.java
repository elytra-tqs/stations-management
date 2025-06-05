package elytra.stations_management.services;

import elytra.stations_management.models.Admin;
import elytra.stations_management.models.Station;
import elytra.stations_management.models.User;
import elytra.stations_management.repositories.AdminRepository;
import elytra.stations_management.repositories.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private UserService userService;

    @InjectMocks
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
                .stations(new ArrayList<>(Arrays.asList(testStation)))
                .build();
    }

    @Test
    void registerAdmin_ShouldCreateAdmin_WhenValidData() {
        // Given
        Admin newAdmin = Admin.builder().build();
        User newUser = User.builder()
                .username("newadmin")
                .password("password")
                .email("newadmin@example.com")
                .build();
        
        User savedUser = User.builder()
                .id(2L)
                .username("newadmin")
                .email("newadmin@example.com")
                .userType(User.UserType.ADMIN)
                .build();

        when(userService.registerUser(any(User.class))).thenReturn(savedUser);
        when(adminRepository.save(any(Admin.class))).thenReturn(testAdmin);

        // When
        Admin result = adminService.registerAdmin(newAdmin, newUser);

        // Then
        assertThat(result)
                .isNotNull()
                .isEqualTo(testAdmin)
                .extracting(Admin::getId, Admin::getUser)
                .containsExactly(1L, testUser);

        verify(userService).registerUser(argThat(user -> 
            user.getUserType() == User.UserType.ADMIN &&
            user.getUsername().equals("newadmin")
        ));
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    void registerAdmin_ShouldThrowException_WhenUserAlreadyAdmin() {
        // Given
        Admin newAdmin = Admin.builder().build();
        User existingUser = User.builder()
                .id(1L)
                .username("existing")
                .build();

        when(adminRepository.existsByUserId(1L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> adminService.registerAdmin(newAdmin, existingUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User is already an admin");

        verify(adminRepository).existsByUserId(1L);
        verify(userService, never()).registerUser(any());
        verify(adminRepository, never()).save(any());
    }

    @Test
    void getAdminById_ShouldReturnAdmin_WhenExists() {
        // Given
        when(adminRepository.findById(1L)).thenReturn(Optional.of(testAdmin));

        // When
        Admin result = adminService.getAdminById(1L);

        // Then
        assertThat(result)
                .isNotNull()
                .isEqualTo(testAdmin)
                .satisfies(admin -> {
                    assertThat(admin.getId()).isEqualTo(1L);
                    assertThat(admin.getUser()).isEqualTo(testUser);
                    assertThat(admin.getStations()).hasSize(1);
                });

        verify(adminRepository).findById(1L);
    }

    @Test
    void getAdminById_ShouldThrowException_WhenNotFound() {
        // Given
        when(adminRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> adminService.getAdminById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Admin not found");

        verify(adminRepository).findById(99L);
    }

    @Test
    void getAdminByUserId_ShouldReturnAdmin_WhenExists() {
        // Given
        when(adminRepository.findByUserId(1L)).thenReturn(Optional.of(testAdmin));

        // When
        Admin result = adminService.getAdminByUserId(1L);

        // Then
        assertThat(result)
                .isNotNull()
                .isEqualTo(testAdmin)
                .extracting(Admin::getUser)
                .extracting(User::getId)
                .isEqualTo(1L);

        verify(adminRepository).findByUserId(1L);
    }

    @Test
    void getAdminByUserId_ShouldThrowException_WhenNotFound() {
        // Given
        when(adminRepository.findByUserId(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> adminService.getAdminByUserId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Admin not found for user");

        verify(adminRepository).findByUserId(99L);
    }

    @Test
    void getAllAdmins_ShouldReturnAllAdmins() {
        // Given
        Admin admin2 = Admin.builder()
                .id(2L)
                .user(User.builder().username("admin2").build())
                .build();
        
        List<Admin> admins = Arrays.asList(testAdmin, admin2);
        when(adminRepository.findAll()).thenReturn(admins);

        // When
        List<Admin> result = adminService.getAllAdmins();

        // Then
        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .containsExactly(testAdmin, admin2)
                .extracting(Admin::getId)
                .containsExactly(1L, 2L);

        verify(adminRepository).findAll();
    }

    @Test
    void updateAdmin_ShouldUpdateUserInfo_WhenValidData() {
        // Given
        Admin updatedAdmin = Admin.builder()
                .user(User.builder()
                        .email("updated@example.com")
                        .firstName("Updated")
                        .build())
                .build();

        User updatedUser = User.builder()
                .id(1L)
                .username("admin1")
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("User")
                .userType(User.UserType.ADMIN)
                .build();

        when(adminRepository.findById(1L)).thenReturn(Optional.of(testAdmin));
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);
        when(adminRepository.save(any(Admin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Admin result = adminService.updateAdmin(1L, updatedAdmin);

        // Then
        assertThat(result)
                .isNotNull()
                .extracting(Admin::getUser)
                .satisfies(user -> {
                    assertThat(user.getEmail()).isEqualTo("updated@example.com");
                    assertThat(user.getFirstName()).isEqualTo("Updated");
                });

        verify(adminRepository).findById(1L);
        verify(userService).updateUser(eq(1L), any(User.class));
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    void updateAdmin_ShouldDoNothing_WhenNoUserUpdate() {
        // Given
        Admin updatedAdmin = Admin.builder().build();

        when(adminRepository.findById(1L)).thenReturn(Optional.of(testAdmin));
        when(adminRepository.save(any(Admin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Admin result = adminService.updateAdmin(1L, updatedAdmin);

        // Then
        assertThat(result)
                .isNotNull()
                .isEqualTo(testAdmin);

        verify(adminRepository).findById(1L);
        verify(userService, never()).updateUser(anyLong(), any());
        verify(adminRepository).save(testAdmin);
    }

    @Test
    void deleteAdmin_ShouldDeleteAdmin_WhenExists() {
        // Given
        when(adminRepository.findById(1L)).thenReturn(Optional.of(testAdmin));
        doNothing().when(adminRepository).delete(testAdmin);

        // When
        adminService.deleteAdmin(1L);

        // Then
        verify(adminRepository).findById(1L);
        verify(adminRepository).delete(testAdmin);
    }

    @Test
    void deleteAdmin_ShouldThrowException_WhenNotFound() {
        // Given
        when(adminRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> adminService.deleteAdmin(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Admin not found");

        verify(adminRepository).findById(99L);
        verify(adminRepository, never()).delete(any());
    }

    @Test
    void assignStationToAdmin_ShouldAssignStation_WhenValidData() {
        // Given
        Station newStation = Station.builder()
                .id(2L)
                .name("New Station")
                .admin(null)
                .build();

        when(adminRepository.findById(1L)).thenReturn(Optional.of(testAdmin));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(newStation));
        when(stationRepository.save(any(Station.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(adminRepository.save(any(Admin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Admin result = adminService.assignStationToAdmin(1L, 2L);

        // Then
        assertThat(result.getStations())
                .hasSize(2)
                .extracting(Station::getName)
                .contains("Test Station", "New Station");

        verify(adminRepository).findById(1L);
        verify(stationRepository).findById(2L);
        verify(stationRepository).save(newStation);
        verify(adminRepository).save(testAdmin);
    }

    @Test
    void removeStationFromAdmin_ShouldRemoveStation_WhenExists() {
        // Given
        testStation.setAdmin(testAdmin);
        when(adminRepository.findById(1L)).thenReturn(Optional.of(testAdmin));
        when(stationRepository.findById(1L)).thenReturn(Optional.of(testStation));
        when(stationRepository.save(any(Station.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(adminRepository.save(any(Admin.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Admin result = adminService.removeStationFromAdmin(1L, 1L);

        // Then
        assertThat(result.getStations())
                .isEmpty();

        verify(adminRepository).findById(1L);
        verify(stationRepository).findById(1L);
        verify(stationRepository).save(testStation);
        verify(adminRepository).save(testAdmin);
    }
}