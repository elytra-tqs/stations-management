package elytra.stations_management.services;

import java.util.ArrayList;
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
import elytra.stations_management.repositories.StationRepository;

class StationServiceTest {
    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private StationService stationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        stationRepository.deleteAll();
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

    @Test
    void getStationById_existingId_returnsStation() {
        Station station = Station.builder()
                .id(1L)
                .name("Central Station")
                .address("123 Main St")
                .latitude(40.12345)
                .longitude(-8.54321)
                .build();
        when(stationRepository.findById(1L)).thenReturn(java.util.Optional.of(station));
        Station result = stationService.getStationById(1L);
        assertEquals(station, result);
    }

    @Test
    void getStationById_nonExistingId_throwsException() {
        when(stationRepository.findById(1L)).thenReturn(java.util.Optional.empty());
        assertThrows(RuntimeException.class, () -> stationService.getStationById(1L));
    }

    @Test
    void getAllStations_returnsListOfStations() {
        Station station1 = Station.builder()
                .id(1L)
                .name("Central Station")
                .address("123 Main St")
                .latitude(40.12345)
                .longitude(-8.54321)
                .build();
        Station station2 = Station.builder()
                .id(2L)
                .name("North Station")
                .address("456 North St")
                .latitude(41.12345)
                .longitude(-9.54321)
                .build();
        List<Station> stations = Arrays.asList(station1, station2);
        when(stationRepository.findAll()).thenReturn(stations);
        List<Station> result = stationService.getAllStations();
        assertEquals(2, result.size());
        assertEquals(station1, result.get(0));
        assertEquals(station2, result.get(1));
    }

    @Test
    void getAllStations_emptyList_returnsEmptyList() {
        List<Station> stations = Arrays.asList();
        when(stationRepository.findAll()).thenReturn(stations);
        List<Station> result = stationService.getAllStations();
        assertEquals(0, result.size());
    }

    @Test
    void addChargerToStation_addsChargerToStation() {
        Station station = Station.builder()
                .id(1L)
                .name("Central Station")
                .address("123 Main St")
                .latitude(40.12345)
                .longitude(-8.54321)
                .chargers(new java.util.ArrayList<>())
                .build();

        Charger charger = Charger.builder()
                .type("CCS")
                .power(50.0)
                .status(Charger.Status.AVAILABLE)
                .build();

        when(stationRepository.findById(1L)).thenReturn(java.util.Optional.of(station));
        when(stationRepository.save(station)).thenReturn(station);

        Charger result = stationService.addChargerToStation(1L, charger);

        assertEquals(charger, result);
        assertEquals(station, charger.getStation());
        assertTrue(station.getChargers().contains(charger));
        verify(stationRepository).save(station);
    }

    @Test
    void updateStation_updatesStationDetails() {
        Station existingStation = Station.builder()
                .id(1L)
                .name("Central Station")
                .address("123 Main St")
                .latitude(40.12345)
                .longitude(-8.54321)
                .chargers(new java.util.ArrayList<>())
                .build();

        Station updatedStation = Station.builder()
                .name("Updated Central Station")
                .address("456 Updated St")
                .latitude(41.12345)
                .longitude(-9.54321)
                .build();

        when(stationRepository.findById(1L)).thenReturn(java.util.Optional.of(existingStation));
        when(stationRepository.save(existingStation)).thenReturn(existingStation);

        Station result = stationService.updateStation(1L, updatedStation);

        assertEquals("Updated Central Station", result.getName());
        assertEquals("456 Updated St", result.getAddress());
        assertEquals(41.12345, result.getLatitude());
        assertEquals(-9.54321, result.getLongitude());
        verify(stationRepository).save(existingStation);
    }

    @Test
    void updateStation_shouldReplaceChargersAndSetStationReference() {
        Station originalStation = Station.builder()
                .id(1L)
                .name("Central Station")
                .address("123 Main St")
                .latitude(40.12345)
                .longitude(-8.54321)
                .build();

        Charger oldCharger = new Charger();
        oldCharger.setType("Type1");
        oldCharger.setStation(originalStation);

        originalStation.setChargers(new ArrayList<>(List.of(oldCharger)));

        when(stationRepository.findById(1L)).thenReturn(java.util.Optional.of(originalStation));
        when(stationRepository.save(originalStation)).thenReturn(originalStation);


        Station updateData = Station.builder()
                .name("Updated Central Station")
                .address("456 Updated St")
                .latitude(41.12345)
                .longitude(-9.54321)
                .build();

        Charger newCharger1 = new Charger();
        newCharger1.setType("Type2");
        Charger newCharger2 = new Charger();
        newCharger2.setType("Type3");
        updateData.setChargers(new ArrayList<>(List.of(newCharger1, newCharger2)));


        Station updatedStation = stationService.updateStation(1L, updateData);


        assertEquals(2, updatedStation.getChargers().size());
        for (Charger charger : updatedStation.getChargers()) {
            assertTrue(charger.getType().equals("Type2") || charger.getType().equals("Type3"));
            assertEquals(updatedStation, charger.getStation());
        }
    }

    @Test
    void deleteStation_deletesExistingStation() {
        Station station = Station.builder()
                .id(1L)
                .name("Central Station")
                .address("123 Main St")
                .latitude(40.12345)
                .longitude(-8.54321)
                .build();

        when(stationRepository.findById(1L)).thenReturn(java.util.Optional.of(station));

        stationService.deleteStation(1L);

        verify(stationRepository, times(1)).delete(station);
    }


}