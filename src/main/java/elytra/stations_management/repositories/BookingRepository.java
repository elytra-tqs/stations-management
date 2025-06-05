package elytra.stations_management.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import elytra.stations_management.models.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    List<Booking> findByUserId(Long userId);
    
    List<Booking> findByChargerId(Long chargerId);

    @Query("SELECT b FROM Booking b WHERE b.charger.id = :chargerId " +
            "AND b.status != 'CANCELLED' " +
            "AND ((b.startTime <= :endTime AND b.endTime >= :startTime) " +
            "OR (b.startTime >= :startTime AND b.startTime <= :endTime))")
    List<Booking> findOverlappingBookings(
            @Param("chargerId") Long chargerId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT b FROM Booking b WHERE b.charger.id = :chargerId " +
           "AND b.startTime >= :startTime " +
           "AND b.startTime <= :endTime " +
           "AND b.status != 'CANCELLED'")
    List<Booking> findBookingsInTimeRange(
        @Param("chargerId") Long chargerId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    List<Booking> findByChargerIdAndStatus(Long chargerId, Booking.Status status);
}
