package pl.edu.agh.geotime.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.edu.agh.geotime.domain.ClassUnitGroup;
import pl.edu.agh.geotime.web.rest.dto.ClassUnitGroupDTO;

@Mapper(componentModel = "spring", uses = {DepartmentMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClassUnitGroupMapper extends EntityMapper<ClassUnitGroupDTO, ClassUnitGroup> {

    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.shortName", target = "departmentShortName")
    ClassUnitGroupDTO toDto(ClassUnitGroup classUnitGroup);

    @Mapping(source = "departmentId", target = "department")
    @Mapping(target = "classUnits", ignore = true)
    ClassUnitGroup toEntity(ClassUnitGroupDTO classUnitGroupDTO);

    default ClassUnitGroup fromId(Long id) {
        if (id == null) {
            return null;
        }
        ClassUnitGroup classUnitGroup = new ClassUnitGroup();
        classUnitGroup.setId(id);
        return classUnitGroup;
    }
}
