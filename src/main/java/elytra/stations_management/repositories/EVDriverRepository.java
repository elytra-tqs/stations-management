package elytra.stations_management.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import elytra.stations_management.models.EVDriver;

import java.util.Optional;

@Repository
public interface EVDriverRepository extends JpaRepository<EVDriver, Long> {
    Optional<EVDriver> findByUserId(Long userId);
}
