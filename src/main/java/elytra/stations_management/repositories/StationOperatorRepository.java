package elytra.stations_management.repositories;

import elytra.stations_management.models.StationOperator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StationOperatorRepository extends JpaRepository<StationOperator, Long> {
    Optional<StationOperator> findByUserId(Long userId);
    Optional<StationOperator> findByStationId(Long stationId);
    boolean existsByUserId(Long userId);
    boolean existsByStationId(Long stationId);
}