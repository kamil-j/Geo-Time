package pl.edu.agh.geotime.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.edu.agh.geotime.domain.StudyField;
import pl.edu.agh.geotime.web.rest.dto.StudyFieldDTO;

@Mapper(componentModel = "spring", uses = {DepartmentMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudyFieldMapper extends EntityMapper<StudyFieldDTO, StudyField> {

    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.shortName", target = "departmentShortName")
    StudyFieldDTO toDto(StudyField studyField);

    @Mapping(source = "departmentId", target = "department")
    @Mapping(target = "academicUnits", ignore = true)
    StudyField toEntity(StudyFieldDTO studyFieldDTO);

    default StudyField fromId(Long id) {
        if (id == null) {
            return null;
        }
        StudyField studyField = new StudyField();
        studyField.setId(id);
        return studyField;
    }
}
