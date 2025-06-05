package elytra.stations_management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import elytra.stations_management.models.Charger;
import elytra.stations_management.services.ChargerService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/chargers")
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

    @PutMapping("/{chargerId}")
    public ResponseEntity<Charger> updateCharger(
            @PathVariable Long chargerId,
            @RequestBody Charger charger) {
        try {
            return ResponseEntity.ok(chargerService.updateCharger(chargerId, charger));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{chargerId}")
    public ResponseEntity<Void> deleteCharger(@PathVariable Long chargerId) {
        try {
            chargerService.deleteCharger(chargerId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}