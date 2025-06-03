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
    void testEquals_SameObject() {
        Station station1 = Station.builder().id(1L).name("Station A").build();
        assertTrue(station1.equals(station1));
    }

    @Test
    void testEquals_DifferentObjectsSameValues() {
        Station station1 = Station.builder().id(1L).name("Station A").build();
        Station station2 = Station.builder().id(1L).name("Station A").build();
        assertTrue(station1.equals(station2));
    }

    @Test
    void testEquals_DifferentObjectsDifferentIds() {
        Station station1 = Station.builder().id(1L).name("Station A").build();
        Station station2 = Station.builder().id(2L).name("Station A").build();
        assertFalse(station1.equals(station2));
    }

    @Test
    void testEquals_DifferentObjectsDifferentNames() {
        Station station1 = Station.builder().id(1L).name("Station A").build();
        Station station2 = Station.builder().id(1L).name("Station B").build();
        assertFalse(station1.equals(station2));
    }

    @Test
    void testEquals_VsNull() {
        Station station = Station.builder().build();
        assertFalse(station.equals(null));
    }

    @Test
    void testEquals_VsDifferentType() {
        Station station = Station.builder().build();
        assertFalse(station.equals("a string"));
    }

    @Test
    void testEquals_WithNullFields() {
        Station station1 = Station.builder().id(1L).name(null).address("Address A").latitude(10.0).longitude(20.0).build();
        Station station2 = Station.builder().id(1L).name(null).address("Address A").latitude(10.0).longitude(20.0).build();
        Station station3 = Station.builder().id(1L).name("Station A").address("Address A").latitude(10.0).longitude(20.0).build();

        assertTrue(station1.equals(station2));
        assertFalse(station1.equals(station3));
    }

    @Test
    void testHashCode_SameObjects() {
        Station station1 = Station.builder().id(1L).name("Station A").build();
        Station station2 = Station.builder().id(1L).name("Station A").build();
        assertEquals(station1.hashCode(), station2.hashCode());
    }

    @Test
    void testCanEqual_SameType() {
        Station station1 = Station.builder().id(1L).build();
        Station station2 = Station.builder().id(2L).build();
        assertTrue(station1.canEqual(station2));
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
} 