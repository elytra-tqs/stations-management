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
} 