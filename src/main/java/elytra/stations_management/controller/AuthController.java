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
                response.put("username", user.getUsername());
                response.put("userType", user.getUserType());
                response.put("userId", user.getId());
                
                // Add role-specific information
                switch (user.getUserType()) {
                    case EV_DRIVER:
                        try {
                            EVDriver driver = evDriverService.getDriverByUserId(user.getId());
                            response.put("driverId", driver.getId());
                        } catch (Exception e) {
                            // Driver might not be fully set up yet
                        }
                        break;
                    case STATION_OPERATOR:
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
                        break;
                    case ADMIN:
                        try {
                            Admin admin = adminService.getAdminByUserId(user.getId());
                            response.put("adminId", admin.getId());
                        } catch (Exception e) {
                            // Admin might not be fully set up yet
                        }
                        break;
                }
                
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid credentials");
        }
        throw new BadCredentialsException("Invalid credentials");
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
            response.put("username", driver.getUser().getUsername());
            response.put("userType", User.UserType.EV_DRIVER);
            response.put("userId", driver.getUser().getId());
            response.put("driverId", driver.getId());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register/operator")
    @Operation(summary = "Register a new station operator (Admin only)")
    public ResponseEntity<Map<String, Object>> registerOperator(@RequestBody OperatorRegistrationRequest request) {
        try {
            // This endpoint should be protected and only accessible by admins
            // The security is handled by Spring Security configuration
            
            StationOperator operator = stationOperatorService.registerStationOperator(
                    request.getOperator(), 
                    request.getUser(), 
                    request.getStationId()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Station operator registered successfully");
            response.put("username", operator.getUser().getUsername());
            response.put("userType", User.UserType.STATION_OPERATOR);
            response.put("userId", operator.getUser().getId());
            response.put("operatorId", operator.getId());
            
            // Include station info only if assigned
            if (operator.getStation() != null) {
                response.put("stationId", operator.getStation().getId());
                response.put("stationName", operator.getStation().getName());
            }
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register/admin")
    @Operation(summary = "Register a new admin (Admin only)")
    public ResponseEntity<Map<String, Object>> registerAdmin(@RequestBody AdminRegistrationRequest request) {
        try {
            // This endpoint should be protected and only accessible by existing admins
            // The security is handled by Spring Security configuration
            
            Admin admin = adminService.registerAdmin(request.getAdmin(), request.getUser());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Admin registered successfully");
            response.put("username", admin.getUser().getUsername());
            response.put("userType", User.UserType.ADMIN);
            response.put("userId", admin.getUser().getId());
            response.put("adminId", admin.getId());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user information")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            // Remove "Bearer " prefix
            String jwt = token.substring(7);
            String username = jwtService.extractUsername(jwt);
            User user = userService.getUserByUsername(username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("userType", user.getUserType());
            response.put("userId", user.getId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid token"));
        }
    }
}