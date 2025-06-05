package elytra.stations_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import elytra.stations_management.models.Booking;
import elytra.stations_management.models.Charger;
import elytra.stations_management.models.User;
import elytra.stations_management.dto.BookingRequest;
import elytra.stations_management.services.BookingService;
import elytra.stations_management.exception.InvalidBookingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import elytra.stations_management.config.TestSecurityConfig;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingRequest bookingRequest;
    private Booking booking;
    private Charger charger;
    private User user;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.now().plusHours(1);
        endTime = startTime.plusHours(1);
        
        user = User.builder()
                .id(123L)
                .username("user123")
                .email("user123@test.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .userType(User.UserType.EV_DRIVER)
                .build();
        
        charger = Charger.builder()
                .id(1L)
                .type("Type 2")
                .power(50.0)
                .status(Charger.Status.AVAILABLE)
                .build();

        bookingRequest = BookingRequest.builder()
                .startTime(startTime)
                .endTime(endTime)
                .userId(123L)
                .chargerId(1L)
                .build();

        booking = Booking.builder()
                .id(1L)
                .startTime(startTime)
                .endTime(endTime)
                .user(user)
                .charger(charger)
                .status(Booking.Status.PENDING)
                .build();
    }

    @Test
    void createBooking_ShouldReturnCreatedBooking() throws Exception {
        when(bookingService.createBooking(any(BookingRequest.class))).thenReturn(booking);

        mockMvc.perform(post("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createBooking_WhenServiceThrowsException_ShouldReturnBadRequest() throws Exception {
        when(bookingService.createBooking(any(BookingRequest.class)))
                .thenThrow(new InvalidBookingException("Invalid booking"));

        mockMvc.perform(post("/api/v1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllBookings_ShouldReturnBookingsList() throws Exception {
        List<Booking> bookings = Arrays.asList(booking, booking);
        when(bookingService.getAllBookings()).thenReturn(bookings);

        mockMvc.perform(get("/api/v1/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void getBookingById_WhenExists_ShouldReturnBooking() throws Exception {
        when(bookingService.getBookingById(1L)).thenReturn(booking);

        mockMvc.perform(get("/api/v1/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getBookingById_WhenNotExists_ShouldReturn404() throws Exception {
        when(bookingService.getBookingById(999L))
                .thenThrow(new InvalidBookingException("Booking not found"));

        mockMvc.perform(get("/api/v1/bookings/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBookingsByUser_ShouldReturnUserBookings() throws Exception {
        List<Booking> userBookings = Arrays.asList(booking);
        when(bookingService.getBookingsByUser(123L)).thenReturn(userBookings);

        mockMvc.perform(get("/api/v1/bookings/user/123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].user.id").value(123));
    }

    @Test
    void getBookingsByCharger_ShouldReturnChargerBookings() throws Exception {
        List<Booking> chargerBookings = Arrays.asList(booking);
        when(bookingService.getBookingsByCharger(1L)).thenReturn(chargerBookings);

        mockMvc.perform(get("/api/v1/bookings/charger/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void updateBookingStatus_ShouldReturnUpdatedBooking() throws Exception {
        booking.setStatus(Booking.Status.CONFIRMED);
        when(bookingService.updateBookingStatus(eq(1L), eq(Booking.Status.CONFIRMED)))
                .thenReturn(booking);

        mockMvc.perform(put("/api/v1/bookings/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"CONFIRMED\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void updateBookingStatus_WhenServiceThrowsException_ShouldReturnBadRequest() throws Exception {
        when(bookingService.updateBookingStatus(eq(1L), any(Booking.Status.class)))
                .thenThrow(new InvalidBookingException("Invalid status transition"));

        mockMvc.perform(put("/api/v1/bookings/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"INVALID\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteBooking_WhenExists_ShouldReturn204() throws Exception {
        doNothing().when(bookingService).deleteBooking(1L);

        mockMvc.perform(delete("/api/v1/bookings/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteBooking_WhenNotExists_ShouldReturn404() throws Exception {
        doThrow(new InvalidBookingException("Booking not found"))
                .when(bookingService).deleteBooking(999L);

        mockMvc.perform(delete("/api/v1/bookings/999"))
                .andExpect(status().isNotFound());
    }
}