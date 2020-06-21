package pl.edu.agh.geotime.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.StudyField;
import pl.edu.agh.geotime.repository.StudyFieldRepository;
import pl.edu.agh.geotime.service.helper.UserHelper;

import java.util.Optional;

@Service
@Transactional
public class StudyFieldService {

    private final Logger log = LoggerFactory.getLogger(StudyFieldService.class);

    private final StudyFieldRepository studyFieldRepository;
    private final UserHelper userHelper;

    public StudyFieldService(StudyFieldRepository studyFieldRepository, UserHelper userHelper) {
        this.studyFieldRepository = studyFieldRepository;
        this.userHelper = userHelper;
    }

    public StudyField save(StudyField studyField) {
        log.debug("Request to save StudyField: {}", studyField);
        studyField.setDepartment(userHelper.getCurrentUserDepartment());
        studyField = studyFieldRepository.save(studyField);
        return studyField;
    }

    @Transactional(readOnly = true)
    public Page<StudyField> findAll(Pageable pageable) {
        log.debug("Request to get all StudyFields");
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        return studyFieldRepository.findAllByDepartment_Id(pageable, departmentId);
    }

    @Transactional(readOnly = true)
    public Optional<StudyField> findById(Long id) {
        log.debug("Request to get StudyField by id: {}", id);
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        return studyFieldRepository.findByIdAndDepartment_Id(id, departmentId);
    }

    @Transactional(readOnly = true)
    public Optional<StudyField> findByShortName(String shortName) {
        log.debug("Request to get StudyField by shortName: {}", shortName);
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        return studyFieldRepository.findByShortNameAndDepartment_Id(shortName, departmentId);
    }

    public void deleteById(Long id) {
        log.debug("Request to delete StudyField by id: {}", id);
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        studyFieldRepository.deleteByIdAndDepartment_Id(id, departmentId);
    }
}
