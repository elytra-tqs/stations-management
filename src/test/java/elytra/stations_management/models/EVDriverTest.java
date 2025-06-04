package elytra.stations_management.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EVDriverTest {

    private EVDriver driver;
    private User user;
    private Car car1;
    private Car car2;

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
                .cars(new ArrayList<>())
                .build();

        car1 = Car.builder()
                .id(1L)
                .model("Tesla Model 3")
                .licensePlate("ABC-1234")
                .batteryCapacity(75.0)
                .chargerType("Type 2")
                .evDriver(driver)
                .build();

        car2 = Car.builder()
                .id(2L)
                .model("Nissan Leaf")
                .licensePlate("XYZ-5678")
                .batteryCapacity(40.0)
                .chargerType("CHAdeMO")
                .evDriver(driver)
                .build();
    }

    @Test
    void testEVDriverCreation() {
        assertThat(driver).isNotNull();
        assertThat(driver.getId()).isEqualTo(1L);
        assertThat(driver.getUser()).isEqualTo(user);
        assertThat(driver.getCars()).isNotNull();
        assertThat(driver.getCars()).isEmpty();
    }

    @Test
    void testNoArgsConstructor() {
        EVDriver emptyDriver = new EVDriver();
        assertThat(emptyDriver).isNotNull();
        assertThat(emptyDriver.getId()).isNull();
        assertThat(emptyDriver.getUser()).isNull();
        assertThat(emptyDriver.getCars()).isNotNull();
        assertThat(emptyDriver.getCars()).isEmpty();
    }

    @Test
    void testAllArgsConstructor() {
        List<Car> carList = Arrays.asList(car1, car2);
        EVDriver fullDriver = new EVDriver(2L, user, carList);
        
        assertThat(fullDriver.getId()).isEqualTo(2L);
        assertThat(fullDriver.getUser()).isEqualTo(user);
        assertThat(fullDriver.getCars()).hasSize(2);
        assertThat(fullDriver.getCars()).containsExactly(car1, car2);
    }

    @Test
    void testBuilder() {
        List<Car> carList = Arrays.asList(car1, car2);
        EVDriver builtDriver = EVDriver.builder()
                .id(3L)
                .user(user)
                .cars(carList)
                .build();

        assertThat(builtDriver.getId()).isEqualTo(3L);
        assertThat(builtDriver.getUser()).isEqualTo(user);
        assertThat(builtDriver.getCars()).hasSize(2);
        assertThat(builtDriver.getCars()).containsExactly(car1, car2);
    }

    @Test
    void testSettersAndGetters() {
        EVDriver testDriver = new EVDriver();
        List<Car> carList = new ArrayList<>();
        carList.add(car1);
        
        testDriver.setId(4L);
        testDriver.setUser(user);
        testDriver.setCars(carList);

        assertThat(testDriver.getId()).isEqualTo(4L);
        assertThat(testDriver.getUser()).isEqualTo(user);
        assertThat(testDriver.getCars()).hasSize(1);
        assertThat(testDriver.getCars()).contains(car1);
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
                .cars(new ArrayList<>())
                .build();

        EVDriver driver2 = EVDriver.builder()
                .id(1L)
                .user(user)
                .cars(new ArrayList<>())
                .build();

        EVDriver driver3 = EVDriver.builder()
                .id(2L)
                .user(user)
                .cars(new ArrayList<>())
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
                .cars(new ArrayList<>())
                .build();

        EVDriver driver2 = EVDriver.builder()
                .id(1L)
                .user(user)
                .cars(new ArrayList<>())
                .build();

        assertThat(driver1).hasSameHashCodeAs(driver2);
    }

    @Test
    void testBuilderWithDefaultCars() {
        EVDriver driverWithDefaultCars = EVDriver.builder()
                .id(5L)
                .user(user)
                .build();

        assertThat(driverWithDefaultCars.getId()).isEqualTo(5L);
        assertThat(driverWithDefaultCars.getUser()).isEqualTo(user);
        // When cars list is not explicitly set in builder, it's null
        assertThat(driverWithDefaultCars.getCars()).isNull();
    }

    @Test
    void testCarListManipulation() {
        List<Car> mutableCarList = new ArrayList<>();
        driver.setCars(mutableCarList);
        
        assertThat(driver.getCars()).isEmpty();
        
        mutableCarList.add(car1);
        assertThat(driver.getCars()).hasSize(1);
        assertThat(driver.getCars()).contains(car1);
        
        mutableCarList.add(car2);
        assertThat(driver.getCars()).hasSize(2);
        assertThat(driver.getCars()).containsExactly(car1, car2);
        
        mutableCarList.remove(car1);
        assertThat(driver.getCars()).hasSize(1);
        assertThat(driver.getCars()).contains(car2);
    }

    @Test
    void testSetAndGetNullValues() {
        EVDriver testDriver = new EVDriver();
        
        testDriver.setUser(null);
        testDriver.setCars(null);

        assertThat(testDriver.getUser()).isNull();
        assertThat(testDriver.getCars()).isNull();
    }

    @Test
    void testBuilderWithPartialData() {
        EVDriver partialDriver = EVDriver.builder()
                .user(user)
                .build();

        assertThat(partialDriver.getId()).isNull();
        assertThat(partialDriver.getUser()).isEqualTo(user);
        assertThat(partialDriver.getCars()).isNull();
    }
}