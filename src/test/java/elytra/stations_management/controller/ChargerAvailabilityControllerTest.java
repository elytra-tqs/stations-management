package elytra.stations_management.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import elytra.stations_management.models.Charger;
import elytra.stations_management.service.ChargerService;

@WebMvcTest(ChargerAvailabilityController.class)
class ChargerAvailabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChargerService chargerService;

    private Charger charger;

    @BeforeEach
    void setUp() {
        charger = Charger.builder()
                .id(1L)
                .type("Type 2")
                .power(22.0)
                .availabilityStatus(Charger.AvailabilityStatus.AVAILABLE)
                .build();
    }

    @Test
    void whenGettingChargerAvailability_thenReturnStatus() throws Exception {
        when(chargerService.getChargerAvailability(1L))
                .thenReturn(Charger.AvailabilityStatus.AVAILABLE);

        mockMvc.perform(get("/api/chargers/1/availability"))
                .andExpect(status().isOk())
                .andExpect(content().string("AVAILABLE"));
    }

    @Test
    void whenUpdatingChargerAvailability_thenReturnUpdatedCharger() throws Exception {
        when(chargerService.updateChargerAvailability(1L, Charger.AvailabilityStatus.IN_USE))
                .thenReturn(charger);

        mockMvc.perform(put("/api/chargers/1/availability")
                .param("status", "IN_USE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availabilityStatus").value("AVAILABLE"));
    }

    @Test
    void whenUpdatingChargerAvailabilityWithInvalidTransition_thenReturnBadRequest() throws Exception {
        when(chargerService.updateChargerAvailability(1L, Charger.AvailabilityStatus.IN_USE))
                .thenThrow(new RuntimeException("Invalid transition"));

        mockMvc.perform(put("/api/chargers/1/availability")
                .param("status", "IN_USE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGettingChargersByAvailability_thenReturnList() throws Exception {
        List<Charger> chargers = Arrays.asList(charger);
        when(chargerService.getChargersByAvailability(Charger.AvailabilityStatus.AVAILABLE))
                .thenReturn(chargers);

        mockMvc.perform(get("/api/chargers/availability/AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].availabilityStatus").value("AVAILABLE"));
    }

    @Test
    void whenGettingAvailableChargersAtStation_thenReturnList() throws Exception {
        List<Charger> chargers = Arrays.asList(charger);
        when(chargerService.getAvailableChargersAtStation(1L))
                .thenReturn(chargers);

        mockMvc.perform(get("/api/chargers/station/1/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].availabilityStatus").value("AVAILABLE"));
    }
}