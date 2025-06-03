package elytra.stations_management.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

class BookingTest {

    @Test
    void testNoArgsConstructor() {
        Booking booking = new Booking();
        assertNotNull(booking);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Charger charger = new Charger();
        Booking booking = new Booking(1L, startTime, endTime, "user123", charger, Booking.Status.PENDING);
        assertEquals(1L, booking.getId());
        assertEquals(startTime, booking.getStartTime());
        assertEquals(endTime, booking.getEndTime());
        assertEquals("user123", booking.getUserId());
        assertEquals(charger, booking.getCharger());
        assertEquals(Booking.Status.PENDING, booking.getStatus());
    }

    @Test
    void testBuilder() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Charger charger = new Charger();
        Booking booking = Booking.builder()
                .id(2L)
                .startTime(startTime)
                .endTime(endTime)
                .userId("user456")
                .charger(charger)
                .status(Booking.Status.CONFIRMED)
                .build();
        assertEquals(2L, booking.getId());
        assertEquals(startTime, booking.getStartTime());
        assertEquals(endTime, booking.getEndTime());
        assertEquals("user456", booking.getUserId());
        assertEquals(charger, booking.getCharger());
        assertEquals(Booking.Status.CONFIRMED, booking.getStatus());
    }

    @Test
    void testSettersAndGetters() {
        Booking booking = new Booking();
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Charger charger = new Charger();

        booking.setId(3L);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setUserId("user789");
        booking.setCharger(charger);
        booking.setStatus(Booking.Status.COMPLETED);

        assertEquals(3L, booking.getId());
        assertEquals(startTime, booking.getStartTime());
        assertEquals(endTime, booking.getEndTime());
        assertEquals("user789", booking.getUserId());
        assertEquals(charger, booking.getCharger());
        assertEquals(Booking.Status.COMPLETED, booking.getStatus());
    }

    @Test
    void testEnumValues() {
        assertNotNull(Booking.Status.PENDING);
        assertNotNull(Booking.Status.CONFIRMED);
        assertNotNull(Booking.Status.CANCELLED);
        assertNotNull(Booking.Status.COMPLETED);
    }
} 