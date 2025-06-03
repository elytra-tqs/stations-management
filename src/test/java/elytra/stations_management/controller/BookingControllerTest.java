package elytra.stations_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import elytra.stations_management.models.Booking;
import elytra.stations_management.models.Charger;
import elytra.stations_management.services.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private Booking booking;
    private Charger charger;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.now().plusHours(1);
        endTime = startTime.plusHours(1);
        charger = Charger.builder()
                .id(1L)
                .type("Type 2")
                .power(50.0)
                .status(Charger.Status.AVAILABLE)
                .build();

        booking = Booking.builder()
                .id(1L)
                .startTime(startTime)
                .endTime(endTime)
                .userId("user123")
                .charger(charger)
                .status(Booking.Status.PENDING)
                .build();
    }

    @Test
    void createBooking_ShouldReturnCreatedBooking() throws Exception {
        when(bookingService.createBooking(any(Booking.class))).thenReturn(booking);

        mockMvc.perform(post("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createBooking_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        booking.setStartTime(null);

        when(bookingService.createBooking(any(Booking.class)))
                .thenThrow(new RuntimeException("Start time is required"));


        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllBookings_ShouldReturnAllBookings() throws Exception {
        List<Booking> bookings = Arrays.asList(booking);
        when(bookingService.getAllBookings()).thenReturn(bookings);

        mockMvc.perform(get("/api/v1/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void getBookingById_ShouldReturnBooking() throws Exception {
        when(bookingService.getBookingById(1L)).thenReturn(booking);

        mockMvc.perform(get("/api/v1/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getBookingById_WhenNotFound_ShouldReturnNotFound() throws Exception {
        when(bookingService.getBookingById(1L)).thenThrow(new RuntimeException("Booking not found"));

        mockMvc.perform(get("/api/v1/bookings/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookingsByUser_ShouldReturnUserBookings() throws Exception {
        List<Booking> bookings = Arrays.asList(booking);
        when(bookingService.getBookingsByUser("user123")).thenReturn(bookings);

        mockMvc.perform(get("/api/v1/bookings/user/user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userId").value("user123"));
    }

    @Test
    void getBookingsByCharger_ShouldReturnChargerBookings() throws Exception {
        List<Booking> bookings = Arrays.asList(booking);
        when(bookingService.getBookingsByCharger(1L)).thenReturn(bookings);

        mockMvc.perform(get("/api/v1/bookings/charger/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void updateBookingStatus_ShouldUpdateStatus() throws Exception {
        when(bookingService.updateBookingStatus(eq(1L), any(Booking.Status.class)))
                .thenReturn(booking);

        mockMvc.perform(put("/api/v1/bookings/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"CONFIRMED\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void updateBookingStatus_WhenInvalidStatus_ShouldReturnBadRequest() throws Exception {
        when(bookingService.updateBookingStatus(eq(1L), any(Booking.Status.class)))
                .thenThrow(new RuntimeException("Invalid status transition"));

        mockMvc.perform(put("/api/v1/bookings/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"INVALID_STATUS\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBookingStatus_WhenServiceThrowsException_ShouldReturnBadRequest() throws Exception {
        when(bookingService.updateBookingStatus(eq(1L), any(Booking.Status.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(put("/api/v1/bookings/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"CONFIRMED\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteBooking_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/bookings/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBooking_WhenNotFound_ShouldReturnNotFound() throws Exception {
        doThrow(new RuntimeException("Booking not found"))
                .when(bookingService).deleteBooking(1L);

        mockMvc.perform(delete("/api/v1/bookings/1"))
                .andExpect(status().isNotFound());
    }
} 