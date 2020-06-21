package pl.edu.agh.geotime.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.edu.agh.geotime.domain.ScheduleUnit;
import pl.edu.agh.geotime.web.rest.dto.ScheduleUnitDTO;

@Mapper(componentModel = "spring", uses = {ClassUnitMapper.class, RoomMapper.class})
public interface ScheduleUnitMapper extends EntityMapper<ScheduleUnitDTO, ScheduleUnit> {

    @Mapping(source = "classUnit.id", target = "classUnitId")
    @Mapping(source = "classUnit.title", target = "classUnitTitle")
    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "room.name", target = "roomName")
    ScheduleUnitDTO toDto(ScheduleUnit scheduleUnit);

    @Mapping(source = "classUnitId", target = "classUnit")
    @Mapping(source = "roomId", target = "room")
    ScheduleUnit toEntity(ScheduleUnitDTO scheduleUnitDTO);

    default ScheduleUnit fromId(Long id) {
        if (id == null) {
            return null;
        }
        ScheduleUnit scheduleUnit = new ScheduleUnit();
        scheduleUnit.setId(id);
        return scheduleUnit;
    }
}
