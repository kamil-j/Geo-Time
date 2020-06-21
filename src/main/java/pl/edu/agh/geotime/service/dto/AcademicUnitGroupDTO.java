package pl.edu.agh.geotime.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.geotime.domain.ClassUnit;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitGroup;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class AcademicUnitGroupDTO implements Serializable {
    private String name;
    private Long academicUnitId;

    public AcademicUnitGroupDTO(ClassUnit classUnit) {
        AcademicUnitGroup academicUnitGroup = classUnit.getAcademicUnitGroup();
        if(academicUnitGroup != null) {
            this.name = academicUnitGroup.name();
        }
        this.academicUnitId = classUnit.getAcademicUnit().getId();
    }
}
