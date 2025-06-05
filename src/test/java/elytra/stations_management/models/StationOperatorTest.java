package elytra.stations_management.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StationOperatorTest {

    private StationOperator stationOperator;
    private User user;
    private Station station;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("operator1")
                .password("hashedPassword")
                .email("operator@example.com")
                .firstName("John")
                .lastName("Operator")
                .userType(User.UserType.STATION_OPERATOR)
                .build();

        station = Station.builder()
                .id(1L)
                .name("Test Station")
                .address("123 Main St")
                .latitude(40.7128)
                .longitude(-74.0060)
                .build();

        stationOperator = StationOperator.builder()
                .id(1L)
                .user(user)
                .station(station)
                .build();
    }

    @Test
    void testStationOperatorCreation() {
        assertThat(stationOperator).isNotNull();
        assertThat(stationOperator.getId()).isEqualTo(1L);
        assertThat(stationOperator.getUser()).isEqualTo(user);
        assertThat(stationOperator.getStation()).isEqualTo(station);
    }

    @Test
    void testNoArgsConstructor() {
        StationOperator emptyOperator = new StationOperator();
        assertThat(emptyOperator).isNotNull();
        assertThat(emptyOperator.getId()).isNull();
        assertThat(emptyOperator.getUser()).isNull();
        assertThat(emptyOperator.getStation()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        StationOperator fullOperator = new StationOperator(2L, user, station);
        
        assertThat(fullOperator.getId()).isEqualTo(2L);
        assertThat(fullOperator.getUser()).isEqualTo(user);
        assertThat(fullOperator.getStation()).isEqualTo(station);
    }

    @Test
    void testBuilder() {
        StationOperator builtOperator = StationOperator.builder()
                .id(3L)
                .user(user)
                .station(station)
                .build();

        assertThat(builtOperator.getId()).isEqualTo(3L);
        assertThat(builtOperator.getUser()).isEqualTo(user);
        assertThat(builtOperator.getStation()).isEqualTo(station);
    }

    @Test
    void testSettersAndGetters() {
        StationOperator testOperator = new StationOperator();
        
        testOperator.setId(4L);
        testOperator.setUser(user);
        testOperator.setStation(station);

        assertThat(testOperator.getId()).isEqualTo(4L);
        assertThat(testOperator.getUser()).isEqualTo(user);
        assertThat(testOperator.getStation()).isEqualTo(station);
    }

    @Test
    void testToString() {
        String operatorString = stationOperator.toString();
        assertThat(operatorString).contains("StationOperator", "id=1", "user=", "station=");
    }

    @Test
    void testEquals() {
        StationOperator operator1 = StationOperator.builder()
                .id(1L)
                .user(user)
                .station(station)
                .build();

        StationOperator operator2 = StationOperator.builder()
                .id(1L)
                .user(user)
                .station(station)
                .build();

        StationOperator operator3 = StationOperator.builder()
                .id(2L)
                .user(user)
                .station(station)
                .build();

        assertThat(operator1)
                .isEqualTo(operator1)
                .isEqualTo(operator2)
                .isNotEqualTo(operator3)
                .isNotEqualTo(null)
                .isNotEqualTo("not an operator");
    }

    @Test
    void testHashCode() {
        StationOperator operator1 = StationOperator.builder()
                .id(1L)
                .user(user)
                .station(station)
                .build();

        StationOperator operator2 = StationOperator.builder()
                .id(1L)
                .user(user)
                .station(station)
                .build();

        assertThat(operator1).hasSameHashCodeAs(operator2);
    }

    @Test
    void testBuilderWithPartialData() {
        StationOperator partialOperator = StationOperator.builder()
                .user(user)
                .build();

        assertThat(partialOperator.getId()).isNull();
        assertThat(partialOperator.getUser()).isEqualTo(user);
        assertThat(partialOperator.getStation()).isNull();
    }

    @Test
    void testSetAndGetNullValues() {
        StationOperator testOperator = new StationOperator();
        
        testOperator.setUser(null);
        testOperator.setStation(null);

        assertThat(testOperator.getUser()).isNull();
        assertThat(testOperator.getStation()).isNull();
    }

    @Test
    void testDifferentUsers() {
        User user2 = User.builder()
                .id(2L)
                .username("operator2")
                .email("operator2@example.com")
                .firstName("Jane")
                .lastName("Operator")
                .userType(User.UserType.STATION_OPERATOR)
                .build();

        StationOperator operator1 = StationOperator.builder()
                .id(1L)
                .user(user)
                .station(station)
                .build();

        StationOperator operator2 = StationOperator.builder()
                .id(1L)
                .user(user2)
                .station(station)
                .build();

        assertThat(operator1).isNotEqualTo(operator2);
    }

    @Test
    void testDifferentStations() {
        Station station2 = Station.builder()
                .id(2L)
                .name("Another Station")
                .address("456 Another St")
                .build();

        StationOperator operator1 = StationOperator.builder()
                .id(1L)
                .user(user)
                .station(station)
                .build();

        StationOperator operator2 = StationOperator.builder()
                .id(1L)
                .user(user)
                .station(station2)
                .build();

        assertThat(operator1).isNotEqualTo(operator2);
    }

    @Test
    void testCanEqual() {
        StationOperator operator1 = StationOperator.builder()
                .id(1L)
                .user(user)
                .station(station)
                .build();

        StationOperator operator2 = StationOperator.builder()
                .id(2L)
                .user(user)
                .station(station)
                .build();

        assertThat(operator1.canEqual(operator2)).isTrue();
        assertThat(operator1.canEqual("not an operator")).isFalse();
        assertThat(operator1.canEqual(null)).isFalse();
    }
}