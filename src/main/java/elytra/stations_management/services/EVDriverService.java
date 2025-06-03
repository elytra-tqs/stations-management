package elytra.stations_management.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import elytra.stations_management.models.EVDriver;
import elytra.stations_management.models.User;
import elytra.stations_management.repositories.EVDriverRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EVDriverService {
    private final EVDriverRepository evDriverRepository;
    private final UserService userService;

    @Transactional
    public EVDriver registerDriver(EVDriver driver, User user) {
        user.setUserType(User.UserType.EV_DRIVER);
        User savedUser = userService.registerUser(user);
        driver.setUser(savedUser);
        return evDriverRepository.save(driver);
    }

    @Transactional(readOnly = true)
    public EVDriver getDriverById(Long id) {
        return evDriverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
    }

    @Transactional(readOnly = true)
    public EVDriver getDriverByUserId(Long userId) {
        return evDriverRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
    }

    @Transactional(readOnly = true)
    public List<EVDriver> getAllDrivers() {
        return evDriverRepository.findAll();
    }

    @Transactional
    public EVDriver updateDriver(Long id, EVDriver updatedDriver) {
        EVDriver existingDriver = getDriverById(id);
        User updatedUser = userService.updateUser(existingDriver.getUser().getId(), updatedDriver.getUser());
        existingDriver.setUser(updatedUser);
        return evDriverRepository.save(existingDriver);
    }

    @Transactional
    public void deleteDriver(Long id) {
        EVDriver driver = getDriverById(id);
        evDriverRepository.delete(driver);
    }
}
