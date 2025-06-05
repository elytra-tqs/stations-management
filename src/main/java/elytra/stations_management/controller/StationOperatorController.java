package elytra.stations_management.controller;

import elytra.stations_management.dto.OperatorRegistrationRequest;
import elytra.stations_management.models.Station;
import elytra.stations_management.models.StationOperator;
import elytra.stations_management.services.StationOperatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/station-operators")
@RequiredArgsConstructor
@Tag(name = "Station Operator", description = "Station Operator management APIs")
public class StationOperatorController {

    private final StationOperatorService stationOperatorService;

    @PostMapping
    @Operation(summary = "Register a new station operator")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StationOperator> registerStationOperator(
            @RequestBody OperatorRegistrationRequest request) {
        try {
            StationOperator operator = stationOperatorService.registerStationOperator(
                    request.getOperator(), 
                    request.getUser(), 
                    request.getStationId()
            );
            return ResponseEntity.ok(operator);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get station operator by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'STATION_OPERATOR')")
    public ResponseEntity<StationOperator> getStationOperatorById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(stationOperatorService.getStationOperatorById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get station operator by user ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'STATION_OPERATOR')")
    public ResponseEntity<StationOperator> getStationOperatorByUserId(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(stationOperatorService.getStationOperatorByUserId(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/station/{stationId}")
    @Operation(summary = "Get station operator by station ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'STATION_OPERATOR')")
    public ResponseEntity<StationOperator> getStationOperatorByStationId(@PathVariable Long stationId) {
        try {
            return ResponseEntity.ok(stationOperatorService.getStationOperatorByStationId(stationId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @Operation(summary = "Get all station operators")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<StationOperator>> getAllStationOperators() {
        return ResponseEntity.ok(stationOperatorService.getAllStationOperators());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update station operator")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StationOperator> updateStationOperator(
            @PathVariable Long id,
            @RequestBody StationOperator stationOperator) {
        try {
            return ResponseEntity.ok(stationOperatorService.updateStationOperator(id, stationOperator));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete station operator")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStationOperator(@PathVariable Long id) {
        try {
            stationOperatorService.deleteStationOperator(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{operatorId}/claim-station/{stationId}")
    @Operation(summary = "Claim a station for operation")
    @PreAuthorize("hasAnyRole('ADMIN', 'STATION_OPERATOR')")
    public ResponseEntity<Map<String, Object>> claimStation(
            @PathVariable Long operatorId, 
            @PathVariable Long stationId) {
        try {
            StationOperator operator = stationOperatorService.claimStation(operatorId, stationId);
            
            Map<String, Object> response = Map.of(
                "message", "Station claimed successfully",
                "operatorId", operator.getId(),
                "stationId", operator.getStation().getId(),
                "stationName", operator.getStation().getName()
            );
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{operatorId}/release-station")
    @Operation(summary = "Release current station")
    @PreAuthorize("hasAnyRole('ADMIN', 'STATION_OPERATOR')")
    public ResponseEntity<Map<String, Object>> releaseStation(@PathVariable Long operatorId) {
        try {
            StationOperator operator = stationOperatorService.releaseStation(operatorId);
            
            Map<String, Object> response = Map.of(
                "message", "Station released successfully",
                "operatorId", operator.getId()
            );
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/available-stations")
    @Operation(summary = "Get all available stations (without operators)")
    @PreAuthorize("hasAnyRole('ADMIN', 'STATION_OPERATOR')")
    public ResponseEntity<List<Station>> getAvailableStations() {
        return ResponseEntity.ok(stationOperatorService.getAvailableStations());
    }
}