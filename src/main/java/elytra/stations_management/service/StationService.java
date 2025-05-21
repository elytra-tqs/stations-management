package elytra.stations_management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import elytra.stations_management.models.Charger;
import elytra.stations_management.models.Station;
import elytra.stations_management.repository.StationRepository;

@Service
public class StationService {
    @Autowired
    private StationRepository stationRepository;

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
        return stationRepository.findAll();
    }
}