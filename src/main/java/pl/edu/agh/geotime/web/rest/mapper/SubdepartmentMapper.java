package pl.edu.agh.geotime.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.edu.agh.geotime.domain.Subdepartment;
import pl.edu.agh.geotime.web.rest.dto.SubdepartmentDTO;

@Mapper(componentModel = "spring", uses = {DepartmentMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubdepartmentMapper extends EntityMapper<SubdepartmentDTO, Subdepartment> {

    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.shortName", target = "departmentShortName")
    SubdepartmentDTO toDto(Subdepartment subdepartment);

    @Mapping(source = "departmentId", target = "department")
    @Mapping(target = "usersExt", ignore = true)
    @Mapping(target = "schedulingTimeFrames", ignore = true)
    @Mapping(target = "classUnits", ignore = true)
    Subdepartment toEntity(SubdepartmentDTO subdepartmentDTO);

    default Subdepartment fromId(Long id) {
        if (id == null) {
            return null;
        }
        Subdepartment subdepartment = new Subdepartment();
        subdepartment.setId(id);
        return subdepartment;
    }
}
