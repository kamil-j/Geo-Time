package pl.edu.agh.geotime.service.errors;

public class ScheduleConflictException extends RuntimeException {
    public ScheduleConflictException() {
        super("Conflict in schedule found!");
    }
}
