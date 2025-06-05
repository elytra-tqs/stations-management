package elytra.stations_management.repositories;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import elytra.stations_management.models.Booking;
import elytra.stations_management.models.Charger;
import elytra.stations_management.models.Station;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void saveAndRetrieveBooking() {
        // Create and save station
        Station station = Station.builder()
                .name("Test Station")
                .address("123 Main St")
                .latitude(0.0)
                .longitude(0.0)
                .build();
        entityManager.persist(station);

        // Create and save charger
        Charger charger = Charger.builder()
                .type("Type 2")
                .power(50.0)
                .status(Charger.Status.AVAILABLE)
                .station(station)
                .build();
        entityManager.persist(charger);

        // Create and save booking
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);
        Booking booking = Booking.builder()
                .startTime(startTime)
                .endTime(endTime)
                .userId("user123")
                .charger(charger)
                .status(Booking.Status.PENDING)
                .build();
        entityManager.persist(booking);

        // Retrieve booking
        Booking found = bookingRepository.findById(booking.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getUserId()).isEqualTo("user123");
        assertThat(found.getEndTime()).isEqualTo(endTime);
        assertThat(found.getStatus()).isEqualTo(Booking.Status.PENDING);
    }

    @Test
    void findByUserId() {
        // Create and save station
        Station station = Station.builder()
                .name("Test Station")
                .address("123 Main St")
                .latitude(0.0)
                .longitude(0.0)
                .build();
        entityManager.persist(station);

        // Create and save charger
        Charger charger = Charger.builder()
                .type("Type 2")
                .power(50.0)
                .status(Charger.Status.AVAILABLE)
                .station(station)
                .build();
        entityManager.persist(charger);

        // Create and save bookings
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);
        Booking booking1 = Booking.builder()
                .startTime(startTime)
                .endTime(endTime)
                .userId("user123")
                .charger(charger)
                .status(Booking.Status.PENDING)
                .build();

        LocalDateTime startTime2 = startTime.plusHours(2);
        LocalDateTime endTime2 = startTime2.plusHours(1);
        Booking booking2 = Booking.builder()
                .startTime(startTime2)
                .endTime(endTime2)
                .userId("user123")
                .charger(charger)
                .status(Booking.Status.CONFIRMED)
                .build();

        entityManager.persist(booking1);
        entityManager.persist(booking2);
        entityManager.flush();

        // Test findByUserId
        List<Booking> userBookings = bookingRepository.findByUserId("user123");
        assertThat(userBookings)
            .hasSize(2)
            .allMatch(booking -> booking.getUserId().equals("user123"));
    }

    @Test
    void findByChargerId() {
        // Create and save station
        Station station = Station.builder()
                .name("Test Station")
                .address("123 Main St")
                .latitude(0.0)
                .longitude(0.0)
                .build();
        entityManager.persist(station);

        // Create and save charger
        Charger charger = Charger.builder()
                .type("Type 2")
                .power(50.0)
                .status(Charger.Status.AVAILABLE)
                .station(station)
                .build();
        entityManager.persist(charger);

        // Create and save booking
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);
        Booking booking = Booking.builder()
                .startTime(startTime)
                .endTime(endTime)
                .userId("user123")
                .charger(charger)
                .status(Booking.Status.PENDING)
                .build();
        entityManager.persist(booking);

        // Test findByChargerId
        List<Booking> chargerBookings = bookingRepository.findByChargerId(charger.getId());
        assertThat(chargerBookings).hasSize(1);
        assertThat(chargerBookings.get(0).getCharger().getId()).isEqualTo(charger.getId());
    }

    @Test
    void findOverlappingBookings() {
        // Create and save station
        Station station = Station.builder()
                .name("Test Station")
                .address("123 Main St")
                .latitude(0.0)
                .longitude(0.0)
                .build();
        entityManager.persist(station);

        // Create and save charger
        Charger charger = Charger.builder()
                .type("Type 2")
                .power(50.0)
                .status(Charger.Status.AVAILABLE)
                .station(station)
                .build();
        entityManager.persist(charger);

        // Create and save booking
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);
        Booking booking = Booking.builder()
                .startTime(startTime)
                .endTime(endTime)
                .userId("user123")
                .charger(charger)
                .status(Booking.Status.PENDING)
                .build();
        entityManager.persist(booking);

        // Test findOverlappingBookings
        LocalDateTime searchStart = startTime.minusMinutes(30);
        LocalDateTime searchEnd = endTime.plusMinutes(30);
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                charger.getId(), searchStart, searchEnd);
        assertThat(overlappingBookings).hasSize(1);
        assertThat(overlappingBookings.get(0).getId()).isEqualTo(booking.getId());
    }

    @Test
    void findByChargerIdAndStatus() {
        // Create and save station
        Station station = Station.builder()
                .name("Test Station")
                .address("123 Main St")
                .latitude(0.0)
                .longitude(0.0)
                .build();
        entityManager.persist(station);

        // Create and save charger
        Charger charger = Charger.builder()
                .type("Type 2")
                .power(50.0)
                .status(Charger.Status.AVAILABLE)
                .station(station)
                .build();
        entityManager.persist(charger);

        // Create and save bookings
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);
        Booking booking1 = Booking.builder()
                .startTime(startTime)
                .endTime(endTime)
                .userId("user123")
                .charger(charger)
                .status(Booking.Status.PENDING)
                .build();
        entityManager.persist(booking1);

        Booking booking2 = Booking.builder()
                .startTime(startTime.plusHours(2))
                .endTime(startTime.plusHours(3))
                .userId("user456")
                .charger(charger)
                .status(Booking.Status.CONFIRMED)
                .build();
        entityManager.persist(booking2);

        // Test findByChargerIdAndStatus
        List<Booking> pendingBookings = bookingRepository.findByChargerIdAndStatus(
                charger.getId(), Booking.Status.PENDING);
        assertThat(pendingBookings).hasSize(1);
        assertThat(pendingBookings.get(0).getCharger().getId()).isEqualTo(charger.getId());
        assertThat(pendingBookings.get(0).getStatus()).isEqualTo(Booking.Status.PENDING);
    }
} 