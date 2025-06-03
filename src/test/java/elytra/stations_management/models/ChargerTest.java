package elytra.stations_management.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChargerTest {

    @Test
    void testNoArgsConstructor() {
        Charger charger = new Charger();
        assertNotNull(charger);
    }

    @Test
    void testBuilder() {
        Station station = new Station();
        Charger charger = Charger.builder()
                .id(2L)
                .type("CCS")
                .power(150.0)
                .status(Charger.Status.BEING_USED)
                .station(station)
                .build();
        assertEquals(2L, charger.getId());
        assertEquals("CCS", charger.getType());
        assertEquals(150.0, charger.getPower());
        assertEquals(Charger.Status.BEING_USED, charger.getStatus());
        assertEquals(station, charger.getStation());
    }

    @Test
    void testSettersAndGetters() {
        Charger charger = new Charger();
        Station station = new Station();

        charger.setId(3L);
        charger.setType("CHAdeMO");
        charger.setPower(100.0);
        charger.setStatus(Charger.Status.UNDER_MAINTENANCE);
        charger.setStation(station);

        assertEquals(3L, charger.getId());
        assertEquals("CHAdeMO", charger.getType());
        assertEquals(100.0, charger.getPower());
        assertEquals(Charger.Status.UNDER_MAINTENANCE, charger.getStatus());
        assertEquals(station, charger.getStation());
    }

    @Test
    void testEnumValues() {
        assertNotNull(Charger.Status.AVAILABLE);
        assertNotNull(Charger.Status.BEING_USED);
        assertNotNull(Charger.Status.UNDER_MAINTENANCE);
        assertNotNull(Charger.Status.OUT_OF_SERVICE);
    }

    @Test
    void testEqualsAndHashCode() {
        Station station1 = new Station();
        station1.setId(1L);
        Station station2 = new Station();
        station2.setId(1L);
        Station station3 = new Station();
        station3.setId(2L);

        Charger charger1 = Charger.builder().id(1L).type("TypeA").power(10.0).status(Charger.Status.AVAILABLE).station(station1).build();
        Charger charger2 = Charger.builder().id(1L).type("TypeA").power(10.0).status(Charger.Status.AVAILABLE).station(station2).build();
        Charger charger3 = Charger.builder().id(2L).type("TypeB").power(20.0).status(Charger.Status.BEING_USED).station(station3).build();

        // Test equality
        assertTrue(charger1.equals(charger1)); // Reflexive
        assertTrue(charger1.equals(charger2)); // Symmetric
        assertTrue(charger2.equals(charger1)); // Symmetric
        assertFalse(charger1.equals(charger3)); // Different object
        assertFalse(charger1.equals(null)); // Vs null
        assertFalse(charger1.equals("string")); // Vs different type

        // Test hashCode
        assertEquals(charger1.hashCode(), charger2.hashCode()); // Equal objects have same hash code
    }

    @Test
    void testToString() {
        Station station = new Station();
        station.setId(1L);
        Charger charger = Charger.builder().id(1L).type("TypeA").power(10.0).status(Charger.Status.AVAILABLE).station(station).build();
        String toStringResult = charger.toString();
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("Charger"));
        assertTrue(toStringResult.contains("id=1"));
        assertTrue(toStringResult.contains("type=TypeA"));
        assertTrue(toStringResult.contains("status=AVAILABLE"));
    }

    @Test
    void testCanEqual() {
        Charger charger1 = Charger.builder().id(1L).build();
        Charger charger2 = Charger.builder().id(2L).build();
        assertTrue(charger1.canEqual(charger2));
    }
} 