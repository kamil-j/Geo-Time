package pl.edu.agh.geotime.service.util;

import pl.edu.agh.geotime.domain.ClassUnit;
import pl.edu.agh.geotime.domain.Room;

import java.util.Optional;

public class ClassUnitUtil {

    private ClassUnitUtil() {}

    public static Optional<Room> getRoomWithId(ClassUnit classUnit, Long roomId) {
        return classUnit.getRooms().stream()
            .filter(room -> room.getId().equals(roomId))
            .findFirst();
    }
}
