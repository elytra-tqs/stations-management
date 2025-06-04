package elytra.stations_management.services;

import elytra.stations_management.models.Car;
import elytra.stations_management.models.EVDriver;
import elytra.stations_management.repositories.CarRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private EVDriverRepository evDriverRepository;

    @InjectMocks
    private CarService carService;

    private Car testCar;
    private EVDriver testDriver;

    @BeforeEach
    void setUp() {
        testDriver = EVDriver.builder()
                .id(1L)
                .build();

        testCar = Car.builder()
                .id(1L)
                .model("Tesla Model 3")
                .licensePlate("ABC-1234")
                .batteryCapacity(75.0)
                .chargerType("Type 2")
                .evDriver(testDriver)
                .build();
    }

    @Test
    void registerCar_ShouldSaveCar_WhenValidData() {
        Car newCar = Car.builder()
                .model("Tesla Model S")
                .licensePlate("XYZ-5678")
                .batteryCapacity(100.0)
                .chargerType("Type 2")
                .build();

        when(evDriverRepository.findById(1L)).thenReturn(Optional.of(testDriver));
        when(carRepository.existsByLicensePlate("XYZ-5678")).thenReturn(false);
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        Car savedCar = carService.registerCar(newCar, 1L);

        assertThat(savedCar).isEqualTo(testCar);
        verify(evDriverRepository).findById(1L);
        verify(carRepository).existsByLicensePlate("XYZ-5678");
        verify(carRepository).save(any(Car.class));
    }

    @Test
    void registerCar_ShouldThrowException_WhenDriverNotFound() {
        Car newCar = Car.builder()
                .model("Tesla Model S")
                .licensePlate("XYZ-5678")
                .batteryCapacity(100.0)
                .chargerType("Type 2")
                .build();

        when(evDriverRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.registerCar(newCar, 999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Driver not found");

        verify(evDriverRepository).findById(999L);
        verify(carRepository, never()).save(any());
    }

    @Test
    void registerCar_ShouldThrowException_WhenLicensePlateExists() {
        Car newCar = Car.builder()
                .model("Tesla Model S")
                .licensePlate("EXISTING-123")
                .batteryCapacity(100.0)
                .chargerType("Type 2")
                .build();

        when(evDriverRepository.findById(1L)).thenReturn(Optional.of(testDriver));
        when(carRepository.existsByLicensePlate("EXISTING-123")).thenReturn(true);

        assertThatThrownBy(() -> carService.registerCar(newCar, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("License plate already registered");

        verify(carRepository).existsByLicensePlate("EXISTING-123");
        verify(carRepository, never()).save(any());
    }

    @Test
    void getCarsByDriver_ShouldReturnCars() {
        List<Car> cars = Arrays.asList(testCar, 
                Car.builder()
                    .id(2L)
                    .model("Nissan Leaf")
                    .licensePlate("DEF-5678")
                    .batteryCapacity(40.0)
                    .chargerType("CHAdeMO")
                    .evDriver(testDriver)
                    .build());

        when(carRepository.findByEvDriverId(1L)).thenReturn(cars);

        List<Car> result = carService.getCarsByDriver(1L);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(cars);
        verify(carRepository).findByEvDriverId(1L);
    }

    @Test
    void getCarsByDriver_ShouldReturnEmptyList_WhenNoCars() {
        when(carRepository.findByEvDriverId(1L)).thenReturn(Arrays.asList());

        List<Car> result = carService.getCarsByDriver(1L);

        assertThat(result).isEmpty();
        verify(carRepository).findByEvDriverId(1L);
    }

    @Test
    void getCarsByChargerType_ShouldReturnCars() {
        List<Car> cars = Arrays.asList(testCar,
                Car.builder()
                    .id(2L)
                    .model("BMW i3")
                    .licensePlate("GHI-9012")
                    .batteryCapacity(42.0)
                    .chargerType("Type 2")
                    .evDriver(testDriver)
                    .build());

        when(carRepository.findByChargerType("Type 2")).thenReturn(cars);

        List<Car> result = carService.getCarsByChargerType("Type 2");

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(cars);
        verify(carRepository).findByChargerType("Type 2");
    }

    @Test
    void updateCar_ShouldUpdateCar_WhenValidData() {
        Car updatedCarData = Car.builder()
                .model("Tesla Model 3 Updated")
                .licensePlate("ABC-1234")
                .batteryCapacity(80.0)
                .chargerType("CCS")
                .build();

        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(carRepository.save(any(Car.class))).thenReturn(testCar);

        Car updatedCar = carService.updateCar(1L, updatedCarData);

        assertThat(updatedCar).isEqualTo(testCar);
        verify(carRepository).findById(1L);
        verify(carRepository, never()).existsByLicensePlate(anyString());
        verify(carRepository).save(testCar);
    }

    @Test
    void updateCar_ShouldThrowException_WhenCarNotFound() {
        Car updatedCarData = Car.builder()
                .model("Tesla Model 3 Updated")
                .licensePlate("ABC-1234")
                .batteryCapacity(80.0)
                .chargerType("CCS")
                .build();

        when(carRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.updateCar(999L, updatedCarData))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Car not found");

        verify(carRepository).findById(999L);
        verify(carRepository, never()).save(any());
    }

    @Test
    void updateCar_ShouldThrowException_WhenNewLicensePlateExists() {
        Car updatedCarData = Car.builder()
                .model("Tesla Model 3 Updated")
                .licensePlate("NEW-LICENSE")
                .batteryCapacity(80.0)
                .chargerType("CCS")
                .build();

        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));
        when(carRepository.existsByLicensePlate("NEW-LICENSE")).thenReturn(true);

        assertThatThrownBy(() -> carService.updateCar(1L, updatedCarData))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("License plate already registered");

        verify(carRepository).findById(1L);
        verify(carRepository).existsByLicensePlate("NEW-LICENSE");
        verify(carRepository, never()).save(any());
    }

    @Test
    void deleteCar_ShouldDeleteCar_WhenCarExists() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(testCar));

        carService.deleteCar(1L);

        verify(carRepository).findById(1L);
        verify(carRepository).delete(testCar);
    }

    @Test
    void deleteCar_ShouldThrowException_WhenCarNotFound() {
        when(carRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.deleteCar(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Car not found");

        verify(carRepository).findById(999L);
        verify(carRepository, never()).delete(any());
    }
}