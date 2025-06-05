package elytra.stations_management.services;

import elytra.stations_management.exception.AdminException;
import elytra.stations_management.models.Admin;
import elytra.stations_management.models.Station;
import elytra.stations_management.models.User;
import elytra.stations_management.repositories.AdminRepository;
import elytra.stations_management.repositories.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final StationRepository stationRepository;
    private final UserService userService;

    @Transactional
    public Admin registerAdmin(Admin admin, User user) {
        if (adminRepository.existsByUserId(user.getId())) {
            throw new AdminException("User is already an admin");
        }

        user.setUserType(User.UserType.ADMIN);
        User savedUser = userService.registerUser(user);

        admin.setUser(savedUser);
        admin.setStations(new ArrayList<>());

        return adminRepository.save(admin);
    }

    @Transactional(readOnly = true)
    public Admin getAdminById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    @Transactional(readOnly = true)
    public Admin getAdminByUserId(Long userId) {
        return adminRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Admin not found for user"));
    }

    @Transactional(readOnly = true)
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Transactional
    public Admin assignStationToAdmin(Long adminId, Long stationId) {
        Admin admin = getAdminById(adminId);
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found"));

        if (station.getAdmin() != null && !station.getAdmin().getId().equals(adminId)) {
            throw new AdminException("Station is already assigned to another admin");
        }

        if (!admin.getStations().contains(station)) {
            admin.getStations().add(station);
            station.setAdmin(admin);
            stationRepository.save(station);
        }

        return adminRepository.save(admin);
    }

    @Transactional
    public Admin removeStationFromAdmin(Long adminId, Long stationId) {
        Admin admin = getAdminById(adminId);
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found"));

        if (station.getAdmin() == null || !station.getAdmin().getId().equals(adminId)) {
            throw new AdminException("Station does not belong to this admin");
        }

        admin.getStations().remove(station);
        station.setAdmin(null);
        stationRepository.save(station);

        return adminRepository.save(admin);
    }

    @Transactional
    public Admin updateAdmin(Long id, Admin updatedAdmin) {
        Admin existingAdmin = getAdminById(id);

        if (updatedAdmin.getUser() != null) {
            User updatedUser = userService.updateUser(
                existingAdmin.getUser().getId(), 
                updatedAdmin.getUser()
            );
            existingAdmin.setUser(updatedUser);
        }

        return adminRepository.save(existingAdmin);
    }

    @Transactional
    public void deleteAdmin(Long id) {
        Admin admin = getAdminById(id);

        for (Station station : admin.getStations()) {
            station.setAdmin(null);
            stationRepository.save(station);
        }
        
        adminRepository.delete(admin);
    }
}