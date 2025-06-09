package elytra.stations_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long userId;
    private Long chargerId;
    private Long carId;
}