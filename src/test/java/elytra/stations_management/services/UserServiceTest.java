package elytra.stations_management.services;

import elytra.stations_management.models.User;
import elytra.stations_management.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
        
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .userType(User.UserType.EV_DRIVER)
                .build();
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userService.loadUserByUsername("testuser");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("testuser");
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found: nonexistent");

        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void registerUser_ShouldEncodePasswordAndSaveUser() {
        User newUser = User.builder()
                .username("newuser")
                .password("plainPassword")
                .email("new@example.com")
                .firstName("New")
                .lastName("User")
                .userType(User.UserType.EV_DRIVER)
                .build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User savedUser = userService.registerUser(newUser);

        assertThat(savedUser).isEqualTo(testUser);
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("new@example.com");
        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameExists() {
        User newUser = User.builder()
                .username("existinguser")
                .password("plainPassword")
                .email("new@example.com")
                .firstName("New")
                .lastName("User")
                .userType(User.UserType.EV_DRIVER)
                .build();

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(newUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Username already exists: existinguser");

        verify(userRepository).existsByUsername("existinguser");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_ShouldThrowException_WhenEmailExists() {
        User newUser = User.builder()
                .username("newuser")
                .password("plainPassword")
                .email("existing@example.com")
                .firstName("New")
                .lastName("User")
                .userType(User.UserType.EV_DRIVER)
                .build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(newUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already exists: existing@example.com");

        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_ShouldUpdateExistingUser_WhenUserExists() {
        User updateData = User.builder()
                .email("updated@example.com")
                .userType(User.UserType.ADMIN)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User updatedUser = userService.updateUser(1L, updateData);

        assertThat(updatedUser).isNotNull();
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUser_ShouldUpdatePassword_WhenPasswordProvided() {
        User updateData = User.builder()
                .password("newPassword")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User updatedUser = userService.updateUser(1L, updateData);

        assertThat(updatedUser).isNotNull();
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUser_ShouldNotUpdatePassword_WhenPasswordEmpty() {
        User updateData = User.builder()
                .password("")
                .email("updated@example.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User updatedUser = userService.updateUser(1L, updateData);

        assertThat(updatedUser).isNotNull();
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(999L, testUser))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found with id: 999");

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        List<User> users = Arrays.asList(testUser,
                User.builder()
                    .id(2L)
                    .username("user2")
                    .email("user2@example.com")
                    .firstName("User")
                    .lastName("Two")
                    .userType(User.UserType.STATION_OPERATOR)
                    .build());

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(users);
        verify(userRepository).findAll();
    }

    @Test
    void getAllUsers_ShouldReturnEmptyList_WhenNoUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        List<User> result = userService.getAllUsers();

        assertThat(result).isEmpty();
        verify(userRepository).findAll();
    }
}