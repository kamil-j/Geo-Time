package pl.edu.agh.geotime.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import pl.edu.agh.geotime.domain.Semester;
import pl.edu.agh.geotime.web.rest.dto.SemesterDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SemesterMapper extends EntityMapper<SemesterDTO, Semester> {

    @Mapping(target = "classUnits", ignore = true)
    @Mapping(target = "schedulingTimeFrames", ignore = true)
    Semester toEntity(SemesterDTO semesterDTO);

    default Semester fromId(Long id) {
        if (id == null) {
            return null;
        }
        Semester semester = new Semester();
        semester.setId(id);
        return semester;
    }
}
