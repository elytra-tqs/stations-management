package elytra.stations_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import elytra.stations_management.models.Station;

public interface StationRepository extends JpaRepository<Station, Long> {
}