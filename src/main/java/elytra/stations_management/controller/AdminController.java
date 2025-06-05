package elytra.stations_management.controller;

import elytra.stations_management.dto.AdminRegistrationRequest;
import elytra.stations_management.models.Admin;
import elytra.stations_management.services.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin management APIs")
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    @Operation(summary = "Register a new admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Admin> registerAdmin(@RequestBody AdminRegistrationRequest request) {
        try {
            Admin admin = adminService.registerAdmin(request.getAdmin(), request.getUser());
            return ResponseEntity.ok(admin);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get admin by ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Admin> getAdminById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(adminService.getAdminById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get admin by user ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Admin> getAdminByUserId(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(adminService.getAdminByUserId(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @Operation(summary = "Get all admins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Admin>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @PostMapping("/{adminId}/stations/{stationId}")
    @Operation(summary = "Assign a station to an admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Admin> assignStationToAdmin(
            @PathVariable Long adminId,
            @PathVariable Long stationId) {
        try {
            return ResponseEntity.ok(adminService.assignStationToAdmin(adminId, stationId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{adminId}/stations/{stationId}")
    @Operation(summary = "Remove a station from an admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Admin> removeStationFromAdmin(
            @PathVariable Long adminId,
            @PathVariable Long stationId) {
        try {
            return ResponseEntity.ok(adminService.removeStationFromAdmin(adminId, stationId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Admin> updateAdmin(
            @PathVariable Long id,
            @RequestBody Admin admin) {
        try {
            return ResponseEntity.ok(adminService.updateAdmin(id, admin));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        try {
            adminService.deleteAdmin(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}