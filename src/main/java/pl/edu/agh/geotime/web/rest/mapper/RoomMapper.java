package pl.edu.agh.geotime.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.edu.agh.geotime.domain.Room;
import pl.edu.agh.geotime.web.rest.dto.RoomDTO;

@Mapper(componentModel = "spring", uses = {RoomTypeMapper.class, LocationMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoomMapper extends EntityMapper<RoomDTO, Room> {

    @Mapping(source = "roomType.id", target = "roomTypeId")
    @Mapping(source = "roomType.name", target = "roomTypeName")
    @Mapping(source = "location.id", target = "locationId")
    @Mapping(source = "location.name", target = "locationName")
    RoomDTO toDto(Room room);

    @Mapping(source = "roomTypeId", target = "roomType")
    @Mapping(source = "locationId", target = "location")
    @Mapping(target = "classUnits", ignore = true)
    @Mapping(target = "bookingUnits", ignore = true)
    @Mapping(target = "scheduleUnits", ignore = true)
    Room toEntity(RoomDTO roomDTO);

    default Room fromId(Long id) {
        if (id == null) {
            return null;
        }
        Room room = new Room();
        room.setId(id);
        return room;
    }
}
