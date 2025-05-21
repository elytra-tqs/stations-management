package elytra.stations_management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import elytra.stations_management.models.Charger;
import elytra.stations_management.models.Charger.Status;

@Repository
public interface ChargerRepository extends JpaRepository<Charger, Long> {
    List<Charger> findByStationIdAndStatus(Long stationId, Status status);

    List<Charger> findByStatus(Status status);
}