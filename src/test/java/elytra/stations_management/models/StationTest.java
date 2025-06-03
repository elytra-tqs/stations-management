package elytra.stations_management.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StationTest {

    @Test
    void testNoArgsConstructor() {
        Station station = new Station();
        assertNotNull(station);
    }

    @Test
    void testBuilder() {
        Station station = Station.builder()
                .id(2L)
                .name("Another Station")
                .address("456 Another Ave")
                .latitude(34.0522)
                .longitude(-118.2437)
                .build();
        assertEquals(2L, station.getId());
        assertEquals("Another Station", station.getName());
        assertEquals("456 Another Ave", station.getAddress());
        assertEquals(34.0522, station.getLatitude());
        assertEquals(-118.2437, station.getLongitude());
    }

    @Test
    void testSettersAndGetters() {
        Station station = new Station();
        station.setId(3L);
        station.setName("Setter Test");
        station.setAddress("789 Setter Ln");
        station.setLatitude(41.8781);
        station.setLongitude(-87.6298);

        assertEquals(3L, station.getId());
        assertEquals("Setter Test", station.getName());
        assertEquals("789 Setter Ln", station.getAddress());
        assertEquals(41.8781, station.getLatitude());
        assertEquals(-87.6298, station.getLongitude());
    }

    @Test
    void testEqualsAndHashCode() {
        Station station1 = Station.builder().id(1L).name("Station A").address("Address A").latitude(10.0).longitude(20.0).build();
        Station station2 = Station.builder().id(1L).name("Station A").address("Address A").latitude(10.0).longitude(20.0).build();
        Station station3 = Station.builder().id(2L).name("Station B").address("Address B").latitude(30.0).longitude(40.0).build();

        // Test equality
        assertTrue(station1.equals(station1)); // Reflexive
        assertTrue(station1.equals(station2)); // Symmetric
        assertTrue(station2.equals(station1)); // Symmetric
        assertFalse(station1.equals(station3)); // Different object
        assertFalse(station1.equals(null)); // Vs null
        assertFalse(station1.equals("string")); // Vs different type

        // Test hashCode
        assertEquals(station1.hashCode(), station2.hashCode()); // Equal objects have same hash code
    }

    @Test
    void testToString() {
        Station station = Station.builder().id(1L).name("Station A").build();
        String toStringResult = station.toString();
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("Station"));
        assertTrue(toStringResult.contains("id=1"));
        assertTrue(toStringResult.contains("name=Station A"));
    }

    @Test
    void testCanEqual() {
        Station station1 = Station.builder().id(1L).build();
        Station station2 = Station.builder().id(2L).build();
        assertTrue(station1.canEqual(station2));
    }
} 