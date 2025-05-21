package elytra.stations_management;

import org.springframework.data.jpa.repository.JpaRepository;

import elytra.stations_management.models.Station;

public interface StationRepository extends JpaRepository<Station, Long> {
}