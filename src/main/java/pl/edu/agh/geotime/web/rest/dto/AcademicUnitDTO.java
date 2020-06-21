package pl.edu.agh.geotime.web.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitDegree;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitYear;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class AcademicUnitDTO implements Serializable {

    private Long id;

    private String name;

    @NotNull
    private AcademicUnitYear year;

    @NotNull
    private AcademicUnitDegree degree;

    @Size(max = 50)
    private String description;

    @NotNull
    private Long studyFieldId;

    private String studyFieldName;
}
