package elytra.stations_management.dto;

import elytra.stations_management.models.StationOperator;
import elytra.stations_management.models.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OperatorRegistrationRequestTest {

    @Test
    void testGettersAndSetters() {
        // Given
        OperatorRegistrationRequest request = new OperatorRegistrationRequest();
        StationOperator operator = StationOperator.builder()
                .id(1L)
                .build();
        User user = User.builder()
                .id(1L)
                .username("operator1")
                .email("operator@example.com")
                .build();
        Long stationId = 5L;

        // When
        request.setOperator(operator);
        request.setUser(user);
        request.setStationId(stationId);

        // Then
        assertThat(request.getOperator())
                .isNotNull()
                .isEqualTo(operator)
                .extracting(StationOperator::getId)
                .isEqualTo(1L);

        assertThat(request.getUser())
                .isNotNull()
                .isEqualTo(user)
                .extracting(User::getUsername, User::getEmail)
                .containsExactly("operator1", "operator@example.com");

        assertThat(request.getStationId())
                .isNotNull()
                .isEqualTo(5L);
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        StationOperator operator = StationOperator.builder().id(1L).build();
        User user = User.builder().id(1L).username("operator1").build();
        Long stationId = 5L;

        OperatorRegistrationRequest request1 = new OperatorRegistrationRequest();
        request1.setOperator(operator);
        request1.setUser(user);
        request1.setStationId(stationId);

        OperatorRegistrationRequest request2 = new OperatorRegistrationRequest();
        request2.setOperator(operator);
        request2.setUser(user);
        request2.setStationId(stationId);

        OperatorRegistrationRequest request3 = new OperatorRegistrationRequest();
        request3.setOperator(operator);
        request3.setUser(user);
        request3.setStationId(6L);

        // Then
        assertThat(request1)
                .isEqualTo(request2)
                .hasSameHashCodeAs(request2)
                .isNotEqualTo(request3)
                .isNotEqualTo(null)
                .isNotEqualTo(new Object());

        assertThat(request1.hashCode()).isNotEqualTo(request3.hashCode());
    }

    @Test
    void testToString() {
        // Given
        OperatorRegistrationRequest request = new OperatorRegistrationRequest();
        StationOperator operator = StationOperator.builder().id(1L).build();
        User user = User.builder().username("operator1").build();
        request.setOperator(operator);
        request.setUser(user);
        request.setStationId(5L);

        // When
        String toString = request.toString();

        // Then
        assertThat(toString)
                .isNotNull()
                .contains("OperatorRegistrationRequest")
                .contains("operator=")
                .contains("user=")
                .contains("stationId=5");
    }

    @Test
    void testCanEqual() {
        // Given
        OperatorRegistrationRequest request1 = new OperatorRegistrationRequest();
        OperatorRegistrationRequest request2 = new OperatorRegistrationRequest();
        
        // Then
        assertThat(request1.canEqual(request2)).isTrue();
        assertThat(request1.canEqual(new Object())).isFalse();
    }

    @Test
    void testEmptyRequest() {
        // Given
        OperatorRegistrationRequest request = new OperatorRegistrationRequest();

        // Then
        assertThat(request.getOperator()).isNull();
        assertThat(request.getUser()).isNull();
        assertThat(request.getStationId()).isNull();
    }

    @Test
    void testWithNullStationId() {
        // Given
        OperatorRegistrationRequest request = new OperatorRegistrationRequest();
        StationOperator operator = StationOperator.builder().id(1L).build();
        User user = User.builder().username("operator1").build();
        
        request.setOperator(operator);
        request.setUser(user);
        request.setStationId(null);

        // Then
        assertThat(request)
                .hasFieldOrPropertyWithValue("operator", operator)
                .hasFieldOrPropertyWithValue("user", user)
                .hasFieldOrPropertyWithValue("stationId", null);
    }

    @Test
    void testWithAllNullFields() {
        // Given
        OperatorRegistrationRequest request = new OperatorRegistrationRequest();
        request.setOperator(null);
        request.setUser(null);
        request.setStationId(null);

        // Then
        assertThat(request)
                .hasAllNullFieldsOrProperties();
    }
}