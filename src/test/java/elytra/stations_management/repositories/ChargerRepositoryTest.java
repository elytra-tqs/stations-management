package elytra.stations_management.repositories;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import elytra.stations_management.models.Charger;
import elytra.stations_management.models.Station;
import elytra.stations_management.repositories.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import elytra.stations_management.repositories.ChargerRepository;

@DataJpaTest
class ChargerRepositoryTest {
    @Autowired
    private ChargerRepository chargerRepository;

    @Autowired
    private StationRepository stationRepository;

    @Test
    void testFindAll() {
        Station station = Station.builder()
                .name("Test Station")
                .address("123 Main St")
                .latitude(0.0)
                .longitude(0.0)
                .build();
        Station savedStation = stationRepository.save(station); // Save first!

        Charger charger = Charger.builder()
                .type("Fast DC")
                .power(50.0)
                .status(Charger.Status.AVAILABLE)
                .station(savedStation) // Reference saved entity
                .build();

        Charger savedCharger = chargerRepository.save(charger);

        Optional<Charger> foundCharger = chargerRepository.findById(savedCharger.getId());
        assertTrue(foundCharger.isPresent());
        assertEquals("Fast DC", foundCharger.get().getType());
    }

    @Test
    void testFindByStationId() {
        // First, save the station
        Station station = Station.builder()
                .name("Test Station")
                .address("123 Main St")
                .latitude(0.0)
                .longitude(0.0)
                .build();
        Station savedStation = stationRepository.save(station);

        // Now, create a charger and associate it with the saved station
        Charger charger = Charger.builder()
                .type("Fast DC")
                .power(50.0)
                .status(Charger.Status.AVAILABLE)
                .station(savedStation)
                .build();

        chargerRepository.save(charger);

        // Test the repository method
        List<Charger> chargers = chargerRepository.findByStationIdAndStatus(
                savedStation.getId(), Charger.Status.AVAILABLE);

        assertEquals(1, chargers.size());
        assertEquals("Fast DC", chargers.get(0).getType());
    }






}
