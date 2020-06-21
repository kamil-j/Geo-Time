package pl.edu.agh.geotime.web.rest.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.geotime.domain.ClassUnit;
import pl.edu.agh.geotime.domain.ClassUnitGroup;
import pl.edu.agh.geotime.repository.ClassUnitGroupRepository;
import pl.edu.agh.geotime.service.dto.AcademicUnitGroupDTO;
import pl.edu.agh.geotime.web.rest.dto.ClassUnitDTO;
import pl.edu.agh.geotime.web.rest.dto.ext.ClassUnitExtDTO;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClassUnitExtMapper {

    private final ClassUnitMapper classUnitMapper;
    private final ClassUnitGroupRepository classUnitGroupRepository;

    @Autowired
    public ClassUnitExtMapper(ClassUnitMapper classUnitMapper, ClassUnitGroupRepository classUnitGroupRepository) {
        this.classUnitMapper = classUnitMapper;
        this.classUnitGroupRepository = classUnitGroupRepository;
    }

    public ClassUnitExtDTO toDto(ClassUnit classUnit) {
        ClassUnitDTO classUnitDTO = classUnitMapper.toDto(classUnit);
        ClassUnitExtDTO classUnitExtDTO = new ClassUnitExtDTO(classUnitDTO);

        ClassUnitGroup classUnitGroup = classUnit.getClassUnitGroup();
        if(classUnitGroup != null) {
            Set<ClassUnit> classUnits = classUnitGroupRepository.findByIdWithClassUnits(classUnitGroup.getId())
                .getClassUnits();

            Long academicUnitIdFromClassUnit = classUnit.getAcademicUnit().getId();
            classUnitExtDTO.setRelatedAcademicUnitGroups(getRelatedAcademicUnitGroups(classUnits, academicUnitIdFromClassUnit));

            Long userIdFromClassUnit = classUnit.getUserExt().getId();
            classUnitExtDTO.setRelatedUserIds(getRelatedUserIds(classUnits, userIdFromClassUnit));
        }

        return classUnitExtDTO;
    }

    private Set<AcademicUnitGroupDTO> getRelatedAcademicUnitGroups(Set<ClassUnit> classUnits, Long academicUnitIdFromClassUnit) {
        return classUnits.stream()
            .map(AcademicUnitGroupDTO::new)
            .filter(relatedAcademicUnit -> !relatedAcademicUnit.getAcademicUnitId().equals(academicUnitIdFromClassUnit))
            .collect(Collectors.toSet());
    }

    private Set<Long> getRelatedUserIds(Set<ClassUnit> classUnits, Long userIdFromScheduleUnit) {
        return classUnits.stream()
            .map(classUnit -> classUnit.getUserExt().getId())
            .filter(userId -> !userId.equals(userIdFromScheduleUnit))
            .collect(Collectors.toSet());
    }
}
