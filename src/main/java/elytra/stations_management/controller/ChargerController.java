package elytra.stations_management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import elytra.stations_management.models.Charger;
import elytra.stations_management.service.ChargerService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chargers")
@RequiredArgsConstructor
public class ChargerController {
    private final ChargerService chargerService;

    @GetMapping("/{chargerId}/availability")
    public ResponseEntity<Charger.Status> getChargerAvailability(@PathVariable Long chargerId) {
        return ResponseEntity.ok(chargerService.getChargerAvailability(chargerId));
    }

    @PutMapping("/{chargerId}/availability")
    public ResponseEntity<Charger> updateChargerAvailability(
            @PathVariable Long chargerId,
            @RequestBody Charger.Status newStatus) {
        try {
            return ResponseEntity.ok(chargerService.updateChargerAvailability(chargerId, newStatus));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/availability/{status}")
    public ResponseEntity<List<Charger>> getChargersByAvailability(@PathVariable Charger.Status status) {
        return ResponseEntity.ok(chargerService.getChargersByAvailability(status));
    }

    @GetMapping("/station/{stationId}/available")
    public ResponseEntity<List<Charger>> getAvailableChargersAtStation(@PathVariable Long stationId) {
        return ResponseEntity.ok(chargerService.getAvailableChargersAtStation(stationId));
    }
}