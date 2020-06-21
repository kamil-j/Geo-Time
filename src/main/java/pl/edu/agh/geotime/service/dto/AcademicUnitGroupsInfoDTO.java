package pl.edu.agh.geotime.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import pl.edu.agh.geotime.domain.AcademicUnit;
import pl.edu.agh.geotime.domain.ClassUnit;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitGroup;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class AcademicUnitGroupsInfoDTO implements Serializable {
    private Long academicUnitId;
    private Set<AcademicUnitGroup> academicUnitGroups;

    public AcademicUnitGroupsInfoDTO(AcademicUnit academicUnit) {
        this.academicUnitId = academicUnit.getId();
        this.academicUnitGroups = academicUnit.getClassUnits()
            .stream()
            .filter(classUnit -> !classUnit.getScheduleUnits().isEmpty())
            .map(ClassUnit::getAcademicUnitGroup)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }
}
