package elytra.stations_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import elytra.stations_management.models.Charger;
import elytra.stations_management.service.ChargerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChargerController.class)
class ChargerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChargerService chargerService;

    private Charger charger;

    @BeforeEach
    void setUp() {
        charger = Charger.builder()
                .id(1L)
                .type("Type 2")
                .power(50.0)
                .status(Charger.Status.AVAILABLE)
                .build();
    }

    @Test
    void getChargerAvailability_ShouldReturnStatus() throws Exception {
        when(chargerService.getChargerAvailability(1L)).thenReturn(Charger.Status.AVAILABLE);

        mockMvc.perform(get("/api/chargers/1/availability"))
                .andExpect(status().isOk())
                .andExpect(content().string("\"AVAILABLE\""));
    }

    @Test
    void updateChargerAvailability_ShouldUpdateStatus() throws Exception {
        when(chargerService.updateChargerAvailability(eq(1L), any(Charger.Status.class)))
                .thenReturn(charger);

        mockMvc.perform(put("/api/chargers/1/availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"BEING_USED\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void updateChargerAvailability_WhenInvalidTransition_ShouldReturnBadRequest() throws Exception {
        when(chargerService.updateChargerAvailability(eq(1L), any(Charger.Status.class)))
                .thenThrow(new RuntimeException("Invalid transition"));

        mockMvc.perform(put("/api/chargers/1/availability")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"BEING_USED\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getChargersByAvailability_ShouldReturnList() throws Exception {
        List<Charger> chargers = Arrays.asList(charger);
        when(chargerService.getChargersByAvailability(Charger.Status.AVAILABLE))
                .thenReturn(chargers);

        mockMvc.perform(get("/api/chargers/availability/AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"));
    }

    @Test
    void getAvailableChargersAtStation_ShouldReturnList() throws Exception {
        List<Charger> chargers = Arrays.asList(charger);
        when(chargerService.getAvailableChargersAtStation(1L)).thenReturn(chargers);

        mockMvc.perform(get("/api/chargers/station/1/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"));
    }
}