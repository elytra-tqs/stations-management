package elytra.stations_management.dto;

import elytra.stations_management.models.StationOperator;
import elytra.stations_management.models.User;
import lombok.Data;

@Data
public class OperatorRegistrationRequest {
    private StationOperator operator;
    private User user;
    private Long stationId;
}