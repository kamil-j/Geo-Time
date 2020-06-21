package pl.edu.agh.geotime.service.errors;

public class BookingConflictException extends RuntimeException {
    public BookingConflictException() {
        super("Conflict in bookings found!");
    }
}
