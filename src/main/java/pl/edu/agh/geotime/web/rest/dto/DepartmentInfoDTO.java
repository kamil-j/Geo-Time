package pl.edu.agh.geotime.web.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.geotime.domain.Department;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class DepartmentInfoDTO implements Serializable {
    private Long id;
    private String name;
    private String shortName;
    private List<StudyFieldInfoDTO> studyFields;

    public DepartmentInfoDTO(Department department) {
        this.id = department.getId();
        this.name = department.getName();
        this.shortName = department.getShortName();
        this.studyFields = department.getStudyFields().stream()
            .map(StudyFieldInfoDTO::new)
            .collect(Collectors.toList());
    }
}
