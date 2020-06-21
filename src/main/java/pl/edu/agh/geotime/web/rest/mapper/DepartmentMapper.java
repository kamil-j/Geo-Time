package pl.edu.agh.geotime.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.edu.agh.geotime.domain.Department;
import pl.edu.agh.geotime.web.rest.dto.DepartmentDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DepartmentMapper extends EntityMapper<DepartmentDTO, Department> {

    @Mapping(target = "subdepartments", ignore = true)
    @Mapping(target = "studyFields", ignore = true)
    @Mapping(target = "locations", ignore = true)
    @Mapping(target = "classUnitGroups", ignore = true)
    Department toEntity(DepartmentDTO departmentDTO);

    default Department fromId(Long id) {
        if (id == null) {
            return null;
        }
        Department department = new Department();
        department.setId(id);
        return department;
    }
}
