package elytra.stations_management.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

class BookingTest {

    private User createTestUser(Long id, String username) {
        return User.builder()
                .id(id)
                .username(username)
                .email(username + "@test.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .userType(User.UserType.EV_DRIVER)
                .build();
    }

    private Car createTestCar(Long id) {
        return Car.builder()
                .id(id)
                .model("Tesla Model 3")
                .licensePlate("TEST" + id)
                .batteryCapacity(75.0)
                .chargerType("Type 2")
                .build();
    }

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
        User user = createTestUser(123L, "user123");
        Car car = createTestCar(1L);
        Booking booking = new Booking(1L, startTime, endTime, user, charger, car, Booking.Status.PENDING);
        assertEquals(1L, booking.getId());
        assertEquals(startTime, booking.getStartTime());
        assertEquals(endTime, booking.getEndTime());
        assertEquals(user, booking.getUser());
        assertEquals(charger, booking.getCharger());
        assertEquals(Booking.Status.PENDING, booking.getStatus());
    }

    @Test
    void testBuilder() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Charger charger = new Charger();
        User user = createTestUser(456L, "user456");
        Booking booking = Booking.builder()
                .id(2L)
                .startTime(startTime)
                .endTime(endTime)
                .user(user)
                .charger(charger)
                .car(createTestCar(2L))
                .status(Booking.Status.CONFIRMED)
                .build();
        assertEquals(2L, booking.getId());
        assertEquals(startTime, booking.getStartTime());
        assertEquals(endTime, booking.getEndTime());
        assertEquals(456L, booking.getUser().getId());
        assertEquals(charger, booking.getCharger());
        assertEquals(Booking.Status.CONFIRMED, booking.getStatus());
    }

    @Test
    void testSettersAndGetters() {
        Booking booking = new Booking();
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Charger charger = new Charger();
        User user = createTestUser(789L, "user789");

        booking.setId(3L);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setUser(user);
        booking.setCharger(charger);
        booking.setCar(createTestCar(3L));
        booking.setStatus(Booking.Status.COMPLETED);

        assertEquals(3L, booking.getId());
        assertEquals(startTime, booking.getStartTime());
        assertEquals(endTime, booking.getEndTime());
        assertEquals(789L, booking.getUser().getId());
        assertEquals(charger, booking.getCharger());
        assertNotNull(booking.getCar());
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
        User user = createTestUser(1L, "userA");
        Booking booking1 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).user(user).charger(charger).car(createTestCar(1L)).status(Booking.Status.PENDING).build();
        assertEquals(booking1, booking1);
    }

    @Test
    void testEquals_DifferentObjectsSameValues() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Charger charger1 = Charger.builder().id(1L).build();
        Charger charger2 = Charger.builder().id(1L).build();
        User user = createTestUser(1L, "userA");

        Booking booking1 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).user(user).charger(charger1).car(createTestCar(1L)).status(Booking.Status.PENDING).build();
        Booking booking2 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).user(user).charger(charger2).car(createTestCar(1L)).status(Booking.Status.PENDING).build();

        assertEquals(booking1, booking2);
    }

    @Test
    void testEquals_DifferentObjectsDifferentIds() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.now().plusHours(1);
        Charger charger1 = Charger.builder().id(1L).build();
        User user = createTestUser(1L, "userA");

        Booking booking1 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).user(user).charger(charger1).car(createTestCar(1L)).status(Booking.Status.PENDING).build();
        Booking booking2 = Booking.builder().id(2L).startTime(startTime).endTime(endTime).user(user).charger(charger1).car(createTestCar(1L)).status(Booking.Status.PENDING).build();

        assertNotEquals(booking1, booking2);
    }

    @Test
    void testEquals_DifferentObjectsDifferentUsers() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.now().plusHours(1);
        Charger charger = Charger.builder().id(1L).build();
        User userA = createTestUser(1L, "userA");
        User userB = createTestUser(2L, "userB");

        Booking booking1 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).user(userA).charger(charger).car(createTestCar(1L)).status(Booking.Status.PENDING).build();
        Booking booking2 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).user(userB).charger(charger).car(createTestCar(1L)).status(Booking.Status.PENDING).build();

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
        User user = createTestUser(1L, "userA");

        // Test with null user
        Booking booking1 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).user(null).charger(charger1).car(createTestCar(1L)).status(Booking.Status.PENDING).build();
        Booking booking2 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).user(null).charger(charger2).car(createTestCar(1L)).status(Booking.Status.PENDING).build();
        Booking booking3 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).user(user).charger(charger1).car(createTestCar(1L)).status(Booking.Status.PENDING).build();
        assertEquals(booking1, booking2);
        assertNotEquals(booking1, booking3);

        // Test with null startTime
        Booking booking4 = Booking.builder().id(1L).startTime(null).endTime(endTime).user(user).charger(charger1).car(createTestCar(1L)).status(Booking.Status.PENDING).build();
        Booking booking5 = Booking.builder().id(1L).startTime(null).endTime(endTime).user(user).charger(charger2).car(createTestCar(1L)).status(Booking.Status.PENDING).build();
        Booking booking6 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).user(user).charger(charger1).car(createTestCar(1L)).status(Booking.Status.PENDING).build();
        assertEquals(booking4, booking5);
        assertNotEquals(booking4, booking6);

        // Test with null endTime
        Booking booking7 = Booking.builder().id(1L).startTime(startTime).endTime(null).user(user).charger(charger1).car(createTestCar(1L)).status(Booking.Status.PENDING).build();
        Booking booking8 = Booking.builder().id(1L).startTime(startTime).endTime(null).user(user).charger(charger2).car(createTestCar(1L)).status(Booking.Status.PENDING).build();
        Booking booking9 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).user(user).charger(charger1).car(createTestCar(1L)).status(Booking.Status.PENDING).build();
        assertEquals(booking7, booking8);
        assertNotEquals(booking7, booking9);

        // Test with null charger
        Booking booking10 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).user(user).charger(null).car(createTestCar(1L)).status(Booking.Status.PENDING).build();
        Booking booking11 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).user(user).charger(null).car(createTestCar(1L)).status(Booking.Status.PENDING).build();
        Booking booking12 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).user(user).charger(charger1).car(createTestCar(1L)).status(Booking.Status.PENDING).build();
        assertEquals(booking10, booking11);
        assertNotEquals(booking10, booking12);

        // Test with null status
        Booking booking13 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).user(user).charger(charger1).car(createTestCar(1L)).status(null).build();
        Booking booking14 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).user(user).charger(charger2).car(createTestCar(1L)).status(null).build();
        Booking booking15 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).user(user).charger(charger1).car(createTestCar(1L)).status(Booking.Status.PENDING).build();
        assertEquals(booking13, booking14);
        assertNotEquals(booking13, booking15);
    }

    @Test
    void testHashCode_SameObjects() {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(1);
        Charger charger1 = Charger.builder().id(1L).build();
        Charger charger2 = Charger.builder().id(1L).build();
        User user = createTestUser(1L, "userA");

        Booking booking1 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).user(user).charger(charger1).car(createTestCar(1L)).status(Booking.Status.PENDING).build();
        Booking booking2 = Booking.builder().id(1L).startTime(startTime).endTime(endTime).user(user).charger(charger2).car(createTestCar(1L)).status(Booking.Status.PENDING).build();
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
        User user = createTestUser(1L, "userA");
        Booking booking = Booking.builder().id(1L).startTime(startTime).endTime(endTime).user(user).charger(charger).car(createTestCar(1L)).status(Booking.Status.PENDING).build();
        String toStringResult = booking.toString();
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("Booking"));
        assertTrue(toStringResult.contains("id=1"));
        assertTrue(toStringResult.contains("user="));
        assertTrue(toStringResult.contains("status=PENDING"));
    }
} 