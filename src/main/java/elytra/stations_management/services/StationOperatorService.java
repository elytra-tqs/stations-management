package elytra.stations_management.services;

import elytra.stations_management.exception.StationOperatorException;
import elytra.stations_management.models.StationOperator;
import elytra.stations_management.models.Station;
import elytra.stations_management.models.User;
import elytra.stations_management.repositories.StationOperatorRepository;
import elytra.stations_management.repositories.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StationOperatorService {
    private static final String STATION_NOT_FOUND = "Station not found";

    private final StationOperatorRepository stationOperatorRepository;
    private final StationRepository stationRepository;
    private final UserService userService;

    @Transactional
    public StationOperator registerStationOperator(StationOperator stationOperator, User user, Long stationId) {
        // Check if user is already a station operator
        if (user.getId() != null && stationOperatorRepository.existsByUserId(user.getId())) {
            throw new StationOperatorException("User is already a station operator");
        }

        // Set user type
        user.setUserType(User.UserType.STATION_OPERATOR);
        User savedUser = userService.registerUser(user);

        // Create the station operator
        stationOperator.setUser(savedUser);

        // If stationId is provided, assign the station
        if (stationId != null) {
            // Check if station already has an operator
            if (stationOperatorRepository.existsByStationId(stationId)) {
                throw new StationOperatorException("Station already has an operator assigned");
            }

            // Get the station
            Station station = stationRepository.findById(stationId)
                    .orElseThrow(() -> new RuntimeException(STATION_NOT_FOUND));
            stationOperator.setStation(station);
        }

        return stationOperatorRepository.save(stationOperator);
    }

    @Transactional(readOnly = true)
    public StationOperator getStationOperatorById(Long id) {
        return stationOperatorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Station operator not found"));
    }

    @Transactional(readOnly = true)
    public StationOperator getStationOperatorByUserId(Long userId) {
        return stationOperatorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Station operator not found for user"));
    }

    @Transactional(readOnly = true)
    public StationOperator getStationOperatorByStationId(Long stationId) {
        return stationOperatorRepository.findByStationId(stationId)
                .orElseThrow(() -> new RuntimeException("Station operator not found for station"));
    }

    @Transactional(readOnly = true)
    public List<StationOperator> getAllStationOperators() {
        return stationOperatorRepository.findAll();
    }

    @Transactional
    public StationOperator updateStationOperator(Long id, StationOperator updatedStationOperator) {
        StationOperator existingOperator = getStationOperatorById(id);

        // Update user information
        if (updatedStationOperator.getUser() != null) {
            User updatedUser = userService.updateUser(
                    existingOperator.getUser().getId(),
                    updatedStationOperator.getUser()
            );
            existingOperator.setUser(updatedUser);
        }

        // Update station assignment if provided
        if (updatedStationOperator.getStation() != null &&
                !updatedStationOperator.getStation().getId().equals(existingOperator.getStation().getId())) {

            // Check if new station already has an operator
            if (stationOperatorRepository.existsByStationId(updatedStationOperator.getStation().getId())) {
                throw new StationOperatorException("Target station already has an operator assigned");
            }

            Station newStation = stationRepository.findById(updatedStationOperator.getStation().getId())
                    .orElseThrow(() -> new RuntimeException(STATION_NOT_FOUND));
            existingOperator.setStation(newStation);
        }

        return stationOperatorRepository.save(existingOperator);
    }

    @Transactional
    public void deleteStationOperator(Long id) {
        StationOperator stationOperator = getStationOperatorById(id);
        stationOperatorRepository.delete(stationOperator);
    }

    @Transactional
    public StationOperator claimStation(Long operatorId, Long stationId) {
        StationOperator operator = getStationOperatorById(operatorId);

        // Check if operator already has a station
        if (operator.getStation() != null) {
            throw new StationOperatorException("Operator already manages a station");
        }

        // Check if station already has an operator
        if (stationOperatorRepository.existsByStationId(stationId)) {
            throw new StationOperatorException("Station already has an operator assigned");
        }

        // Get the station
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException(STATION_NOT_FOUND));

        // Assign the station to the operator
        operator.setStation(station);

        return stationOperatorRepository.save(operator);
    }

    @Transactional
    public StationOperator releaseStation(Long operatorId) {
        StationOperator operator = getStationOperatorById(operatorId);

        if (operator.getStation() == null) {
            throw new StationOperatorException("Operator doesn't manage any station");
        }

        operator.setStation(null);
        return stationOperatorRepository.save(operator);
    }

    @Transactional(readOnly = true)
    public List<Station> getAvailableStations() {
        // Get all stations that don't have an operator
        List<Long> stationsWithOperators = stationOperatorRepository.findAll().stream()
                .map(op -> op.getStation() != null ? op.getStation().getId() : null)
                .filter(Objects::nonNull)
                .toList();

        if (stationsWithOperators.isEmpty()) {
            return stationRepository.findAll();
        }

        return stationRepository.findAll().stream()
                .filter(station -> !stationsWithOperators.contains(station.getId()))
                .toList();
    }
}