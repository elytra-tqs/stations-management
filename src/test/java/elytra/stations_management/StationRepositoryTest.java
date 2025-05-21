package elytra.stations_management;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import elytra.stations_management.models.Station;

@DataJpaTest
class StationRepositoryTest {
    @Autowired
    private StationRepository stationRepository;

    @Test
    void saveAndRetrieveStation() {
        Station station = Station.builder()
                .name("Central Station")
                .address("123 Main St")
                .latitude(40.12345)
                .longitude(-8.54321)
                .build();
        Station saved = stationRepository.save(station);
        Optional<Station> found = stationRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Central Station", found.get().getName());
    }
}