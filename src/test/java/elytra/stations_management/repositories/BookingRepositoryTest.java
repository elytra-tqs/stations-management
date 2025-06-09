package elytra.stations_management.repositories;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import elytra.stations_management.models.Booking;
import elytra.stations_management.models.Car;
import elytra.stations_management.models.Charger;
import elytra.stations_management.models.EVDriver;
import elytra.stations_management.models.Station;
import elytra.stations_management.models.User;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    private User createAndPersistUser(String username) {
        User user = User.builder()
                .username(username)
                .email(username + "@test.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .userType(User.UserType.EV_DRIVER)
                .build();
        return entityManager.persist(user);
    }

    private Car createAndPersistCar(User user, String chargerType) {
        EVDriver evDriver = EVDriver.builder()
                .user(user)
                .build();
        entityManager.persist(evDriver);

        Car car = Car.builder()
                .model("Tesla Model 3")
                .licensePlate("TEST-" + user.getUsername())
                .batteryCapacity(75.0)
                .chargerType(chargerType)
                .evDriver(evDriver)
                .build();
        return entityManager.persist(car);
    }

    @Test
    void saveAndRetrieveBooking() {
        // Create and save user
        User user = createAndPersistUser("user123");

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

        // Create and save car
        Car car = createAndPersistCar(user, charger.getType());

        // Create and save booking
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);
        Booking booking = Booking.builder()
                .startTime(startTime)
                .endTime(endTime)
                .user(user)
                .charger(charger)
                .car(car)
                .status(Booking.Status.PENDING)
                .build();
        entityManager.persist(booking);
        entityManager.flush();

        // Retrieve booking
        Booking found = bookingRepository.findById(booking.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getUser().getUsername()).isEqualTo("user123");
        assertThat(found.getEndTime()).isEqualTo(endTime);
        assertThat(found.getStatus()).isEqualTo(Booking.Status.PENDING);
    }

    @Test
    void findByUserId() {
        // Create and save user
        User user1 = createAndPersistUser("user1");
        User user2 = createAndPersistUser("user2");

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

        // Create cars for users
        Car car1 = createAndPersistCar(user1, charger.getType());
        Car car2 = createAndPersistCar(user2, charger.getType());

        // Create bookings for user1
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        Booking booking1 = Booking.builder()
                .startTime(startTime)
                .endTime(startTime.plusHours(1))
                .user(user1)
                .charger(charger)
                .car(car1)
                .status(Booking.Status.PENDING)
                .build();
        entityManager.persist(booking1);

        // Create booking for user2
        Booking booking2 = Booking.builder()
                .startTime(startTime.plusHours(2))
                .endTime(startTime.plusHours(3))
                .user(user2)
                .charger(charger)
                .car(car2)
                .status(Booking.Status.CONFIRMED)
                .build();
        entityManager.persist(booking2);
        entityManager.flush();

        // Find bookings by user1
        List<Booking> user1Bookings = bookingRepository.findByUserId(user1.getId());
        assertThat(user1Bookings).hasSize(1);
        assertThat(user1Bookings.get(0).getUser().getUsername()).isEqualTo("user1");
    }

    @Test
    void findByChargerId() {
        // Create and save user
        User user = createAndPersistUser("user123");

        // Create and save station
        Station station = Station.builder()
                .name("Test Station")
                .address("123 Main St")
                .latitude(0.0)
                .longitude(0.0)
                .build();
        entityManager.persist(station);

        // Create and save chargers
        Charger charger1 = Charger.builder()
                .type("Type 2")
                .power(50.0)
                .status(Charger.Status.AVAILABLE)
                .station(station)
                .build();
        entityManager.persist(charger1);

        Charger charger2 = Charger.builder()
                .type("CCS")
                .power(150.0)
                .status(Charger.Status.AVAILABLE)
                .station(station)
                .build();
        entityManager.persist(charger2);

        // Create car
        Car car = createAndPersistCar(user, charger1.getType());

        // Create bookings
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        Booking booking1 = Booking.builder()
                .startTime(startTime)
                .endTime(startTime.plusHours(1))
                .user(user)
                .charger(charger1)
                .car(car)
                .status(Booking.Status.PENDING)
                .build();
        entityManager.persist(booking1);

        Booking booking2 = Booking.builder()
                .startTime(startTime.plusHours(2))
                .endTime(startTime.plusHours(3))
                .user(user)
                .charger(charger1)
                .car(car)
                .status(Booking.Status.CONFIRMED)
                .build();
        entityManager.persist(booking2);
        entityManager.flush();

        // Find bookings by charger1
        List<Booking> charger1Bookings = bookingRepository.findByChargerId(charger1.getId());
        assertThat(charger1Bookings).hasSize(2);
    }

    @Test
    void findOverlappingBookings() {
        // Create and save user
        User user = createAndPersistUser("user123");

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

        // Create car
        Car car = createAndPersistCar(user, charger.getType());

        // Create existing booking
        LocalDateTime existingStart = LocalDateTime.now().plusHours(2);
        LocalDateTime existingEnd = existingStart.plusHours(2);
        Booking existingBooking = Booking.builder()
                .startTime(existingStart)
                .endTime(existingEnd)
                .user(user)
                .charger(charger)
                .car(car)
                .status(Booking.Status.CONFIRMED)
                .build();
        entityManager.persist(existingBooking);
        entityManager.flush();

        // Test overlapping scenarios
        LocalDateTime overlapStart = existingStart.plusMinutes(30);
        LocalDateTime overlapEnd = existingEnd.plusMinutes(30);
        List<Booking> overlapping = bookingRepository.findOverlappingBookings(
                charger.getId(), overlapStart, overlapEnd);
        assertThat(overlapping).hasSize(1);

        // Test non-overlapping scenario
        LocalDateTime nonOverlapStart = existingEnd.plusHours(1);
        LocalDateTime nonOverlapEnd = nonOverlapStart.plusHours(1);
        List<Booking> nonOverlapping = bookingRepository.findOverlappingBookings(
                charger.getId(), nonOverlapStart, nonOverlapEnd);
        assertThat(nonOverlapping).isEmpty();
    }

    @Test
    void findBookingsInTimeRange() {
        // Create and save user
        User user = createAndPersistUser("user123");

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

        // Create car
        Car car = createAndPersistCar(user, charger.getType());

        LocalDateTime baseTime = LocalDateTime.now();
        
        // Create bookings at different times
        Booking booking1 = Booking.builder()
                .startTime(baseTime.plusHours(1))
                .endTime(baseTime.plusHours(2))
                .user(user)
                .charger(charger)
                .car(car)
                .status(Booking.Status.CONFIRMED)
                .build();
        entityManager.persist(booking1);

        Booking booking2 = Booking.builder()
                .startTime(baseTime.plusHours(3))
                .endTime(baseTime.plusHours(4))
                .user(user)
                .charger(charger)
                .car(car)
                .status(Booking.Status.CONFIRMED)
                .build();
        entityManager.persist(booking2);

        Booking booking3 = Booking.builder()
                .startTime(baseTime.plusHours(5))
                .endTime(baseTime.plusHours(6))
                .user(user)
                .charger(charger)
                .car(car)
                .status(Booking.Status.CANCELLED)
                .build();
        entityManager.persist(booking3);
        entityManager.flush();

        // Find bookings in time range
        List<Booking> bookingsInRange = bookingRepository.findBookingsInTimeRange(
                charger.getId(), 
                baseTime, 
                baseTime.plusHours(4));
        
        assertThat(bookingsInRange).hasSize(2);
        assertThat(bookingsInRange).allMatch(b -> b.getStatus() != Booking.Status.CANCELLED);
    }

    @Test
    void findByChargerIdAndStatus() {
        // Create and save user
        User user = createAndPersistUser("user123");

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

        // Create car
        Car car = createAndPersistCar(user, charger.getType());

        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        
        // Create bookings with different statuses
        Booking pendingBooking = Booking.builder()
                .startTime(startTime)
                .endTime(startTime.plusHours(1))
                .user(user)
                .charger(charger)
                .car(car)
                .status(Booking.Status.PENDING)
                .build();
        entityManager.persist(pendingBooking);

        Booking confirmedBooking = Booking.builder()
                .startTime(startTime.plusHours(2))
                .endTime(startTime.plusHours(3))
                .user(user)
                .charger(charger)
                .car(car)
                .status(Booking.Status.CONFIRMED)
                .build();
        entityManager.persist(confirmedBooking);
        entityManager.flush();

        // Find confirmed bookings
        List<Booking> confirmedBookings = bookingRepository.findByChargerIdAndStatus(
                charger.getId(), Booking.Status.CONFIRMED);
        assertThat(confirmedBookings).hasSize(1);
        assertThat(confirmedBookings.get(0).getStatus()).isEqualTo(Booking.Status.CONFIRMED);
    }
}