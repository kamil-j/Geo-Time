package pl.edu.agh.geotime.web.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.geotime.domain.StudyField;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class StudyFieldInfoDTO implements Serializable {
    private Long id;
    private String name;
    private String shortName;

    public StudyFieldInfoDTO(StudyField studyField) {
        this.id = studyField.getId();
        this.name = studyField.getName();
        this.shortName = studyField.getShortName();
    }
}

