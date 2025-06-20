package elytra.stations_management.services;

import elytra.stations_management.exception.InvalidBookingException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import elytra.stations_management.models.Booking;
import elytra.stations_management.models.Charger;
import elytra.stations_management.repositories.BookingRepository;
import elytra.stations_management.repositories.ChargerRepository;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ChargerService chargerService;
    private final BookingService self;

    public BookingService(BookingRepository bookingRepository, ChargerService chargerService, @Lazy BookingService self) {
        this.bookingRepository = bookingRepository;
        this.chargerService = chargerService;
        this.self = self;
    }

    @Transactional
    public Booking createBooking(Booking booking) {
        validateBooking(booking);

        // Check if charger is available
        Charger charger = booking.getCharger();
        if (charger.getStatus() != Charger.Status.AVAILABLE) {
            throw new InvalidBookingException("Charger is not available for booking");
        }

        // Check for overlapping bookings
        List<Booking> existingBookings = bookingRepository.findOverlappingBookings(
            charger.getId(),
            booking.getStartTime(),
            booking.getEndTime()
        );

        if (!existingBookings.isEmpty()) {
            throw new InvalidBookingException("Charger is already booked for this time period");
        }

        // Update charger status
        chargerService.updateChargerAvailability(charger.getId(), Charger.Status.BEING_USED);

        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new InvalidBookingException("Booking not found"));
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsByUser(String userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsByCharger(Long chargerId) {
        return bookingRepository.findByChargerId(chargerId);
    }

    @Transactional
    public Booking updateBookingStatus(Long bookingId, Booking.Status newStatus) {
        Booking booking = self.getBookingById(bookingId);

        validateStatusTransition(booking.getStatus(), newStatus);
        booking.setStatus(newStatus);

        // If booking is cancelled or completed, make charger available again
        if (newStatus == Booking.Status.CANCELLED || newStatus == Booking.Status.COMPLETED) {
            chargerService.updateChargerAvailability(booking.getCharger().getId(), Charger.Status.AVAILABLE);
        }

        return bookingRepository.save(booking);
    }

    @Transactional
    public void deleteBooking(Long bookingId) {
        Booking booking = self.getBookingById(bookingId);

        // If booking is active, make charger available again
        if (booking.getStatus() == Booking.Status.CONFIRMED) {
            chargerService.updateChargerAvailability(booking.getCharger().getId(), Charger.Status.AVAILABLE);
        }

        bookingRepository.delete(booking);
    }

    private void validateBooking(Booking booking) {
        if (booking.getStartTime() == null) {
            throw new InvalidBookingException("Start time is required");
        }
        if (booking.getEndTime() == null || !booking.getEndTime().isAfter(booking.getStartTime())) {
            throw new InvalidBookingException("End time must be after start time");
        }
        if (booking.getUserId() == null || booking.getUserId().trim().isEmpty()) {
            throw new InvalidBookingException("User ID is required");
        }
        if (booking.getCharger() == null) {
            throw new InvalidBookingException("Charger is required");
        }
        if (booking.getStartTime().isBefore(LocalDateTime.now())) {
            throw new InvalidBookingException("Cannot create booking in the past");
        }
    }

    private void validateStatusTransition(Booking.Status currentStatus, Booking.Status newStatus) {
        if (currentStatus == Booking.Status.COMPLETED && newStatus != Booking.Status.COMPLETED) {
            throw new InvalidBookingException("Cannot change status of a completed booking");
        }
        if (currentStatus == Booking.Status.CANCELLED && newStatus != Booking.Status.CANCELLED) {
            throw new InvalidBookingException("Cannot change status of a cancelled booking");
        }
    }
}
