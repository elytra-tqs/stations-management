package elytra.stations_management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import elytra.stations_management.models.Station;
import elytra.stations_management.service.StationService;

@RestController
@RequestMapping("/stations")
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
}