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
} 