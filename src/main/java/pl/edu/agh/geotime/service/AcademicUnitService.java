package pl.edu.agh.geotime.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.AcademicUnit;
import pl.edu.agh.geotime.domain.StudyField;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitDegree;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitYear;
import pl.edu.agh.geotime.repository.AcademicUnitRepository;
import pl.edu.agh.geotime.service.dto.AcademicUnitGroupsInfoDTO;
import pl.edu.agh.geotime.service.dto.AcademicUnitInfoDTO;
import pl.edu.agh.geotime.service.helper.UserHelper;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@Transactional
public class AcademicUnitService {

    private final Logger log = LoggerFactory.getLogger(AcademicUnitService.class);
    private final DepartmentAccessService departmentAccessService;
    private final AcademicUnitRepository academicUnitRepository;
    private final StudyFieldService studyFieldService;
    private final UserHelper userHelper;

    public AcademicUnitService(DepartmentAccessService departmentAccessService, AcademicUnitRepository academicUnitRepository,
                               StudyFieldService studyFieldService, UserHelper userHelper) {
        this.departmentAccessService = departmentAccessService;
        this.academicUnitRepository = academicUnitRepository;
        this.studyFieldService = studyFieldService;
        this.userHelper = userHelper;
    }

    public AcademicUnit save(AcademicUnit academicUnit) {
        log.debug("Request to save AcademicUnit: {}", academicUnit);
        departmentAccessService.checkAccess(academicUnit);
        academicUnit.setName(createAcademicName(academicUnit));
        return academicUnitRepository.save(academicUnit);
    }

    private String createAcademicName(AcademicUnit academicUnit) {
        Long studyFieldId = academicUnit.getStudyField().getId();
        StudyField studyField = studyFieldService.findById(studyFieldId)
            .orElseThrow(() -> new EntityNotFoundException("User does not have access to study field with id: " + studyFieldId));
        AcademicUnitDegree degree = academicUnit.getDegree();
        AcademicUnitYear year = academicUnit.getYear();
        return studyField.getShortName() + "-" + degree.getShortName() + "-" + year.getShortName();
    }

    public Page<AcademicUnit> findAll(Pageable pageable) {
        log.debug("Request to get all AcademicUnits");
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        return academicUnitRepository.findAllByStudyField_Department_Id(pageable, departmentId);
    }

    @Transactional(readOnly = true)
    public Optional<AcademicUnit> findById(Long id) {
        log.debug("Request to get AcademicUnit by id: {}", id);
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        return academicUnitRepository.findByIdAndStudyField_Department_Id(id, departmentId);
    }

    @Transactional(readOnly = true)
    public Optional<AcademicUnit> findByStudyFieldIdAndYearAndDegree(Long studyFieldId, AcademicUnitYear year,
                                                                     AcademicUnitDegree degree) {
        log.debug("Request to get academicUnit by studyFieldId: {}, year: {}, degree: {}", studyFieldId, year, degree);
        return academicUnitRepository.findByStudyField_IdAndYearAndDegree(studyFieldId, year, degree);
    }

    public void deleteById(Long id) {
        log.debug("Request to delete AcademicUnit by id: {}", id);
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        academicUnitRepository.deleteByIdAndStudyField_DepartmentId(id, departmentId);
    }

    @Transactional(readOnly = true)
    public Optional<AcademicUnitInfoDTO> getAcademicUnitInfo(Long id) {
        log.debug("Getting info about AcademicUnit: {}" , id);
        return academicUnitRepository.findOneById(id)
            .map(AcademicUnitInfoDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<AcademicUnitGroupsInfoDTO> getAcademicUnitGroupsInfo(Long id) {
        log.debug("Getting info about AcademicUnitGroups: {}" , id);
        return academicUnitRepository.findOneWithClassUnitsById(id)
            .map(AcademicUnitGroupsInfoDTO::new);
    }
}
