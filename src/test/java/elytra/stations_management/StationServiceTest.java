package elytra.stations_management;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import elytra.stations_management.models.Charger;
import elytra.stations_management.models.Station;
import elytra.stations_management.repository.StationRepository;
import elytra.stations_management.service.StationService;

class StationServiceTest {
    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private StationService stationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerStation_savesAndReturnsStation() {
        Station station = Station.builder()
                .name("Central Station")
                .address("123 Main St")
                .latitude(40.12345)
                .longitude(-8.54321)
                .build();
        when(stationRepository.save(station)).thenReturn(station);
        Station result = stationService.registerStation(station);
        assertEquals(station, result);
        verify(stationRepository, times(1)).save(station);
    }

    @Test
    void registerStation_withChargers_persistsChargers() {
        Charger charger1 = Charger.builder().type("Type2").power(22.0).build();
        Charger charger2 = Charger.builder().type("CCS").power(50.0).build();
        List<Charger> chargers = Arrays.asList(charger1, charger2);
        Station station = Station.builder()
                .name("Central Station")
                .address("123 Main St")
                .latitude(40.12345)
                .longitude(-8.54321)
                .chargers(chargers)
                .build();
        for (Charger c : chargers)
            c.setStation(station);
        when(stationRepository.save(station)).thenReturn(station);
        Station result = stationService.registerStation(station);
        assertEquals(2, result.getChargers().size());
        assertTrue(result.getChargers().stream().allMatch(c -> c.getStation() == result));
        verify(stationRepository, times(1)).save(station);
    }

    @Test
    void registerStation_missingRequiredFields_throwsException() {
        Station station = Station.builder()
                .address("123 Main St")
                .latitude(40.12345)
                .longitude(-8.54321)
                .build();
        when(stationRepository.save(station)).thenThrow(new IllegalArgumentException("Name is required"));
        assertThrows(IllegalArgumentException.class, () -> stationService.registerStation(station));
    }

    @Test
    void registerStation_duplicateName_throwsException() {
        Station station = Station.builder()
                .name("Central Station")
                .address("123 Main St")
                .latitude(40.12345)
                .longitude(-8.54321)
                .build();
        when(stationRepository.save(station)).thenThrow(new RuntimeException("Duplicate station"));
        assertThrows(RuntimeException.class, () -> stationService.registerStation(station));
    }

    @Test
    void registerStation_returnsStationWithId() {
        Station station = Station.builder()
                .name("Central Station")
                .address("123 Main St")
                .latitude(40.12345)
                .longitude(-8.54321)
                .build();
        Station savedStation = Station.builder()
                .id(1L)
                .name("Central Station")
                .address("123 Main St")
                .latitude(40.12345)
                .longitude(-8.54321)
                .build();
        when(stationRepository.save(station)).thenReturn(savedStation);
        Station result = stationService.registerStation(station);
        assertNotNull(result.getId());
        assertEquals(1L, result.getId());
    }
}