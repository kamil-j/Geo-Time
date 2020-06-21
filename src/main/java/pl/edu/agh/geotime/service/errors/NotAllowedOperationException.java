package pl.edu.agh.geotime.service.errors;

import java.util.Set;

public class NotAllowedOperationException extends RuntimeException {
    public NotAllowedOperationException(String entityName, String field, String value) {
        super("User does not have access to " + entityName + " with '" + field + "': " + value);
    }

    public NotAllowedOperationException(String entityName, Long entityId) {
        super("User does not have access to " + entityName + " with id: " + entityId);
    }

    public NotAllowedOperationException(String entityName, Set<Long> entityIds) {
        super("User does not have access to " + entityName + " with ids: " + entityIds);
    }
}
