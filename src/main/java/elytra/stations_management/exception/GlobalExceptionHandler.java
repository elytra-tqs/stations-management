package elytra.stations_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_KEY = "error";

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR_KEY, e.getMessage());

        if (e.getMessage() != null && (e.getMessage().contains("already exists"))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        Map<String, String> error = new HashMap<>();

        if (e.getMessage() != null && e.getMessage().contains("USERNAME")) {
            error.put(ERROR_KEY, "Username already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } else if (e.getMessage() != null && e.getMessage().contains("EMAIL")) {
            error.put(ERROR_KEY, "Email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        error.put(ERROR_KEY, "Data integrity violation");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUsernameNotFound(UsernameNotFoundException e) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR_KEY, e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException e) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR_KEY, "Invalid username or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException e) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR_KEY, "Authentication failed");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException e) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR_KEY, "Access denied");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR_KEY, "Invalid request body: " + e.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR_KEY, "Invalid parameter type: " + e.getName() + " should be of type " + Objects.requireNonNull(e.getRequiredType()).getSimpleName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidBookingException.class)
    public ResponseEntity<Map<String, String>> handleInvalidBooking(InvalidBookingException e) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR_KEY, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<Map<String, String>> handleInvalidStatusTransition(InvalidStatusTransitionException e) {
        Map<String, String> error = new HashMap<>();
        error.put(ERROR_KEY, e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}