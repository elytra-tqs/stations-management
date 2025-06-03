package elytra.stations_management.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import elytra.stations_management.models.Car;
import elytra.stations_management.models.EVDriver;
import elytra.stations_management.repositories.CarRepository;
import elytra.stations_management.repositories.EVDriverRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final EVDriverRepository evDriverRepository;

    @Transactional
    public Car registerCar(Car car, Long driverId) {
        EVDriver driver = evDriverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        if (carRepository.existsByLicensePlate(car.getLicensePlate())) {
            throw new RuntimeException("License plate already registered");
        }

        car.setEvDriver(driver);
        return carRepository.save(car);
    }

    @Transactional(readOnly = true)
    public List<Car> getCarsByDriver(Long driverId) {
        return carRepository.findByEvDriverId(driverId);
    }

    @Transactional(readOnly = true)
    public List<Car> getCarsByChargerType(String chargerType) {
        return carRepository.findByChargerType(chargerType);
    }

    @Transactional
    public Car updateCar(Long carId, Car updatedCar) {
        Car existingCar = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        // Check if license plate is being changed and if it's already taken
        if (!existingCar.getLicensePlate().equals(updatedCar.getLicensePlate()) &&
                carRepository.existsByLicensePlate(updatedCar.getLicensePlate())) {
            throw new RuntimeException("License plate already registered");
        }

        existingCar.setModel(updatedCar.getModel());
        existingCar.setLicensePlate(updatedCar.getLicensePlate());
        existingCar.setBatteryCapacity(updatedCar.getBatteryCapacity());
        existingCar.setChargerType(updatedCar.getChargerType());

        return carRepository.save(existingCar);
    }

    @Transactional
    public void deleteCar(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));
        carRepository.delete(car);
    }
} 