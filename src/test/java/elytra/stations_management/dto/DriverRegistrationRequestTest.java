package elytra.stations_management.dto;

import elytra.stations_management.models.EVDriver;
import elytra.stations_management.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

class DriverRegistrationRequestTest {

    private User testUser;
    private EVDriver testDriver;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testdriver")
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
    void testDriverRegistrationRequestCreation() {
        DriverRegistrationRequest request = new DriverRegistrationRequest();
        request.setDriver(testDriver);
        request.setUser(testUser);

        assertThat(request.getDriver()).isEqualTo(testDriver);
        assertThat(request.getUser()).isEqualTo(testUser);
    }

    @Test
    void testDefaultConstructor() {
        DriverRegistrationRequest request = new DriverRegistrationRequest();

        assertThat(request.getDriver()).isNull();
        assertThat(request.getUser()).isNull();
    }

    @Test
    void testSettersAndGetters() {
        DriverRegistrationRequest request = new DriverRegistrationRequest();
        
        request.setDriver(testDriver);
        assertThat(request.getDriver()).isEqualTo(testDriver);
        
        request.setUser(testUser);
        assertThat(request.getUser()).isEqualTo(testUser);
    }

    @Test
    void testEquals() {
        DriverRegistrationRequest request1 = new DriverRegistrationRequest();
        request1.setDriver(testDriver);
        request1.setUser(testUser);

        DriverRegistrationRequest request2 = new DriverRegistrationRequest();
        request2.setDriver(testDriver);
        request2.setUser(testUser);

        DriverRegistrationRequest request3 = new DriverRegistrationRequest();
        request3.setDriver(null);
        request3.setUser(testUser);

        // Test reflexivity
        assertThat(request1)
                .isEqualTo(request1)
                .isEqualTo(request2)
                .isNotEqualTo(request3)
                .isNotEqualTo(null)
                .isNotEqualTo("not a request");
    }

    @Test
    void testEqualsWithNullFields() {
        DriverRegistrationRequest request1 = new DriverRegistrationRequest();
        request1.setDriver(null);
        request1.setUser(null);

        DriverRegistrationRequest request2 = new DriverRegistrationRequest();
        request2.setDriver(null);
        request2.setUser(null);

        DriverRegistrationRequest request3 = new DriverRegistrationRequest();
        request3.setDriver(testDriver);
        request3.setUser(null);

        DriverRegistrationRequest request4 = new DriverRegistrationRequest();
        request4.setDriver(null);
        request4.setUser(testUser);

        assertThat(request1)
                .isEqualTo(request2)
                .isNotEqualTo(request3)
                .isNotEqualTo(request4);

        assertThat(request3).isNotEqualTo(request4);
    }

    @Test
    void testHashCode() {
        DriverRegistrationRequest request1 = new DriverRegistrationRequest();
        request1.setDriver(testDriver);
        request1.setUser(testUser);

        DriverRegistrationRequest request2 = new DriverRegistrationRequest();
        request2.setDriver(testDriver);
        request2.setUser(testUser);

        DriverRegistrationRequest request3 = new DriverRegistrationRequest();
        request3.setDriver(null);
        request3.setUser(testUser);

        // Equal objects must have equal hash codes
        assertThat(request1).hasSameHashCodeAs(request2);

        // Different objects should (likely) have different hash codes
        assertThat(request1.hashCode()).isNotEqualTo(request3.hashCode());
    }

    @Test
    void testHashCodeWithNullFields() {
        DriverRegistrationRequest request1 = new DriverRegistrationRequest();
        request1.setDriver(null);
        request1.setUser(null);

        DriverRegistrationRequest request2 = new DriverRegistrationRequest();
        request2.setDriver(null);
        request2.setUser(null);

        DriverRegistrationRequest request3 = new DriverRegistrationRequest();
        request3.setDriver(testDriver);
        request3.setUser(null);

        // Equal objects with null fields must have equal hash codes
        assertThat(request1).hasSameHashCodeAs(request2);
        
        // Test that null fields don't cause NullPointerException
        assertThatCode(request1::hashCode).doesNotThrowAnyException();
        assertThatCode(request3::hashCode).doesNotThrowAnyException();
    }

    @Test
    void testCanEqual() {
        DriverRegistrationRequest request1 = new DriverRegistrationRequest();
        request1.setDriver(testDriver);
        request1.setUser(testUser);

        DriverRegistrationRequest request2 = new DriverRegistrationRequest();
        request2.setDriver(null);
        request2.setUser(null);

        // Test canEqual with same type
        assertThat(request1.canEqual(request2)).isTrue();
        assertThat(request2.canEqual(request1)).isTrue();

        // Test canEqual with different type
        assertThat(request1.canEqual("not a request")).isFalse();
        assertThat(request1.canEqual(null)).isFalse();
        assertThat(request1.canEqual(new Object())).isFalse();
    }

    @Test
    void testToString() {
        DriverRegistrationRequest request = new DriverRegistrationRequest();
        request.setDriver(testDriver);
        request.setUser(testUser);

        String toString = request.toString();
        assertThat(toString)
                .contains("DriverRegistrationRequest")
                .contains("driver=")
                .contains("user=");
    }

    @Test
    void testNullValues() {
        DriverRegistrationRequest request = new DriverRegistrationRequest();
        
        request.setDriver(null);
        request.setUser(null);

        assertThat(request.getDriver()).isNull();
        assertThat(request.getUser()).isNull();
    }

    @Test
    void testPartialData() {
        DriverRegistrationRequest request = new DriverRegistrationRequest();
        
        request.setDriver(testDriver);
        
        assertThat(request.getDriver()).isEqualTo(testDriver);
        assertThat(request.getUser()).isNull();
    }

    @Test
    void testDifferentDrivers() {
        EVDriver differentDriver = EVDriver.builder()
                .id(2L)
                .user(testUser)
                .build();

        DriverRegistrationRequest request1 = new DriverRegistrationRequest();
        request1.setDriver(testDriver);
        request1.setUser(testUser);

        DriverRegistrationRequest request2 = new DriverRegistrationRequest();
        request2.setDriver(differentDriver);
        request2.setUser(testUser);

        assertThat(request1).isNotEqualTo(request2);
    }

    @Test
    void testDifferentUsers() {
        User differentUser = User.builder()
                .id(2L)
                .username("differentdriver")
                .email("different@example.com")
                .userType(User.UserType.EV_DRIVER)
                .build();

        DriverRegistrationRequest request1 = new DriverRegistrationRequest();
        request1.setDriver(testDriver);
        request1.setUser(testUser);

        DriverRegistrationRequest request2 = new DriverRegistrationRequest();
        request2.setDriver(testDriver);
        request2.setUser(differentUser);

        assertThat(request1).isNotEqualTo(request2);
    }
}