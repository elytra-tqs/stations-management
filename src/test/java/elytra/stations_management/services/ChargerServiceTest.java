package elytra.stations_management.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import elytra.stations_management.exception.InvalidStatusTransitionException;
import elytra.stations_management.models.Charger;
import elytra.stations_management.repositories.ChargerRepository;

@ExtendWith(MockitoExtension.class)
class ChargerServiceTest {

    @Mock
    private ChargerRepository chargerRepository;

    @InjectMocks
    private ChargerService chargerService;

    private Charger charger;

    @BeforeEach
    void setUp() {
        charger = Charger.builder()
                .id(1L)
                .type("Type 2")
                .power(50.0)
                .status(Charger.Status.AVAILABLE)
                .build();
    }

    @Test
    void getChargerAvailability_ShouldReturnStatus() {
        when(chargerRepository.findById(1L)).thenReturn(Optional.of(charger));

        Charger.Status status = chargerService.getChargerAvailability(1L);

        assertEquals(Charger.Status.AVAILABLE, status);
        verify(chargerRepository).findById(1L);
    }

    @Test
    void getChargerAvailability_WhenChargerNotFound_ShouldThrowException() {
        when(chargerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> chargerService.getChargerAvailability(1L));
    }

    @Test
    void updateChargerAvailability_ShouldUpdateStatus() {
        when(chargerRepository.findById(1L)).thenReturn(Optional.of(charger));
        when(chargerRepository.save(any(Charger.class))).thenReturn(charger);

        Charger updatedCharger = chargerService.updateChargerAvailability(1L, Charger.Status.BEING_USED);

        assertEquals(Charger.Status.BEING_USED, updatedCharger.getStatus());
        verify(chargerRepository).save(charger);
    }

    @Test
    void updateChargerAvailability_WhenInvalidTransition_ShouldThrowException() {
        charger.setStatus(Charger.Status.OUT_OF_SERVICE);
        when(chargerRepository.findById(1L)).thenReturn(Optional.of(charger));

        assertThrows(InvalidStatusTransitionException.class,
                () -> chargerService.updateChargerAvailability(1L, Charger.Status.BEING_USED));
    }

    @Test
    void getChargersByAvailability_ShouldReturnList() {
        List<Charger> chargers = Arrays.asList(charger);
        when(chargerRepository.findByStatus(Charger.Status.AVAILABLE))
                .thenReturn(chargers);

        List<Charger> result = chargerService.getChargersByAvailability(Charger.Status.AVAILABLE);

        assertEquals(chargers, result);
        verify(chargerRepository).findByStatus(Charger.Status.AVAILABLE);
    }

    @Test
    void updateCharger_ShouldUpdateChargerDetails() {
        when(chargerRepository.findById(1L)).thenReturn(Optional.of(charger));
        when(chargerRepository.save(any(Charger.class))).thenReturn(charger);

        Charger updatedCharger = Charger.builder()
                .type("CCS")
                .power(100.0)
                .status(Charger.Status.AVAILABLE)
                .build();

        Charger result = chargerService.updateCharger(1L, updatedCharger);

        assertEquals("CCS", result.getType());
        assertEquals(100.0, result.getPower());
        verify(chargerRepository).save(charger);
    }

    @Test
    void updateCharger_WhenChargerNotFound_ShouldThrowException() {
        when(chargerRepository.findById(1L)).thenReturn(Optional.empty());

        Charger updatedCharger = Charger.builder()
                .type("CCS")
                .power(100.0)
                .build();

        assertThrows(RuntimeException.class, () -> chargerService.updateCharger(1L, updatedCharger));
    }

    @Test
    void updateCharger_WithStatus_ShouldValidateTransition() {
        charger.setStatus(Charger.Status.OUT_OF_SERVICE);
        when(chargerRepository.findById(1L)).thenReturn(Optional.of(charger));

        Charger updatedCharger = Charger.builder()
                .type("CCS")
                .power(100.0)
                .status(Charger.Status.BEING_USED)
                .build();

        assertThrows(InvalidStatusTransitionException.class,
                () -> chargerService.updateCharger(1L, updatedCharger));
    }

    @Test
    void deleteCharger_ShouldDeleteCharger() {
        when(chargerRepository.findById(1L)).thenReturn(Optional.of(charger));

        chargerService.deleteCharger(1L);

        verify(chargerRepository).delete(charger);
    }

    @Test
    void deleteCharger_WhenChargerNotFound_ShouldThrowException() {
        when(chargerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> chargerService.deleteCharger(1L));
    }
}