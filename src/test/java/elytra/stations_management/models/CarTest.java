package elytra.stations_management.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CarTest {

    private Car car;
    private EVDriver driver;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testdriver")
                .email("driver@example.com")
                .firstName("John")
                .lastName("Doe")
                .userType(User.UserType.EV_DRIVER)
                .build();

        driver = EVDriver.builder()
                .id(1L)
                .user(user)
                .build();

        car = Car.builder()
                .id(1L)
                .model("Tesla Model 3")
                .licensePlate("ABC-1234")
                .batteryCapacity(75.0)
                .chargerType("Type 2")
                .evDriver(driver)
                .build();
    }

    @Test
    void testCarCreation() {
        assertThat(car).isNotNull();
        assertThat(car.getId()).isEqualTo(1L);
        assertThat(car.getModel()).isEqualTo("Tesla Model 3");
        assertThat(car.getLicensePlate()).isEqualTo("ABC-1234");
        assertThat(car.getBatteryCapacity()).isEqualTo(75.0);
        assertThat(car.getChargerType()).isEqualTo("Type 2");
        assertThat(car.getEvDriver()).isEqualTo(driver);
    }

    @Test
    void testNoArgsConstructor() {
        Car emptyCar = new Car();
        assertThat(emptyCar).isNotNull();
        assertThat(emptyCar.getId()).isNull();
        assertThat(emptyCar.getModel()).isNull();
        assertThat(emptyCar.getLicensePlate()).isNull();
        assertThat(emptyCar.getBatteryCapacity()).isNull();
        assertThat(emptyCar.getChargerType()).isNull();
        assertThat(emptyCar.getEvDriver()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        Car fullCar = new Car(2L, "Nissan Leaf", "XYZ-5678", 40.0, "CHAdeMO", driver);
        assertThat(fullCar.getId()).isEqualTo(2L);
        assertThat(fullCar.getModel()).isEqualTo("Nissan Leaf");
        assertThat(fullCar.getLicensePlate()).isEqualTo("XYZ-5678");
        assertThat(fullCar.getBatteryCapacity()).isEqualTo(40.0);
        assertThat(fullCar.getChargerType()).isEqualTo("CHAdeMO");
        assertThat(fullCar.getEvDriver()).isEqualTo(driver);
    }

    @Test
    void testBuilder() {
        Car builtCar = Car.builder()
                .id(3L)
                .model("BMW i3")
                .licensePlate("DEF-9012")
                .batteryCapacity(42.0)
                .chargerType("CCS")
                .evDriver(driver)
                .build();

        assertThat(builtCar.getId()).isEqualTo(3L);
        assertThat(builtCar.getModel()).isEqualTo("BMW i3");
        assertThat(builtCar.getLicensePlate()).isEqualTo("DEF-9012");
        assertThat(builtCar.getBatteryCapacity()).isEqualTo(42.0);
        assertThat(builtCar.getChargerType()).isEqualTo("CCS");
        assertThat(builtCar.getEvDriver()).isEqualTo(driver);
    }

    @Test
    void testSettersAndGetters() {
        Car testCar = new Car();
        
        testCar.setId(4L);
        testCar.setModel("Audi e-tron");
        testCar.setLicensePlate("GHI-3456");
        testCar.setBatteryCapacity(95.0);
        testCar.setChargerType("Type 2");
        testCar.setEvDriver(driver);

        assertThat(testCar.getId()).isEqualTo(4L);
        assertThat(testCar.getModel()).isEqualTo("Audi e-tron");
        assertThat(testCar.getLicensePlate()).isEqualTo("GHI-3456");
        assertThat(testCar.getBatteryCapacity()).isEqualTo(95.0);
        assertThat(testCar.getChargerType()).isEqualTo("Type 2");
        assertThat(testCar.getEvDriver()).isEqualTo(driver);
    }

    @Test
    void testToString() {
        String carString = car.toString();
        assertThat(carString).contains("Car");
        assertThat(carString).contains("id=1");
        assertThat(carString).contains("model=Tesla Model 3");
        assertThat(carString).contains("licensePlate=ABC-1234");
        assertThat(carString).contains("batteryCapacity=75.0");
        assertThat(carString).contains("chargerType=Type 2");
    }

    @Test
    void testEquals() {
        Car car1 = Car.builder()
                .id(1L)
                .model("Tesla Model 3")
                .licensePlate("ABC-1234")
                .batteryCapacity(75.0)
                .chargerType("Type 2")
                .evDriver(driver)
                .build();

        Car car2 = Car.builder()
                .id(1L)
                .model("Tesla Model 3")
                .licensePlate("ABC-1234")
                .batteryCapacity(75.0)
                .chargerType("Type 2")
                .evDriver(driver)
                .build();

        Car car3 = Car.builder()
                .id(2L)
                .model("Nissan Leaf")
                .licensePlate("XYZ-5678")
                .batteryCapacity(40.0)
                .chargerType("CHAdeMO")
                .evDriver(driver)
                .build();

        assertThat(car1).isEqualTo(car2);
        assertThat(car1).isNotEqualTo(car3);
        assertThat(car1).isNotEqualTo(null);
        assertThat(car1).isNotEqualTo("not a car");
    }

    @Test
    void testHashCode() {
        Car car1 = Car.builder()
                .id(1L)
                .model("Tesla Model 3")
                .licensePlate("ABC-1234")
                .batteryCapacity(75.0)
                .chargerType("Type 2")
                .evDriver(driver)
                .build();

        Car car2 = Car.builder()
                .id(1L)
                .model("Tesla Model 3")
                .licensePlate("ABC-1234")
                .batteryCapacity(75.0)
                .chargerType("Type 2")
                .evDriver(driver)
                .build();

        assertThat(car1.hashCode()).isEqualTo(car2.hashCode());
    }

    @Test
    void testBuilderWithPartialData() {
        Car partialCar = Car.builder()
                .model("Partial Model")
                .licensePlate("PART-123")
                .build();

        assertThat(partialCar.getId()).isNull();
        assertThat(partialCar.getModel()).isEqualTo("Partial Model");
        assertThat(partialCar.getLicensePlate()).isEqualTo("PART-123");
        assertThat(partialCar.getBatteryCapacity()).isNull();
        assertThat(partialCar.getChargerType()).isNull();
        assertThat(partialCar.getEvDriver()).isNull();
    }

    @Test
    void testSetAndGetNullValues() {
        Car testCar = new Car();
        
        testCar.setModel(null);
        testCar.setLicensePlate(null);
        testCar.setBatteryCapacity(null);
        testCar.setChargerType(null);
        testCar.setEvDriver(null);

        assertThat(testCar.getModel()).isNull();
        assertThat(testCar.getLicensePlate()).isNull();
        assertThat(testCar.getBatteryCapacity()).isNull();
        assertThat(testCar.getChargerType()).isNull();
        assertThat(testCar.getEvDriver()).isNull();
    }
}