package elytra.stations_management.dto;

import elytra.stations_management.models.EVDriver;
import elytra.stations_management.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

        assertThat(request1)
                .isEqualTo(request2)
                .isNotEqualTo(request3)
                .isNotEqualTo(null)
                .isNotEqualTo("not a request");
    }

    @Test
    void testHashCode() {
        DriverRegistrationRequest request1 = new DriverRegistrationRequest();
        request1.setDriver(testDriver);
        request1.setUser(testUser);

        DriverRegistrationRequest request2 = new DriverRegistrationRequest();
        request2.setDriver(testDriver);
        request2.setUser(testUser);

        assertThat(request1).hasSameHashCodeAs(request2);
    }

    @Test
    void testToString() {
        DriverRegistrationRequest request = new DriverRegistrationRequest();
        request.setDriver(testDriver);
        request.setUser(testUser);

        String toString = request.toString();
        assertThat(toString).contains("DriverRegistrationRequest");
        assertThat(toString).contains("driver=");
        assertThat(toString).contains("user=");
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