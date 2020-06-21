package pl.edu.agh.geotime.service.errors;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, Long entityId) {
        super("Entity " + entityName + " with id: " + entityId + " not found");
    }
}
