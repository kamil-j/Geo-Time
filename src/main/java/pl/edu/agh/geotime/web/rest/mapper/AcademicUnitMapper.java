package pl.edu.agh.geotime.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.edu.agh.geotime.domain.AcademicUnit;
import pl.edu.agh.geotime.web.rest.dto.AcademicUnitDTO;

@Mapper(componentModel = "spring", uses = {StudyFieldMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AcademicUnitMapper extends EntityMapper<AcademicUnitDTO, AcademicUnit> {

    @Mapping(source = "studyField.id", target = "studyFieldId")
    @Mapping(source = "studyField.name", target = "studyFieldName")
    AcademicUnitDTO toDto(AcademicUnit academicUnit);

    @Mapping(source = "studyFieldId", target = "studyField")
    @Mapping(target = "classUnits", ignore = true)
    AcademicUnit toEntity(AcademicUnitDTO academicUnitDTO);

    default AcademicUnit fromId(Long id) {
        if (id == null) {
            return null;
        }
        AcademicUnit academicUnit = new AcademicUnit();
        academicUnit.setId(id);
        return academicUnit;
    }
}
