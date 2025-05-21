package elytra.stations_management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import elytra.stations_management.models.Station;

@Service
public class StationService {
    @Autowired
    private StationRepository stationRepository;

    public Station registerStation(Station station) {
        return stationRepository.save(station);
    }
}