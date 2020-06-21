package pl.edu.agh.geotime.web.rest.dto.ext;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pl.edu.agh.geotime.service.dto.AcademicUnitGroupDTO;
import pl.edu.agh.geotime.web.rest.dto.ClassUnitDTO;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class ClassUnitExtDTO extends ClassUnitDTO {
    private Set<AcademicUnitGroupDTO> relatedAcademicUnitGroups;
    private Set<Long> relatedUserIds;

    public ClassUnitExtDTO(ClassUnitDTO classUnitDTO) {
        this.setId(classUnitDTO.getId());
        this.setTitle(classUnitDTO.getTitle());
        this.setDescription(classUnitDTO.getDescription());
        this.setDuration(classUnitDTO.getDuration());
        this.setHoursQuantity(classUnitDTO.getHoursQuantity());
        this.setFrequency(classUnitDTO.getFrequency());
        this.setAcademicUnitGroup(classUnitDTO.getAcademicUnitGroup());
        this.setOnlySemesterHalf(classUnitDTO.getOnlySemesterHalf());
        this.setRooms(classUnitDTO.getRooms());
        this.setUserId(classUnitDTO.getUserId());
        this.setUserLogin(classUnitDTO.getUserLogin());
        this.setAcademicUnitId(classUnitDTO.getAcademicUnitId());
        this.setAcademicUnitName(classUnitDTO.getAcademicUnitName());
        this.setSemesterId(classUnitDTO.getSemesterId());
        this.setSemesterName(classUnitDTO.getSemesterName());
        this.setClassTypeId(classUnitDTO.getClassTypeId());
        this.setClassTypeName(classUnitDTO.getClassTypeName());
        this.setClassUnitGroupId(classUnitDTO.getClassUnitGroupId());
        this.setClassUnitGroupName(classUnitDTO.getClassUnitGroupName());
    }
}
