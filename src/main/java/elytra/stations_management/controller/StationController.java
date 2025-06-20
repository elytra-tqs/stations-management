package elytra.stations_management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import elytra.stations_management.models.Station;
import elytra.stations_management.models.Charger;
import elytra.stations_management.services.StationService;

@RestController
@RequestMapping("api/v1/stations")
public class StationController {

    @Autowired
    private StationService stationService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Station> registerStation(@RequestBody Station station) {
        if (station.getName() == null || station.getName().trim().isEmpty() ||
                station.getAddress() == null || station.getAddress().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Station registeredStation = stationService.registerStation(station);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredStation);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Station> getAllStations() {
        return stationService.getAllStations();
    }

    @GetMapping(value = "/{stationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Station> getStationById(@PathVariable Long stationId) {
        try {
            Station station = stationService.getStationById(stationId);
            return ResponseEntity.ok(station);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/{stationId}/chargers", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Charger>> getChargersByStation(@PathVariable Long stationId) {
        try {
            Station station = stationService.getStationById(stationId);
            return ResponseEntity.ok(station.getChargers());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/{stationId}/chargers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Charger> createCharger(@PathVariable Long stationId, @RequestBody Charger charger) {
        try {
            Charger createdCharger = stationService.addChargerToStation(stationId, charger);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCharger);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(value = "/{stationId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Station> updateStation(@PathVariable Long stationId, @RequestBody Station station) {
        try {
            Station updatedStation = stationService.updateStation(stationId, station);
            return ResponseEntity.ok(updatedStation);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "/{stationId}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long stationId) {
        try {
            stationService.deleteStation(stationId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
