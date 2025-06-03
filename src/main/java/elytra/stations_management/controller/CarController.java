package elytra.stations_management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import elytra.stations_management.models.Car;
import elytra.stations_management.services.CarService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @PostMapping("/driver/{driverId}")
    public ResponseEntity<Car> registerCar(@PathVariable Long driverId, @RequestBody Car car) {
        try {
            return ResponseEntity.ok(carService.registerCar(car, driverId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<Car>> getCarsByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(carService.getCarsByDriver(driverId));
    }

    @GetMapping("/charger-type/{chargerType}")
    public ResponseEntity<List<Car>> getCarsByChargerType(@PathVariable String chargerType) {
        return ResponseEntity.ok(carService.getCarsByChargerType(chargerType));
    }

    @PutMapping("/{carId}")
    public ResponseEntity<Car> updateCar(@PathVariable Long carId, @RequestBody Car car) {
        try {
            return ResponseEntity.ok(carService.updateCar(carId, car));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{carId}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long carId) {
        try {
            carService.deleteCar(carId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 