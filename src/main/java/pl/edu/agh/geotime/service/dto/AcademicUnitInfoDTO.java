package pl.edu.agh.geotime.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.geotime.domain.AcademicUnit;

@Data
@NoArgsConstructor
public class AcademicUnitInfoDTO {
    private Long id;
    private String name;
    private String year;
    private String degree;
    private Long studyFieldId;
    private String studyFieldName;
    private String studyFieldShortName;
    private Long departmentId;
    private String departmentName;
    private String departmentShortName;

    public AcademicUnitInfoDTO(AcademicUnit academicUnit) {
        this.id = academicUnit.getId();
        this.name = academicUnit.getName();
        this.year = academicUnit.getYear().toString();
        this.degree = academicUnit.getDegree().toString();
        this.studyFieldId = academicUnit.getStudyField().getId();
        this.studyFieldName = academicUnit.getStudyField().getName();
        this.studyFieldShortName = academicUnit.getStudyField().getShortName();
        this.departmentId = academicUnit.getStudyField().getDepartment().getId();
        this.departmentName = academicUnit.getStudyField().getDepartment().getName();
        this.departmentShortName = academicUnit.getStudyField().getDepartment().getShortName();
    }
}
