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

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Charger charger1 = new Charger();
        charger1.setId(1L);
        Charger charger2 = new Charger();
        charger2.setId(1L);
        Charger charger3 = new Charger();
        charger3.setId(2L);

        Booking booking1 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId("userA").charger(charger1).status(Booking.Status.PENDING).build();
        Booking booking2 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId("userA").charger(charger2).status(Booking.Status.PENDING).build();
        Booking booking3 = Booking.builder().id(2L).startTime(startTime).endTime(endTime).userId("userB").charger(charger3).status(Booking.Status.CONFIRMED).build();

        // Test equality
        assertTrue(booking1.equals(booking1)); // Reflexive
        assertTrue(booking1.equals(booking2)); // Symmetric
        assertTrue(booking2.equals(booking1)); // Symmetric
        assertFalse(booking1.equals(booking3)); // Different object
        assertFalse(booking1.equals(null)); // Vs null
        assertFalse(booking1.equals("string")); // Vs different type

        // Test hashCode
        assertEquals(booking1.hashCode(), booking2.hashCode()); // Equal objects have same hash code
    }

    @Test
    void testToString() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Charger charger = new Charger();
        charger.setId(1L);
        Booking booking = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId("userA").charger(charger).status(Booking.Status.PENDING).build();
        String toStringResult = booking.toString();
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("Booking"));
        assertTrue(toStringResult.contains("id=1"));
        assertTrue(toStringResult.contains("userId=userA"));
        assertTrue(toStringResult.contains("status=PENDING"));
    }

    @Test
    void testCanEqual() {
        Booking booking1 = Booking.builder().id(1L).build();
        Booking booking2 = Booking.builder().id(2L).build();
        assertTrue(booking1.canEqual(booking2));
    }
} 