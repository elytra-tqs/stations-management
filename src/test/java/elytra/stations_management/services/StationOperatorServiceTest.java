package elytra.stations_management.services;

import elytra.stations_management.models.Station;
import elytra.stations_management.models.StationOperator;
import elytra.stations_management.models.User;
import elytra.stations_management.repositories.StationOperatorRepository;
import elytra.stations_management.repositories.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StationOperatorServiceTest {

    @Mock
    private StationOperatorRepository stationOperatorRepository;

    @Mock
    private StationRepository stationRepository;

    @Mock
    private UserService userService;

    @InjectMocks
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
    void registerStationOperator_ShouldCreateOperatorWithStation_WhenStationIdProvided() {
        // Given
        StationOperator newOperator = StationOperator.builder().build();
        User newUser = User.builder()
                .username("newoperator")
                .password("password")
                .email("newoperator@example.com")
                .build();
        Long stationId = 1L;

        User savedUser = User.builder()
                .id(2L)
                .username("newoperator")
                .email("newoperator@example.com")
                .userType(User.UserType.STATION_OPERATOR)
                .build();

        when(stationOperatorRepository.existsByStationId(stationId)).thenReturn(false);
        when(userService.registerUser(any(User.class))).thenReturn(savedUser);
        when(stationRepository.findById(stationId)).thenReturn(Optional.of(testStation));
        when(stationOperatorRepository.save(any(StationOperator.class))).thenReturn(testOperator);

        // When
        StationOperator result = stationOperatorService.registerStationOperator(newOperator, newUser, stationId);

        // Then
        assertThat(result)
                .isNotNull()
                .isEqualTo(testOperator)
                .satisfies(op -> {
                    assertThat(op.getId()).isEqualTo(1L);
                    assertThat(op.getUser()).isEqualTo(testUser);
                    assertThat(op.getStation()).isEqualTo(testStation);
                });

        verify(userService).registerUser(argThat(user -> 
            user.getUserType() == User.UserType.STATION_OPERATOR
        ));
        verify(stationRepository).findById(stationId);
        verify(stationOperatorRepository).save(any(StationOperator.class));
    }

    @Test
    void registerStationOperator_ShouldCreateOperatorWithoutStation_WhenStationIdNull() {
        // Given
        StationOperator newOperator = StationOperator.builder().build();
        User newUser = User.builder()
                .username("newoperator")
                .password("password")
                .email("newoperator@example.com")
                .build();

        User savedUser = User.builder()
                .id(2L)
                .username("newoperator")
                .email("newoperator@example.com")
                .userType(User.UserType.STATION_OPERATOR)
                .build();

        StationOperator savedOperator = StationOperator.builder()
                .id(2L)
                .user(savedUser)
                .station(null)
                .build();

        when(userService.registerUser(any(User.class))).thenReturn(savedUser);
        when(stationOperatorRepository.save(any(StationOperator.class))).thenReturn(savedOperator);

        // When
        StationOperator result = stationOperatorService.registerStationOperator(newOperator, newUser, null);

        // Then
        assertThat(result)
                .isNotNull()
                .satisfies(op -> {
                    assertThat(op.getId()).isEqualTo(2L);
                    assertThat(op.getUser()).isEqualTo(savedUser);
                    assertThat(op.getStation()).isNull();
                });

        verify(stationRepository, never()).findById(anyLong());
        verify(stationOperatorRepository).save(any(StationOperator.class));
    }

    @Test
    void registerStationOperator_ShouldThrowException_WhenStationAlreadyHasOperator() {
        // Given
        StationOperator newOperator = StationOperator.builder().build();
        User newUser = User.builder().username("newoperator").build();
        Long stationId = 1L;

        User savedUser = User.builder()
                .id(2L)
                .username("newoperator")
                .userType(User.UserType.STATION_OPERATOR)
                .build();

        when(userService.registerUser(any(User.class))).thenReturn(savedUser);
        when(stationOperatorRepository.existsByStationId(stationId)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> 
            stationOperatorService.registerStationOperator(newOperator, newUser, stationId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Station already has an operator assigned");

        verify(userService).registerUser(any());
        verify(stationOperatorRepository, never()).save(any());
    }

    @Test
    void getStationOperatorById_ShouldReturnOperator_WhenExists() {
        // Given
        when(stationOperatorRepository.findById(1L)).thenReturn(Optional.of(testOperator));

        // When
        StationOperator result = stationOperatorService.getStationOperatorById(1L);

        // Then
        assertThat(result)
                .isNotNull()
                .isEqualTo(testOperator)
                .extracting(StationOperator::getId, StationOperator::getUser, StationOperator::getStation)
                .containsExactly(1L, testUser, testStation);

        verify(stationOperatorRepository).findById(1L);
    }

    @Test
    void getStationOperatorByUserId_ShouldReturnOperator_WhenExists() {
        // Given
        when(stationOperatorRepository.findByUserId(1L)).thenReturn(Optional.of(testOperator));

        // When
        StationOperator result = stationOperatorService.getStationOperatorByUserId(1L);

        // Then
        assertThat(result)
                .isNotNull()
                .isEqualTo(testOperator)
                .extracting(StationOperator::getUser)
                .extracting(User::getId)
                .isEqualTo(1L);

        verify(stationOperatorRepository).findByUserId(1L);
    }

    @Test
    void getStationOperatorByStationId_ShouldReturnOperator_WhenExists() {
        // Given
        when(stationOperatorRepository.findByStationId(1L)).thenReturn(Optional.of(testOperator));

        // When
        StationOperator result = stationOperatorService.getStationOperatorByStationId(1L);

        // Then
        assertThat(result)
                .isNotNull()
                .isEqualTo(testOperator)
                .extracting(StationOperator::getStation)
                .extracting(Station::getId)
                .isEqualTo(1L);

        verify(stationOperatorRepository).findByStationId(1L);
    }

    @Test
    void claimStation_ShouldAssignStation_WhenOperatorHasNoStation() {
        // Given
        StationOperator operatorWithoutStation = StationOperator.builder()
                .id(2L)
                .user(testUser)
                .station(null)
                .build();

        when(stationOperatorRepository.findById(2L)).thenReturn(Optional.of(operatorWithoutStation));
        when(stationOperatorRepository.existsByStationId(1L)).thenReturn(false);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(testStation));
        when(stationOperatorRepository.save(any(StationOperator.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        StationOperator result = stationOperatorService.claimStation(2L, 1L);

        // Then
        assertThat(result)
                .isNotNull()
                .satisfies(op -> {
                    assertThat(op.getId()).isEqualTo(2L);
                    assertThat(op.getStation()).isEqualTo(testStation);
                });

        verify(stationOperatorRepository).save(argThat(op -> op.getStation() != null));
    }

    @Test
    void claimStation_ShouldThrowException_WhenOperatorAlreadyHasStation() {
        // Given
        when(stationOperatorRepository.findById(1L)).thenReturn(Optional.of(testOperator));

        // When & Then
        assertThatThrownBy(() -> stationOperatorService.claimStation(1L, 2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Operator already manages a station");

        verify(stationOperatorRepository, never()).save(any());
    }

    @Test
    void releaseStation_ShouldRemoveStation_WhenOperatorHasStation() {
        // Given
        when(stationOperatorRepository.findById(1L)).thenReturn(Optional.of(testOperator));
        when(stationOperatorRepository.save(any(StationOperator.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        StationOperator result = stationOperatorService.releaseStation(1L);

        // Then
        assertThat(result)
                .isNotNull()
                .satisfies(op -> {
                    assertThat(op.getId()).isEqualTo(1L);
                    assertThat(op.getStation()).isNull();
                });

        verify(stationOperatorRepository).save(argThat(op -> op.getStation() == null));
    }

    @Test
    void releaseStation_ShouldThrowException_WhenOperatorHasNoStation() {
        // Given
        StationOperator operatorWithoutStation = StationOperator.builder()
                .id(2L)
                .user(testUser)
                .station(null)
                .build();

        when(stationOperatorRepository.findById(2L)).thenReturn(Optional.of(operatorWithoutStation));

        // When & Then
        assertThatThrownBy(() -> stationOperatorService.releaseStation(2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Operator doesn't manage any station");

        verify(stationOperatorRepository, never()).save(any());
    }

    @Test
    void getAvailableStations_ShouldReturnStationsWithoutOperators() {
        // Given
        Station station2 = Station.builder().id(2L).name("Station 2").build();
        Station station3 = Station.builder().id(3L).name("Station 3").build();
        
        StationOperator operator2 = StationOperator.builder()
                .id(2L)
                .station(station2)
                .build();

        when(stationOperatorRepository.findAll()).thenReturn(Arrays.asList(testOperator, operator2));
        when(stationRepository.findAll()).thenReturn(Arrays.asList(testStation, station2, station3));

        // When
        List<Station> result = stationOperatorService.getAvailableStations();

        // Then
        assertThat(result)
                .hasSize(1)
                .containsExactly(station3)
                .extracting(Station::getName)
                .containsExactly("Station 3");

        verify(stationOperatorRepository).findAll();
        verify(stationRepository).findAll();
    }

    @Test
    void getAvailableStations_ShouldReturnAllStations_WhenNoOperators() {
        // Given
        Station station2 = Station.builder().id(2L).name("Station 2").build();
        
        when(stationOperatorRepository.findAll()).thenReturn(Collections.emptyList());
        when(stationRepository.findAll()).thenReturn(Arrays.asList(testStation, station2));

        // When
        List<Station> result = stationOperatorService.getAvailableStations();

        // Then
        assertThat(result)
                .hasSize(2)
                .containsExactly(testStation, station2)
                .extracting(Station::getId)
                .containsExactly(1L, 2L);

        verify(stationOperatorRepository).findAll();
        verify(stationRepository).findAll();
    }

    @Test
    void updateStationOperator_ShouldUpdateBothUserAndStation() {
        // Given
        Station newStation = Station.builder().id(2L).name("New Station").build();
        StationOperator updatedOperator = StationOperator.builder()
                .user(User.builder().email("updated@example.com").build())
                .station(newStation)
                .build();

        User updatedUser = User.builder()
                .id(1L)
                .username("operator1")
                .email("updated@example.com")
                .userType(User.UserType.STATION_OPERATOR)
                .build();

        when(stationOperatorRepository.findById(1L)).thenReturn(Optional.of(testOperator));
        when(stationOperatorRepository.existsByStationId(2L)).thenReturn(false);
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);
        when(stationRepository.findById(2L)).thenReturn(Optional.of(newStation));
        when(stationOperatorRepository.save(any(StationOperator.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        StationOperator result = stationOperatorService.updateStationOperator(1L, updatedOperator);

        // Then
        assertThat(result)
                .isNotNull()
                .satisfies(op -> {
                    assertThat(op.getUser().getEmail()).isEqualTo("updated@example.com");
                    assertThat(op.getStation()).isEqualTo(newStation);
                });

        verify(userService).updateUser(eq(1L), any(User.class));
        verify(stationRepository).findById(2L);
        verify(stationOperatorRepository).save(any(StationOperator.class));
    }

    @Test
    void deleteStationOperator_ShouldDeleteOperator_WhenExists() {
        // Given
        when(stationOperatorRepository.findById(1L)).thenReturn(Optional.of(testOperator));
        doNothing().when(stationOperatorRepository).delete(testOperator);

        // When
        stationOperatorService.deleteStationOperator(1L);

        // Then
        verify(stationOperatorRepository).findById(1L);
        verify(stationOperatorRepository).delete(testOperator);
    }
}