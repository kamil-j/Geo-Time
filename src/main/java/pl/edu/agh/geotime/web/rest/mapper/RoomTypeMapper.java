package pl.edu.agh.geotime.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.edu.agh.geotime.domain.RoomType;
import pl.edu.agh.geotime.web.rest.dto.RoomTypeDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoomTypeMapper extends EntityMapper<RoomTypeDTO, RoomType> {

    @Mapping(target = "rooms", ignore = true)
    RoomType toEntity(RoomTypeDTO departmentDTO);

    default RoomType fromId(Long id) {
        if (id == null) {
            return null;
        }
        RoomType roomType = new RoomType();
        roomType.setId(id);
        return roomType;
    }
}
