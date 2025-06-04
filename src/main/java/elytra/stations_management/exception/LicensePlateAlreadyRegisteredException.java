package elytra.stations_management.exception;

public class LicensePlateAlreadyRegisteredException extends RuntimeException {
    public LicensePlateAlreadyRegisteredException(String message) {
        super(message);
    }
}
