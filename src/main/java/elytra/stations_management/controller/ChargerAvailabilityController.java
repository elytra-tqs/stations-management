package elytra.stations_management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import elytra.stations_management.exception.InvalidStatusTransitionException;
import elytra.stations_management.models.Charger;
import elytra.stations_management.service.ChargerService;

@RestController
@RequestMapping("/api/chargers")
public class ChargerAvailabilityController {

    private final ChargerService chargerService;

    public ChargerAvailabilityController(ChargerService chargerService) {
        this.chargerService = chargerService;
    }

    @GetMapping("/{chargerId}/availability")
    public ResponseEntity<Charger.AvailabilityStatus> getChargerAvailability(@PathVariable Long chargerId) {
        return ResponseEntity.ok(chargerService.getChargerAvailability(chargerId));
    }

    @PutMapping("/{chargerId}/availability")
    public ResponseEntity<Charger> updateChargerAvailability(
            @PathVariable Long chargerId,
            @RequestParam Charger.AvailabilityStatus status) {
        try {
            return ResponseEntity.ok(chargerService.updateChargerAvailability(chargerId, status));
        } catch (InvalidStatusTransitionException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/availability/{status}")
    public ResponseEntity<List<Charger>> getChargersByAvailability(
            @PathVariable Charger.AvailabilityStatus status) {
        return ResponseEntity.ok(chargerService.getChargersByAvailability(status));
    }

    @GetMapping("/station/{stationId}/available")
    public ResponseEntity<List<Charger>> getAvailableChargersAtStation(@PathVariable Long stationId) {
        return ResponseEntity.ok(chargerService.getAvailableChargersAtStation(stationId));
    }
}