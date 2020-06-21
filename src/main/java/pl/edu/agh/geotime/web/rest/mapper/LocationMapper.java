package pl.edu.agh.geotime.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.edu.agh.geotime.domain.Location;
import pl.edu.agh.geotime.web.rest.dto.LocationDTO;

@Mapper(componentModel = "spring", uses = {DepartmentMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationMapper extends EntityMapper<LocationDTO, Location> {

    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.shortName", target = "departmentShortName")
    LocationDTO toDto(Location location);

    @Mapping(source = "departmentId", target = "department")
    @Mapping(target = "rooms", ignore = true)
    Location toEntity(LocationDTO locationDTO);

    default Location fromId(Long id) {
        if (id == null) {
            return null;
        }
        Location location = new Location();
        location.setId(id);
        return location;
    }
}
