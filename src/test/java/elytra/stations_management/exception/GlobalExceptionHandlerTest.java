package elytra.stations_management.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleRuntimeException_WithAlreadyExists() {
        RuntimeException exception = new RuntimeException("Username already exists");
        
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleRuntimeException(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("error", "Username already exists");
    }

    @Test
    void handleRuntimeException_WithGenericError() {
        RuntimeException exception = new RuntimeException("Generic runtime error");
        
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleRuntimeException(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("error", "Generic runtime error");
    }

    @Test
    void handleDataIntegrityViolation_WithUsername() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Constraint violation: USERNAME unique constraint");
        
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleDataIntegrityViolation(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("error", "Username already exists");
    }

    @Test
    void handleDataIntegrityViolation_WithEmail() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Constraint violation: EMAIL unique constraint");
        
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleDataIntegrityViolation(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("error", "Email already exists");
    }

    @Test
    void handleDataIntegrityViolation_WithGeneric() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Generic constraint violation");
        
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleDataIntegrityViolation(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("error", "Data integrity violation");
    }

    @Test
    void handleUsernameNotFound() {
        UsernameNotFoundException exception = new UsernameNotFoundException("User not found");
        
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleUsernameNotFound(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).containsEntry("error", "User not found");
    }

    @Test
    void handleBadCredentials() {
        BadCredentialsException exception = new BadCredentialsException("Bad credentials");
        
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleBadCredentials(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).containsEntry("error", "Invalid username or password");
    }

    @Test
    void handleAuthenticationException() {
        AuthenticationException exception = new AuthenticationException("Auth failed") {};
        
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleAuthenticationException(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).containsEntry("error", "Authentication failed");
    }

    @Test
    void handleHttpMessageNotReadable() {
        Exception cause = new Exception("Invalid JSON format");
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
        when(exception.getMostSpecificCause()).thenReturn(cause);
        
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleHttpMessageNotReadable(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "Invalid request body: Invalid JSON format");
    }

    @Test
    void handleTypeMismatch() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn("id");
        when(exception.getRequiredType()).thenReturn((Class) Long.class);
        
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleTypeMismatch(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "Invalid parameter type: id should be of type Long");
    }

    @Test
    void handleInvalidBookingException() {
        InvalidBookingException exception = new InvalidBookingException("Invalid booking");
        
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleInvalidBooking(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "Invalid booking");
    }

    @Test
    void handleInvalidStatusTransitionException() {
        InvalidStatusTransitionException exception = new InvalidStatusTransitionException("Invalid status transition");
        
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleInvalidStatusTransition(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "Invalid status transition");
    }

    @Test
    void handleRuntimeException_WithNullMessage() {
        RuntimeException exception = new RuntimeException();
        
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleRuntimeException(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("error", null);
    }

    @Test
    void handleDataIntegrityViolation_WithNullMessage() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException(null);
        
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleDataIntegrityViolation(exception);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).containsEntry("error", "Data integrity violation");
    }
}