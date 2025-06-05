package elytra.stations_management.config;

import elytra.stations_management.models.Admin;
import elytra.stations_management.models.User;
import elytra.stations_management.repositories.UserRepository;
import elytra.stations_management.services.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Slf4j
@Profile("!test")  // Don't run in test profile
public class DataInitializer {

    private final UserRepository userRepository;
    private final AdminService adminService;

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.admin.email:admin@elytra.com}")
    private String adminEmail;

    @Value("${app.admin.firstName:System}")
    private String adminFirstName;

    @Value("${app.admin.lastName:Administrator}")
    private String adminLastName;

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            try {
                // Check if admin already exists
                if (!userRepository.existsByUsername(adminUsername)) {
                    log.info("Creating initial admin user...");
                    
                    // Create admin user
                    User adminUser = User.builder()
                            .username(adminUsername)
                            .password(adminPassword) // Will be encoded in the service
                            .email(adminEmail)
                            .firstName(adminFirstName)
                            .lastName(adminLastName)
                            .userType(User.UserType.ADMIN)
                            .build();

                    Admin admin = Admin.builder().build();
                    
                    adminService.registerAdmin(admin, adminUser);
                    
                    log.info("Initial admin user created successfully with username: {}", adminUsername);
                } else {
                    log.info("Admin user already exists, skipping initialization");
                }
            } catch (Exception e) {
                log.error("Failed to create initial admin user: {}", e.getMessage());
            }
        };
    }
}