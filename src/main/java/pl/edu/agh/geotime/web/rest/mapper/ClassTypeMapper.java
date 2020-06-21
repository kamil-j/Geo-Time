package pl.edu.agh.geotime.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.edu.agh.geotime.domain.ClassType;
import pl.edu.agh.geotime.web.rest.dto.ClassTypeDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClassTypeMapper extends EntityMapper<ClassTypeDTO, ClassType> {

    @Mapping(target = "classUnits", ignore = true)
    ClassType toEntity(ClassTypeDTO classTypeDTO);

    default ClassType fromId(Long id) {
        if (id == null) {
            return null;
        }
        ClassType classType = new ClassType();
        classType.setId(id);
        return classType;
    }
}
