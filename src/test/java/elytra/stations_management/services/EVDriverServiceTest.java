package elytra.stations_management.services;

import elytra.stations_management.models.EVDriver;
import elytra.stations_management.models.User;
import elytra.stations_management.repositories.EVDriverRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EVDriverServiceTest {

    @Mock
    private EVDriverRepository evDriverRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private EVDriverService evDriverService;

    private EVDriver testDriver;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("evdriver1")
                .password("hashedPassword")
                .email("driver@example.com")
                .firstName("John")
                .lastName("Doe")
                .userType(User.UserType.EV_DRIVER)
                .build();

        testDriver = EVDriver.builder()
                .id(1L)
                .user(testUser)
                .build();
    }

    @Test
    void registerDriver_ShouldSaveDriverWithUser() {
        // Arrange
        User newUser = User.builder()
                .username("newdriver")
                .password("plainPassword")
                .email("newdriver@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .build();

        User savedUser = User.builder()
                .id(2L)
                .username("newdriver")
                .password("hashedPassword")
                .email("newdriver@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .userType(User.UserType.EV_DRIVER)
                .build();

        EVDriver newDriver = EVDriver.builder()
                .build();

        EVDriver expectedDriver = EVDriver.builder()
                .id(2L)
                .user(savedUser)
                .build();

        when(userService.registerUser(any(User.class))).thenReturn(savedUser);
        when(evDriverRepository.save(any(EVDriver.class))).thenReturn(expectedDriver);

        // Act
        EVDriver result = evDriverService.registerDriver(newDriver, newUser);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getUser()).isEqualTo(savedUser);
        assertThat(newUser.getUserType()).isEqualTo(User.UserType.EV_DRIVER);

        verify(userService).registerUser(newUser);
        verify(evDriverRepository).save(any(EVDriver.class));
    }

    @Test
    void getDriverById_ShouldReturnDriver_WhenDriverExists() {
        // Arrange
        when(evDriverRepository.findById(1L)).thenReturn(Optional.of(testDriver));

        // Act
        EVDriver result = evDriverService.getDriverById(1L);

        // Assert
        assertThat(result).isEqualTo(testDriver);
        verify(evDriverRepository).findById(1L);
    }

    @Test
    void getDriverById_ShouldThrowException_WhenDriverNotFound() {
        // Arrange
        when(evDriverRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> evDriverService.getDriverById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Driver not found");

        verify(evDriverRepository).findById(999L);
    }

    @Test
    void getDriverByUserId_ShouldReturnDriver_WhenDriverExists() {
        // Arrange
        when(evDriverRepository.findByUserId(1L)).thenReturn(Optional.of(testDriver));

        // Act
        EVDriver result = evDriverService.getDriverByUserId(1L);

        // Assert
        assertThat(result).isEqualTo(testDriver);
        verify(evDriverRepository).findByUserId(1L);
    }

    @Test
    void getDriverByUserId_ShouldThrowException_WhenDriverNotFound() {
        // Arrange
        when(evDriverRepository.findByUserId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> evDriverService.getDriverByUserId(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Driver not found");

        verify(evDriverRepository).findByUserId(999L);
    }

    @Test
    void getAllDrivers_ShouldReturnAllDrivers() {
        // Arrange
        User user2 = User.builder()
                .id(2L)
                .username("evdriver2")
                .email("driver2@example.com")
                .userType(User.UserType.EV_DRIVER)
                .build();

        EVDriver driver2 = EVDriver.builder()
                .id(2L)
                .user(user2)
                .build();

        List<EVDriver> drivers = Arrays.asList(testDriver, driver2);
        when(evDriverRepository.findAll()).thenReturn(drivers);

        // Act
        List<EVDriver> result = evDriverService.getAllDrivers();

        // Assert
        assertThat(result).hasSize(2).containsExactly(testDriver, driver2);
        verify(evDriverRepository).findAll();
    }

    @Test
    void getAllDrivers_ShouldReturnEmptyList_WhenNoDrivers() {
        // Arrange
        when(evDriverRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<EVDriver> result = evDriverService.getAllDrivers();

        // Assert
        assertThat(result).isEmpty();
        verify(evDriverRepository).findAll();
    }

    @Test
    void updateDriver_ShouldUpdateDriverAndUser() {
        // Arrange
        User updatedUserData = User.builder()
                .username("updateddriver")
                .email("updated@example.com")
                .firstName("John Updated")
                .lastName("Doe Updated")
                .build();

        User updatedUser = User.builder()
                .id(1L)
                .username("updateddriver")
                .email("updated@example.com")
                .firstName("John Updated")
                .lastName("Doe Updated")
                .userType(User.UserType.EV_DRIVER)
                .build();

        EVDriver updatedDriverData = EVDriver.builder()
                .user(updatedUserData)
                .build();

        EVDriver expectedDriver = EVDriver.builder()
                .id(1L)
                .user(updatedUser)
                .build();

        when(evDriverRepository.findById(1L)).thenReturn(Optional.of(testDriver));
        when(userService.updateUser(1L, updatedUserData)).thenReturn(updatedUser);
        when(evDriverRepository.save(any(EVDriver.class))).thenReturn(expectedDriver);

        // Act
        EVDriver result = evDriverService.updateDriver(1L, updatedDriverData);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUser()).isEqualTo(updatedUser);

        verify(evDriverRepository).findById(1L);
        verify(userService).updateUser(1L, updatedUserData);
        verify(evDriverRepository).save(any(EVDriver.class));
    }

    @Test
    void updateDriver_ShouldThrowException_WhenDriverNotFound() {
        // Arrange
        EVDriver updatedDriverData = EVDriver.builder()
                .user(testUser)
                .build();

        when(evDriverRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> evDriverService.updateDriver(999L, updatedDriverData))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Driver not found");

        verify(evDriverRepository).findById(999L);
        verify(userService, never()).updateUser(anyLong(), any());
        verify(evDriverRepository, never()).save(any());
    }

    @Test
    void deleteDriver_ShouldDeleteDriver_WhenDriverExists() {
        // Arrange
        when(evDriverRepository.findById(1L)).thenReturn(Optional.of(testDriver));

        // Act
        evDriverService.deleteDriver(1L);

        // Assert
        verify(evDriverRepository).findById(1L);
        verify(evDriverRepository).delete(testDriver);
    }

    @Test
    void deleteDriver_ShouldThrowException_WhenDriverNotFound() {
        // Arrange
        when(evDriverRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> evDriverService.deleteDriver(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Driver not found");

        verify(evDriverRepository).findById(999L);
        verify(evDriverRepository, never()).delete(any());
    }
}
