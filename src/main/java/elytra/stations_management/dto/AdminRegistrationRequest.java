package elytra.stations_management.dto;

import elytra.stations_management.models.Admin;
import elytra.stations_management.models.User;
import lombok.Data;

@Data
public class AdminRegistrationRequest {
    private Admin admin;
    private User user;
}