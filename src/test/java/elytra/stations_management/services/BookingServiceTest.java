package elytra.stations_management.services;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import elytra.stations_management.models.Booking;
import elytra.stations_management.models.Charger;
import elytra.stations_management.repositories.BookingRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ChargerService chargerService;

    @InjectMocks
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
    void createBooking_ShouldCreateValidBooking() {
        when(bookingRepository.findOverlappingBookings(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking createdBooking = bookingService.createBooking(booking);

        assertNotNull(createdBooking);
        assertEquals(Booking.Status.PENDING, createdBooking.getStatus());
        verify(chargerService).updateChargerAvailability(charger.getId(), Charger.Status.BEING_USED);
    }

    @Test
    void createBooking_WhenChargerNotAvailable_ShouldThrowException() {
        charger.setStatus(Charger.Status.BEING_USED);

        assertThrows(RuntimeException.class, () -> bookingService.createBooking(booking));
    }

    @Test
    void createBooking_WhenOverlappingBookingExists_ShouldThrowException() {
        when(bookingRepository.findOverlappingBookings(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        assertThrows(RuntimeException.class, () -> bookingService.createBooking(booking));
    }

    @Test
    void createBooking_WithInvalidData_ShouldThrowException() {
        booking.setStartTime(null);
        assertThrows(RuntimeException.class, () -> bookingService.createBooking(booking));

        booking.setStartTime(startTime);
        booking.setEndTime(null);
        assertThrows(RuntimeException.class, () -> bookingService.createBooking(booking));

        booking.setEndTime(endTime);
        booking.setUserId(null);
        assertThrows(RuntimeException.class, () -> bookingService.createBooking(booking));

        booking.setUserId("user123");
        booking.setCharger(null);
        assertThrows(RuntimeException.class, () -> bookingService.createBooking(booking));
    }

    @Test
    void getAllBookings_ShouldReturnAllBookings() {
        List<Booking> bookings = Arrays.asList(booking);
        when(bookingRepository.findAll()).thenReturn(bookings);

        List<Booking> result = bookingService.getAllBookings();

        assertEquals(bookings, result);
        verify(bookingRepository).findAll();
    }

    @Test
    void getBookingById_ShouldReturnBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingById(1L);

        assertEquals(booking, result);
        verify(bookingRepository).findById(1L);
    }

    @Test
    void getBookingById_WhenNotFound_ShouldThrowException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookingService.getBookingById(1L));
    }

    @Test
    void getBookingsByUser_ShouldReturnUserBookings() {
        List<Booking> bookings = Arrays.asList(booking);
        when(bookingRepository.findByUserId("user123")).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByUser("user123");

        assertEquals(bookings, result);
        verify(bookingRepository).findByUserId("user123");
    }

    @Test
    void getBookingsByCharger_ShouldReturnChargerBookings() {
        List<Booking> bookings = Arrays.asList(booking);
        when(bookingRepository.findByChargerId(1L)).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByCharger(1L);

        assertEquals(bookings, result);
        verify(bookingRepository).findByChargerId(1L);
    }

    @Test
    void updateBookingStatus_ShouldUpdateStatus() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking updatedBooking = bookingService.updateBookingStatus(1L, Booking.Status.CONFIRMED);

        assertEquals(Booking.Status.CONFIRMED, updatedBooking.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void updateBookingStatus_WhenCompleted_ShouldMakeChargerAvailable() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        bookingService.updateBookingStatus(1L, Booking.Status.COMPLETED);

        verify(chargerService).updateChargerAvailability(charger.getId(), Charger.Status.AVAILABLE);
    }

    @Test
    void updateBookingStatus_WhenCancelled_ShouldMakeChargerAvailable() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        bookingService.updateBookingStatus(1L, Booking.Status.CANCELLED);

        verify(chargerService).updateChargerAvailability(charger.getId(), Charger.Status.AVAILABLE);
    }

    @Test
    void updateBookingStatus_WhenInvalidTransition_ShouldThrowException() {
        booking.setStatus(Booking.Status.COMPLETED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(RuntimeException.class, 
            () -> bookingService.updateBookingStatus(1L, Booking.Status.PENDING));
    }

    @Test
    void deleteBooking_ShouldDeleteBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.deleteBooking(1L);

        verify(bookingRepository).delete(booking);
    }

    @Test
    void deleteBooking_WhenActive_ShouldMakeChargerAvailable() {
        booking.setStatus(Booking.Status.CONFIRMED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.deleteBooking(1L);

        verify(chargerService).updateChargerAvailability(charger.getId(), Charger.Status.AVAILABLE);
    }

    @Test
    void deleteBooking_WhenNotFound_ShouldThrowException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookingService.deleteBooking(1L));
    }
} 