package elytra.stations_management.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import elytra.stations_management.models.Charger;
import elytra.stations_management.repository.ChargerRepository;
import elytra.stations_management.exception.InvalidStatusTransitionException;

@Service
public class ChargerService {

    private final ChargerRepository chargerRepository;

    public ChargerService(ChargerRepository chargerRepository) {
        this.chargerRepository = chargerRepository;
    }

    @Transactional(readOnly = true)
    public Charger.AvailabilityStatus getChargerAvailability(Long chargerId) {
        Charger charger = chargerRepository.findById(chargerId)
                .orElseThrow(() -> new RuntimeException("Charger not found"));
        return charger.getAvailabilityStatus();
    }

    @Transactional
    public Charger updateChargerAvailability(Long chargerId, Charger.AvailabilityStatus newStatus) {
        Charger charger = chargerRepository.findById(chargerId)
                .orElseThrow(() -> new RuntimeException("Charger not found"));

        validateStatusTransition(charger.getAvailabilityStatus(), newStatus);
        charger.setAvailabilityStatus(newStatus);
        return chargerRepository.save(charger);
    }

    @Transactional(readOnly = true)
    public List<Charger> getChargersByAvailability(Charger.AvailabilityStatus status) {
        return chargerRepository.findByAvailabilityStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Charger> getAvailableChargersAtStation(Long stationId) {
        return chargerRepository.findByStationIdAndAvailabilityStatus(stationId, Charger.AvailabilityStatus.AVAILABLE);
    }

    private void validateStatusTransition(Charger.AvailabilityStatus currentStatus,
            Charger.AvailabilityStatus newStatus) {
        if (currentStatus == Charger.AvailabilityStatus.OUT_OF_SERVICE &&
                newStatus == Charger.AvailabilityStatus.IN_USE) {
            throw new InvalidStatusTransitionException("Cannot transition from OUT_OF_SERVICE to IN_USE");
        }
    }
}