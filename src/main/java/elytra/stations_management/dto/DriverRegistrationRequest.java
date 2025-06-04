package elytra.stations_management.dto;

import elytra.stations_management.models.EVDriver;
import elytra.stations_management.models.User;
import lombok.Data;

@Data
public class DriverRegistrationRequest {
    private EVDriver driver;
    private User user;
} 