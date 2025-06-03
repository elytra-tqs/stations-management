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
    void testEquals_SameObject() {
        Station station = Station.builder().id(1L).build();
        Charger charger1 = Charger.builder().id(1L).type("TypeA").power(10.0).status(Charger.Status.AVAILABLE).station(station).build();
        assertEquals(charger1, charger1);
    }

    @Test
    void testEquals_DifferentObjectsSameValues() {
        Station station1 = Station.builder().id(1L).build();
        Station station2 = Station.builder().id(1L).build();

        Charger charger1 = Charger.builder().id(1L).type("TypeA").power(10.0).status(Charger.Status.AVAILABLE).station(station1).build();
        Charger charger2 = Charger.builder().id(1L).type("TypeA").power(10.0).status(Charger.Status.AVAILABLE).station(station2).build();

        assertEquals(charger1, charger2);
    }

    @Test
    void testEquals_DifferentObjectsDifferentIds() {
        Station station1 = Station.builder().id(1L).build();
        Station station2 = Station.builder().id(1L).build();

        Charger charger1 = Charger.builder().id(1L).type("TypeA").power(10.0).status(Charger.Status.AVAILABLE).station(station1).build();
        Charger charger2 = Charger.builder().id(2L).type("TypeA").power(10.0).status(Charger.Status.AVAILABLE).station(station2).build();

        assertNotEquals(charger1, charger2);
    }

    @Test
    void testEquals_DifferentObjectsDifferentTypes() {
        Station station1 = Station.builder().id(1L).build();
        Station station2 = Station.builder().id(1L).build();

        Charger charger1 = Charger.builder().id(1L).type("TypeA").power(10.0).status(Charger.Status.AVAILABLE).station(station1).build();
        Charger charger2 = Charger.builder().id(1L).type("TypeB").power(10.0).status(Charger.Status.AVAILABLE).station(station2).build();

        assertNotEquals(charger1, charger2);
    }

    @Test
    void testEquals_DifferentObjectsDifferentPower() {
        Station station1 = Station.builder().id(1L).build();
        Station station2 = Station.builder().id(1L).build();

        Charger charger1 = Charger.builder().id(1L).type("TypeA").power(10.0).status(Charger.Status.AVAILABLE).station(station1).build();
        Charger charger2 = Charger.builder().id(1L).type("TypeA").power(20.0).status(Charger.Status.AVAILABLE).station(station2).build();

        assertNotEquals(charger1, charger2);
    }

    @Test
    void testEquals_DifferentObjectsDifferentStatus() {
        Station station1 = Station.builder().id(1L).build();
        Station station2 = Station.builder().id(1L).build();

        Charger charger1 = Charger.builder().id(1L).type("TypeA").power(10.0).status(Charger.Status.AVAILABLE).station(station1).build();
        Charger charger2 = Charger.builder().id(1L).type("TypeA").power(10.0).status(Charger.Status.BEING_USED).station(station2).build();

        assertNotEquals(charger1, charger2);
    }

    @Test
    void testEquals_DifferentObjectsDifferentStations() {
        Station station1 = Station.builder().id(1L).build();
        Station station2 = Station.builder().id(2L).build();

        Charger charger1 = Charger.builder().id(1L).type("TypeA").power(10.0).status(Charger.Status.AVAILABLE).station(station1).build();
        Charger charger2 = Charger.builder().id(1L).type("TypeA").power(10.0).status(Charger.Status.AVAILABLE).station(station2).build();

        assertNotEquals(charger1, charger2);
    }

    @Test
    void testEquals_VsNull() {
        Charger charger = Charger.builder().build();
        assertNotEquals(null, charger);
    }

    @Test
    void testEquals_VsDifferentType() {
        Charger charger = Charger.builder().build();
        assertNotEquals("a string", charger);
    }

    @Test
    void testEquals_WithNullFields() {
        Station station1 = Station.builder().id(1L).build();
        Station station2 = Station.builder().id(1L).build();
        
        // Test with null type
        Charger charger1 = Charger.builder().id(1L).type(null).power(10.0).status(Charger.Status.AVAILABLE).station(station1).build();
        Charger charger2 = Charger.builder().id(1L).type(null).power(10.0).status(Charger.Status.AVAILABLE).station(station2).build();
        Charger charger3 = Charger.builder().id(1L).type("TypeA").power(10.0).status(Charger.Status.AVAILABLE).station(station1).build();
        assertEquals(charger1, charger2);
        assertNotEquals(charger1, charger3);

        // Test with null status
        Charger charger4 = Charger.builder().id(1L).type("TypeA").power(10.0).status(null).station(station1).build();
        Charger charger5 = Charger.builder().id(1L).type("TypeA").power(10.0).status(null).station(station2).build();
         Charger charger6 = Charger.builder().id(1L).type("TypeA").power(10.0).status(Charger.Status.AVAILABLE).station(station1).build();
        assertEquals(charger4, charger5);
        assertNotEquals(charger4, charger6);

         // Test with null station
         Charger charger7 = Charger.builder().id(1L).type("TypeA").power(10.0).status(Charger.Status.AVAILABLE).station(null).build();
        Charger charger8 = Charger.builder().id(1L).type("TypeA").power(10.0).status(Charger.Status.AVAILABLE).station(null).build();
         Charger charger9 = Charger.builder().id(1L).type("TypeA").power(10.0).status(Charger.Status.AVAILABLE).station(station1).build();
        assertEquals(charger7, charger8);
        assertNotEquals(charger7, charger9);
    }

    @Test
    void testHashCode_SameObjects() {
        Station station1 = Station.builder().id(1L).build();
        Station station2 = Station.builder().id(1L).build();

        Charger charger1 = Charger.builder().id(1L).type("TypeA").power(10.0).status(Charger.Status.AVAILABLE).station(station1).build();
        Charger charger2 = Charger.builder().id(1L).type("TypeA").power(10.0).status(Charger.Status.AVAILABLE).station(station2).build();

        assertEquals(charger1.hashCode(), charger2.hashCode());
    }

    @Test
    void testCanEqual_SameType() {
        Charger charger1 = Charger.builder().id(1L).build();
        Charger charger2 = Charger.builder().id(2L).build();
        assertTrue(charger1.canEqual(charger2));
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
} 