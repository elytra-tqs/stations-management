package elytra.stations_management.controller;

import elytra.stations_management.dto.AdminRegistrationRequest;
import elytra.stations_management.dto.DriverRegistrationRequest;
import elytra.stations_management.dto.OperatorRegistrationRequest;
import elytra.stations_management.models.*;
import elytra.stations_management.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and user registration APIs")
public class AuthController {

    private static final String USERNAME = "username";
    private static final String USER_TYPE = "userType";
    private static final String USER_ID = "userId";
    private static final String ERROR = "error";

    private final UserService userService;
    private final EVDriverService evDriverService;
    private final StationOperatorService stationOperatorService;
    private final AdminService adminService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(authRequest.getUsername());
                User user = userService.getUserByUsername(authRequest.getUsername());

                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put(USERNAME, user.getUsername());
                response.put(USER_TYPE, user.getUserType());
                response.put(USER_ID, user.getId());

                // Add role-specific information
                switch (user.getUserType()) {
                    case EV_DRIVER:
                        addDriverInfo(user, response);
                        break;
                    case STATION_OPERATOR:
                        addOperatorInfo(user, response);
                        break;
                    case ADMIN:
                        addAdminInfo(user, response);
                        break;
                }

                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid credentials");
        }
        throw new BadCredentialsException("Invalid credentials");
    }

    private void addDriverInfo(User user, Map<String, Object> response) {
        try {
            EVDriver driver = evDriverService.getDriverByUserId(user.getId());
            response.put("driverId", driver.getId());
        } catch (Exception e) {
            // Driver might not be fully set up yet
        }
    }

    private void addOperatorInfo(User user, Map<String, Object> response) {
        try {
            StationOperator operator = stationOperatorService.getStationOperatorByUserId(user.getId());
            response.put("operatorId", operator.getId());
            if (operator.getStation() != null) {
                response.put("stationId", operator.getStation().getId());
                response.put("stationName", operator.getStation().getName());
            }
        } catch (Exception e) {
            // Operator might not be fully set up yet
        }
    }

    private void addAdminInfo(User user, Map<String, Object> response) {
        try {
            Admin admin = adminService.getAdminByUserId(user.getId());
            response.put("adminId", admin.getId());
        } catch (Exception e) {
            // Admin might not be fully set up yet
        }
    }

    @PostMapping("/register/driver")
    @Operation(summary = "Register a new EV driver")
    public ResponseEntity<Map<String, Object>> registerDriver(@RequestBody DriverRegistrationRequest request) {
        try {
            // Set user type
            request.getUser().setUserType(User.UserType.EV_DRIVER);

            // Register driver
            EVDriver driver = evDriverService.registerDriver(request.getDriver(), request.getUser());

            // Generate token
            String token = jwtService.generateToken(request.getUser().getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put(USERNAME, driver.getUser().getUsername());
            response.put(USER_TYPE, User.UserType.EV_DRIVER);
            response.put(USER_ID, driver.getUser().getId());
            response.put("driverId", driver.getId());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(ERROR, e.getMessage()));
        }
    }

    @PostMapping("/register/operator")
    @Operation(summary = "Register a new station operator (Public)")
    public ResponseEntity<Map<String, Object>> registerOperator(@RequestBody OperatorRegistrationRequest request) {
        try {
            // Set user type
            request.getUser().setUserType(User.UserType.STATION_OPERATOR);
            
            StationOperator operator = stationOperatorService.registerStationOperator(
                    request.getOperator(),
                    request.getUser(),
                    request.getStationId()
            );

            // Generate token for auto-login
            String token = jwtService.generateToken(request.getUser().getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "Station operator registered successfully");
            response.put(USERNAME, operator.getUser().getUsername());
            response.put(USER_TYPE, User.UserType.STATION_OPERATOR);
            response.put(USER_ID, operator.getUser().getId());
            response.put("operatorId", operator.getId());

            if (operator.getStation() != null) {
                response.put("stationId", operator.getStation().getId());
                response.put("stationName", operator.getStation().getName());
            }

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(ERROR, e.getMessage()));
        }
    }

    @PostMapping("/register/admin")
    @Operation(summary = "Register a new admin (Admin only)")
    public ResponseEntity<Map<String, Object>> registerAdmin(@RequestBody AdminRegistrationRequest request) {
        try {
            Admin admin = adminService.registerAdmin(request.getAdmin(), request.getUser());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Admin registered successfully");
            response.put(USERNAME, admin.getUser().getUsername());
            response.put(USER_TYPE, User.UserType.ADMIN);
            response.put(USER_ID, admin.getUser().getId());
            response.put("adminId", admin.getId());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(ERROR, e.getMessage()));
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user information")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            String username = jwtService.extractUsername(jwt);
            User user = userService.getUserByUsername(username);

            Map<String, Object> response = new HashMap<>();
            response.put(USERNAME, user.getUsername());
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put(USER_TYPE, user.getUserType());
            response.put(USER_ID, user.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(ERROR, "Invalid token"));
        }
    }
}