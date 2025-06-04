package elytra.stations_management.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EVDriverTest {

    private EVDriver driver;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testdriver")
                .password("hashedPassword")
                .email("driver@example.com")
                .firstName("John")
                .lastName("Doe")
                .userType(User.UserType.EV_DRIVER)
                .build();

        driver = EVDriver.builder()
                .id(1L)
                .user(user)
                .build();
    }

    @Test
    void testEVDriverCreation() {
        assertThat(driver).isNotNull();
        assertThat(driver.getId()).isEqualTo(1L);
        assertThat(driver.getUser()).isEqualTo(user);
    }

    @Test
    void testNoArgsConstructor() {
        EVDriver emptyDriver = new EVDriver();
        assertThat(emptyDriver).isNotNull();
        assertThat(emptyDriver.getId()).isNull();
        assertThat(emptyDriver.getUser()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        EVDriver fullDriver = new EVDriver(2L, user);
        
        assertThat(fullDriver.getId()).isEqualTo(2L);
        assertThat(fullDriver.getUser()).isEqualTo(user);
    }

    @Test
    void testBuilder() {
        EVDriver builtDriver = EVDriver.builder()
                .id(3L)
                .user(user)
                .build();

        assertThat(builtDriver.getId()).isEqualTo(3L);
        assertThat(builtDriver.getUser()).isEqualTo(user);
    }

    @Test
    void testSettersAndGetters() {
        EVDriver testDriver = new EVDriver();
        
        testDriver.setId(4L);
        testDriver.setUser(user);

        assertThat(testDriver.getId()).isEqualTo(4L);
        assertThat(testDriver.getUser()).isEqualTo(user);
    }

    @Test
    void testToString() {
        String driverString = driver.toString();
        assertThat(driverString).contains("EVDriver", "id=1", "user=");
    }

    @Test
    void testEquals() {
        EVDriver driver1 = EVDriver.builder()
                .id(1L)
                .user(user)
                .build();

        EVDriver driver2 = EVDriver.builder()
                .id(1L)
                .user(user)
                .build();

        EVDriver driver3 = EVDriver.builder()
                .id(2L)
                .user(user)
                .build();

        assertThat(driver1)
                .isEqualTo(driver2)
                .isNotEqualTo(driver3)
                .isNotEqualTo(null)
                .isNotEqualTo("not a driver");
    }

    @Test
    void testHashCode() {
        EVDriver driver1 = EVDriver.builder()
                .id(1L)
                .user(user)
                .build();

        EVDriver driver2 = EVDriver.builder()
                .id(1L)
                .user(user)
                .build();

        assertThat(driver1).hasSameHashCodeAs(driver2);
    }

    @Test
    void testBuilderWithPartialData() {
        EVDriver partialDriver = EVDriver.builder()
                .user(user)
                .build();

        assertThat(partialDriver.getId()).isNull();
        assertThat(partialDriver.getUser()).isEqualTo(user);
    }

    @Test
    void testSetAndGetNullValues() {
        EVDriver testDriver = new EVDriver();
        
        testDriver.setUser(null);

        assertThat(testDriver.getUser()).isNull();
    }

    @Test
    void testDifferentUsers() {
        User user2 = User.builder()
                .id(2L)
                .username("testdriver2")
                .email("driver2@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .userType(User.UserType.EV_DRIVER)
                .build();

        EVDriver driver1 = EVDriver.builder()
                .id(1L)
                .user(user)
                .build();

        EVDriver driver2 = EVDriver.builder()
                .id(1L)
                .user(user2)
                .build();

        assertThat(driver1).isNotEqualTo(driver2);
    }
}