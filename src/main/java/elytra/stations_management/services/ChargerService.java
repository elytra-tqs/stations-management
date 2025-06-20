package elytra.stations_management.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import elytra.stations_management.exception.InvalidStatusTransitionException;
import elytra.stations_management.models.Charger;
import elytra.stations_management.repositories.ChargerRepository;

@Service
public class ChargerService {

    private final ChargerRepository chargerRepository;

    public ChargerService(ChargerRepository chargerRepository) {
        this.chargerRepository = chargerRepository;
    }

    @Transactional(readOnly = true)
    public Charger.Status getChargerAvailability(Long chargerId) {
        Charger charger = chargerRepository.findById(chargerId)
                .orElseThrow(() -> new RuntimeException("Charger not found"));
        return charger.getStatus();
    }

    @Transactional
    public Charger updateChargerAvailability(Long chargerId, Charger.Status newStatus) {
        Charger charger = chargerRepository.findById(chargerId)
                .orElseThrow(() -> new RuntimeException("Charger not found"));

        validateStatusTransition(charger.getStatus(), newStatus);
        charger.setStatus(newStatus);
        return chargerRepository.save(charger);
    }

    @Transactional(readOnly = true)
    public List<Charger> getChargersByAvailability(Charger.Status status) {
        return chargerRepository.findByStatus(status);
    }

    @Transactional
    public Charger updateCharger(Long chargerId, Charger updatedCharger) {
        Charger existingCharger = chargerRepository.findById(chargerId)
                .orElseThrow(() -> new RuntimeException("Charger not found"));

        existingCharger.setType(updatedCharger.getType());
        existingCharger.setPower(updatedCharger.getPower());
        
        if (updatedCharger.getStatus() != null) {
            validateStatusTransition(existingCharger.getStatus(), updatedCharger.getStatus());
            existingCharger.setStatus(updatedCharger.getStatus());
        }

        return chargerRepository.save(existingCharger);
    }

    @Transactional
    public void deleteCharger(Long chargerId) {
        Charger charger = chargerRepository.findById(chargerId)
                .orElseThrow(() -> new RuntimeException("Charger not found"));
        chargerRepository.delete(charger);
    }

    private void validateStatusTransition(Charger.Status currentStatus,
            Charger.Status newStatus) {
        if (currentStatus == Charger.Status.OUT_OF_SERVICE &&
                newStatus == Charger.Status.BEING_USED) {
            throw new InvalidStatusTransitionException("Cannot transition from OUT_OF_SERVICE to BEING_USED");
        }
    }
}