package elytra.stations_management.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import elytra.stations_management.models.Car;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByEvDriverId(Long driverId);
    List<Car> findByChargerType(String chargerType);
    boolean existsByLicensePlate(String licensePlate);
} 