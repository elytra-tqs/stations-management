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

    public void deleteCharger(Long id) {
        chargerRepository.deleteById(id);
    }

    public Charger updateCharger(Long id, Charger charger) {
        Charger existingCharger = chargerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Charger not found"));

        existingCharger.setType(charger.getType());
        existingCharger.setPower(charger.getPower());
        
        return chargerRepository.save(existingCharger);
    }

    private void validateStatusTransition(Charger.Status currentStatus,
            Charger.Status newStatus) {
        if (currentStatus == Charger.Status.OUT_OF_SERVICE &&
                newStatus == Charger.Status.BEING_USED) {
            throw new InvalidStatusTransitionException("Cannot transition from OUT_OF_SERVICE to BEING_USED");
        }
    }
}