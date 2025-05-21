package elytra.stations_management.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import elytra.stations_management.exception.InvalidStatusTransitionException;
import elytra.stations_management.models.Charger;
import elytra.stations_management.repository.ChargerRepository;

@ExtendWith(MockitoExtension.class)
class ChargerServiceTest {

    @Mock
    private ChargerRepository chargerRepository;

    private ChargerService chargerService;
    private Charger charger;

    @BeforeEach
    void setUp() {
        chargerService = new ChargerService(chargerRepository);
        charger = Charger.builder()
                .id(1L)
                .type("Type 2")
                .power(22.0)
                .availabilityStatus(Charger.AvailabilityStatus.AVAILABLE)
                .build();
    }

    @Test
    void whenGettingChargerAvailability_thenReturnStatus() {
        when(chargerRepository.findById(1L)).thenReturn(Optional.of(charger));

        Charger.AvailabilityStatus status = chargerService.getChargerAvailability(1L);

        assertEquals(Charger.AvailabilityStatus.AVAILABLE, status);
        verify(chargerRepository).findById(1L);
    }

    @Test
    void whenGettingNonExistentChargerAvailability_thenThrowException() {
        when(chargerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> chargerService.getChargerAvailability(1L));
    }

    @Test
    void whenUpdatingChargerAvailability_thenStatusIsUpdated() {
        when(chargerRepository.findById(1L)).thenReturn(Optional.of(charger));
        when(chargerRepository.save(any(Charger.class))).thenAnswer(i -> i.getArgument(0));

        Charger updatedCharger = chargerService.updateChargerAvailability(1L, Charger.AvailabilityStatus.IN_USE);

        assertEquals(Charger.AvailabilityStatus.IN_USE, updatedCharger.getAvailabilityStatus());
        verify(chargerRepository).findById(1L);
        verify(chargerRepository).save(charger);
    }

    @Test
    void whenUpdatingNonExistentChargerAvailability_thenThrowException() {
        when(chargerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> chargerService.updateChargerAvailability(1L, Charger.AvailabilityStatus.IN_USE));
    }

    @Test
    void whenUpdatingFromOutOfServiceToInUse_thenThrowException() {
        charger.setAvailabilityStatus(Charger.AvailabilityStatus.OUT_OF_SERVICE);
        when(chargerRepository.findById(1L)).thenReturn(Optional.of(charger));

        assertThrows(InvalidStatusTransitionException.class,
                () -> chargerService.updateChargerAvailability(1L, Charger.AvailabilityStatus.IN_USE));
    }

    @Test
    void whenGettingChargersByAvailability_thenReturnList() {
        List<Charger> chargers = Arrays.asList(charger);
        when(chargerRepository.findByAvailabilityStatus(Charger.AvailabilityStatus.AVAILABLE))
                .thenReturn(chargers);

        List<Charger> result = chargerService.getChargersByAvailability(Charger.AvailabilityStatus.AVAILABLE);

        assertEquals(chargers, result);
        verify(chargerRepository).findByAvailabilityStatus(Charger.AvailabilityStatus.AVAILABLE);
    }

    @Test
    void whenGettingAvailableChargersAtStation_thenReturnList() {
        List<Charger> chargers = Arrays.asList(charger);
        when(chargerRepository.findByStationIdAndAvailabilityStatus(1L, Charger.AvailabilityStatus.AVAILABLE))
                .thenReturn(chargers);

        List<Charger> result = chargerService.getAvailableChargersAtStation(1L);

        assertEquals(chargers, result);
        verify(chargerRepository).findByStationIdAndAvailabilityStatus(1L, Charger.AvailabilityStatus.AVAILABLE);
    }
}