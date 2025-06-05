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
import elytra.stations_management.models.User;
import elytra.stations_management.repositories.BookingRepository;
import elytra.stations_management.repositories.UserRepository;
import elytra.stations_management.dto.BookingRequest;
import elytra.stations_management.exception.InvalidBookingException;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ChargerService chargerService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
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

        // Manually create BookingService with self-injection
        bookingService = new BookingService(bookingRepository, chargerService, userRepository, bookingService);
    }

    @Test
    void createBooking_ShouldCreateValidBooking() {
        when(userRepository.findById(123L)).thenReturn(Optional.of(user));
        when(chargerService.getChargerById(1L)).thenReturn(charger);
        when(bookingRepository.findOverlappingBookings(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking createdBooking = bookingService.createBooking(bookingRequest);

        assertNotNull(createdBooking);
        assertEquals(Booking.Status.PENDING, createdBooking.getStatus());
        assertEquals(user, createdBooking.getUser());
        verify(chargerService).updateChargerAvailability(charger.getId(), Charger.Status.BEING_USED);
    }

    @Test
    void createBooking_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(123L)).thenReturn(Optional.empty());

        InvalidBookingException exception = assertThrows(InvalidBookingException.class, 
            () -> bookingService.createBooking(bookingRequest));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void createBooking_WhenChargerNotAvailable_ShouldThrowException() {
        when(userRepository.findById(123L)).thenReturn(Optional.of(user));
        charger.setStatus(Charger.Status.BEING_USED);
        when(chargerService.getChargerById(1L)).thenReturn(charger);

        InvalidBookingException exception = assertThrows(InvalidBookingException.class, 
            () -> bookingService.createBooking(bookingRequest));
        assertEquals("Charger is not available for booking", exception.getMessage());
    }

    @Test
    void createBooking_WhenOverlappingBookingExists_ShouldThrowException() {
        when(userRepository.findById(123L)).thenReturn(Optional.of(user));
        when(chargerService.getChargerById(1L)).thenReturn(charger);
        when(bookingRepository.findOverlappingBookings(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        InvalidBookingException exception = assertThrows(InvalidBookingException.class, 
            () -> bookingService.createBooking(bookingRequest));
        assertEquals("Charger is already booked for this time period", exception.getMessage());
    }

    @Test
    void createBooking_WithInvalidData_ShouldThrowException() {
        bookingRequest.setStartTime(null);
        InvalidBookingException exception = assertThrows(InvalidBookingException.class, 
            () -> bookingService.createBooking(bookingRequest));
        assertEquals("Start time is required", exception.getMessage());

        bookingRequest.setStartTime(startTime);
        bookingRequest.setEndTime(null);
        exception = assertThrows(InvalidBookingException.class, 
            () -> bookingService.createBooking(bookingRequest));
        assertEquals("End time must be after start time", exception.getMessage());

        bookingRequest.setEndTime(startTime.minusHours(1));
        exception = assertThrows(InvalidBookingException.class, 
            () -> bookingService.createBooking(bookingRequest));
        assertEquals("End time must be after start time", exception.getMessage());

        bookingRequest.setEndTime(endTime);
        bookingRequest.setUserId(null);
        exception = assertThrows(InvalidBookingException.class, 
            () -> bookingService.createBooking(bookingRequest));
        assertEquals("User ID is required", exception.getMessage());

        bookingRequest.setUserId(123L);
        bookingRequest.setChargerId(null);
        exception = assertThrows(InvalidBookingException.class, 
            () -> bookingService.createBooking(bookingRequest));
        assertEquals("Charger ID is required", exception.getMessage());

        bookingRequest.setChargerId(1L);
        bookingRequest.setStartTime(LocalDateTime.now().minusHours(1));
        exception = assertThrows(InvalidBookingException.class, 
            () -> bookingService.createBooking(bookingRequest));
        assertEquals("Cannot create booking in the past", exception.getMessage());
    }

    @Test
    void getAllBookings_ShouldReturnAllBookings() {
        List<Booking> bookings = Arrays.asList(booking, booking);
        when(bookingRepository.findAll()).thenReturn(bookings);

        List<Booking> result = bookingService.getAllBookings();

        assertEquals(2, result.size());
        verify(bookingRepository).findAll();
    }

    @Test
    void getBookingById_WhenExists_ShouldReturnBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Booking result = bookingService.getBookingById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getBookingById_WhenNotExists_ShouldThrowException() {
        when(bookingRepository.findById(999L)).thenReturn(Optional.empty());

        InvalidBookingException exception = assertThrows(InvalidBookingException.class, 
            () -> bookingService.getBookingById(999L));
        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void getBookingsByUser_ShouldReturnUserBookings() {
        List<Booking> userBookings = Arrays.asList(booking);
        when(bookingRepository.findByUserId(123L)).thenReturn(userBookings);

        List<Booking> result = bookingService.getBookingsByUser(123L);

        assertEquals(1, result.size());
        verify(bookingRepository).findByUserId(123L);
    }

    @Test
    void getBookingsByCharger_ShouldReturnChargerBookings() {
        List<Booking> chargerBookings = Arrays.asList(booking);
        when(bookingRepository.findByChargerId(1L)).thenReturn(chargerBookings);

        List<Booking> result = bookingService.getBookingsByCharger(1L);

        assertEquals(1, result.size());
        verify(bookingRepository).findByChargerId(1L);
    }

    @Test
    void updateBookingStatus_ToCompleted_ShouldUpdateStatusAndReleaseCharger() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        booking.setStatus(Booking.Status.CONFIRMED);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking saved = invocation.getArgument(0);
            saved.setStatus(Booking.Status.COMPLETED);
            return saved;
        });

        Booking result = bookingService.updateBookingStatus(1L, Booking.Status.COMPLETED);

        assertEquals(Booking.Status.COMPLETED, result.getStatus());
        verify(chargerService).updateChargerAvailability(charger.getId(), Charger.Status.AVAILABLE);
    }

    @Test
    void updateBookingStatus_ToCancelled_ShouldUpdateStatusAndReleaseCharger() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        booking.setStatus(Booking.Status.CONFIRMED);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking saved = invocation.getArgument(0);
            saved.setStatus(Booking.Status.CANCELLED);
            return saved;
        });

        Booking result = bookingService.updateBookingStatus(1L, Booking.Status.CANCELLED);

        assertEquals(Booking.Status.CANCELLED, result.getStatus());
        verify(chargerService).updateChargerAvailability(charger.getId(), Charger.Status.AVAILABLE);
    }

    @Test
    void updateBookingStatus_InvalidTransition_ShouldThrowException() {
        booking.setStatus(Booking.Status.COMPLETED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        InvalidBookingException exception = assertThrows(InvalidBookingException.class, 
            () -> bookingService.updateBookingStatus(1L, Booking.Status.PENDING));
        assertEquals("Cannot change status of a completed booking", exception.getMessage());
    }

    @Test
    void deleteBooking_WhenConfirmed_ShouldDeleteAndReleaseCharger() {
        booking.setStatus(Booking.Status.CONFIRMED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.deleteBooking(1L);

        verify(bookingRepository).delete(booking);
        verify(chargerService).updateChargerAvailability(charger.getId(), Charger.Status.AVAILABLE);
    }

    @Test
    void deleteBooking_WhenNotConfirmed_ShouldDeleteWithoutReleasingCharger() {
        booking.setStatus(Booking.Status.PENDING);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.deleteBooking(1L);

        verify(bookingRepository).delete(booking);
        verify(chargerService, never()).updateChargerAvailability(anyLong(), any());
    }
}