package elytra.stations_management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import elytra.stations_management.dto.DriverRegistrationRequest;
import elytra.stations_management.models.EVDriver;
import elytra.stations_management.services.EVDriverService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class EVDriverController {
    private final EVDriverService evDriverService;

    @PostMapping
    public ResponseEntity<EVDriver> registerDriver(@RequestBody DriverRegistrationRequest request) {
        try {
            return ResponseEntity.ok(evDriverService.registerDriver(request.getDriver(), request.getUser()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EVDriver> getDriverById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(evDriverService.getDriverById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<EVDriver> getDriverByUserId(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(evDriverService.getDriverByUserId(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<EVDriver>> getAllDrivers() {
        return ResponseEntity.ok(evDriverService.getAllDrivers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EVDriver> updateDriver(@PathVariable Long id, @RequestBody EVDriver driver) {
        try {
            return ResponseEntity.ok(evDriverService.updateDriver(id, driver));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long id) {
        try {
            evDriverService.deleteDriver(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
