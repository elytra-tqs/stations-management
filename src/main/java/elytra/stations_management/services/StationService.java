package elytra.stations_management.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import elytra.stations_management.models.Charger;
import elytra.stations_management.models.Station;
import elytra.stations_management.models.StationOperator;
import elytra.stations_management.repositories.StationRepository;
import elytra.stations_management.repositories.StationOperatorRepository;

@Service
public class StationService {
    @Autowired
    private StationRepository stationRepository;
    
    @Autowired
    private StationOperatorRepository stationOperatorRepository;

    @Transactional
    public Station registerStation(Station station) {
        if (station.getChargers() != null) {
            for (Charger charger : station.getChargers()) {
                charger.setStation(station);
            }
        }
        return stationRepository.save(station);
    }

    public List<Station> getAllStations() {
        List<Station> stations = stationRepository.findAll();
        stations.forEach(this::populateOperatorUsername);
        return stations;
    }

    public Station getStationById(Long stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found"));
        populateOperatorUsername(station);
        return station;
    }

    @Transactional
    public Charger addChargerToStation(Long stationId, Charger charger) {
        Station station = getStationById(stationId);
        charger.setStation(station);

        station.getChargers().add(charger);
        stationRepository.save(station);
        return charger;
    }

    @Transactional
    public Station updateStation(Long stationId, Station station) {
        Station existingStation = getStationById(stationId);
        existingStation.setName(station.getName());
        existingStation.setAddress(station.getAddress());
        existingStation.setLatitude(station.getLatitude());
        existingStation.setLongitude(station.getLongitude());


        existingStation.getChargers().clear();
        if (station.getChargers() != null) {
            for (Charger charger : station.getChargers()) {
                charger.setStation(existingStation);
                existingStation.getChargers().add(charger);
            }
        }
        stationRepository.save(existingStation);

        return existingStation;
    }

    @Transactional
    public void deleteStation(Long stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found"));
        stationRepository.delete(station);
    }
    
    private void populateOperatorUsername(Station station) {
        stationOperatorRepository.findByStationId(station.getId())
                .ifPresent(operator -> {
                    if (operator.getUser() != null) {
                        station.setOperatorUsername(operator.getUser().getUsername());
                    }
                });
    }
}
