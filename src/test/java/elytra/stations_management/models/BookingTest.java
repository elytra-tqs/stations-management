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
    void testEquals_SameObject() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Charger charger = Charger.builder().id(1L).build();
        Booking booking1 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId("userA").charger(charger).status(Booking.Status.PENDING).build();
        assertEquals(booking1, booking1);
    }

    @Test
    void testEquals_DifferentObjectsSameValues() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Charger charger1 = Charger.builder().id(1L).build();
        Charger charger2 = Charger.builder().id(1L).build();

        Booking booking1 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId("userA").charger(charger1).status(Booking.Status.PENDING).build();
        Booking booking2 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId("userA").charger(charger2).status(Booking.Status.PENDING).build();

        assertEquals(booking1, booking2);
    }

    @Test
    void testEquals_DifferentObjectsDifferentIds() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.now().plusHours(1);
        Charger charger1 = Charger.builder().id(1L).build();

        Booking booking1 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId("userA").charger(charger1).status(Booking.Status.PENDING).build();
        Booking booking2 = Booking.builder().id(2L).startTime(startTime).endTime(endTime).userId("userA").charger(charger1).status(Booking.Status.PENDING).build();

        assertNotEquals(booking1, booking2);
    }

    @Test
    void testEquals_DifferentObjectsDifferentUserIds() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.now().plusHours(1);
        Charger charger = Charger.builder().id(1L).build();

        Booking booking1 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId("userA").charger(charger).status(Booking.Status.PENDING).build();
        Booking booking2 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId("userB").charger(charger).status(Booking.Status.PENDING).build();

        assertNotEquals(booking1, booking2);
    }

    @Test
    void testEquals_VsNull() {
        Booking booking = Booking.builder().build();
        assertNotEquals(null, booking);
    }

    @Test
    void testEquals_VsDifferentType() {
        Booking booking = Booking.builder().build();
        assertNotEquals("a string", booking);
    }

    @Test
    void testEquals_WithNullFields() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Charger charger1 = Charger.builder().id(1L).build();
        Charger charger2 = Charger.builder().id(1L).build();

        // Test with null userId
        Booking booking1 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId(null).charger(charger1).status(Booking.Status.PENDING).build();
        Booking booking2 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId(null).charger(charger2).status(Booking.Status.PENDING).build();
        Booking booking3 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId("userA").charger(charger1).status(Booking.Status.PENDING).build();
        assertEquals(booking1, booking2);
        assertNotEquals(booking1, booking3);

        // Test with null startTime
        Booking booking4 = Booking.builder().id(1L).startTime(null).endTime(endTime).userId("userA").charger(charger1).status(Booking.Status.PENDING).build();
        Booking booking5 = Booking.builder().id(1L).startTime(null).endTime(endTime).userId("userA").charger(charger2).status(Booking.Status.PENDING).build();
         Booking booking6 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId("userA").charger(charger1).status(Booking.Status.PENDING).build();
        assertEquals(booking4, booking5);
        assertNotEquals(booking4, booking6);

        // Test with null endTime
         Booking booking7 = Booking.builder().id(1L).startTime(startTime).endTime(null).userId("userA").charger(charger1).status(Booking.Status.PENDING).build();
        Booking booking8 = Booking.builder().id(1L).startTime(startTime).endTime(null).userId("userA").charger(charger2).status(Booking.Status.PENDING).build();
         Booking booking9 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId("userA").charger(charger1).status(Booking.Status.PENDING).build();
        assertEquals(booking7, booking8);
        assertNotEquals(booking7, booking9);

         // Test with null charger
         Booking booking10 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId("userA").charger(null).status(Booking.Status.PENDING).build();
        Booking booking11 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId("userA").charger(null).status(Booking.Status.PENDING).build();
         Booking booking12 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId("userA").charger(charger1).status(Booking.Status.PENDING).build();
        assertEquals(booking10, booking11);
        assertNotEquals(booking10, booking12);

         // Test with null status
         Booking booking13 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId("userA").charger(charger1).status(null).build();
        Booking booking14 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId("userA").charger(charger2).status(null).build();
         Booking booking15 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId("userA").charger(charger1).status(Booking.Status.PENDING).build();
        assertEquals(booking13, booking14);
        assertNotEquals(booking13, booking15);
    }

    @Test
    void testHashCode_SameObjects() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Charger charger1 = Charger.builder().id(1L).build();
        Charger charger2 = Charger.builder().id(1L).build();

        Booking booking1 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId("userA").charger(charger1).status(Booking.Status.PENDING).build();
        Booking booking2 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).userId("userA").charger(charger2).status(Booking.Status.PENDING).build();
        assertEquals(booking1.hashCode(), booking2.hashCode());
    }

    @Test
    void testCanEqual_SameType() {
        Booking booking1 = Booking.builder().id(1L).build();
        Booking booking2 = Booking.builder().id(2L).build();
        assertTrue(booking1.canEqual(booking2));
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
} 