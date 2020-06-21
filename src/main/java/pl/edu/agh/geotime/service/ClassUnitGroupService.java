package pl.edu.agh.geotime.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.ClassUnitGroup;
import pl.edu.agh.geotime.repository.ClassUnitGroupRepository;
import pl.edu.agh.geotime.service.helper.UserHelper;

import java.util.Optional;

@Service
@Transactional
public class ClassUnitGroupService {

    private final Logger log = LoggerFactory.getLogger(ClassUnitGroupService.class);

    private final ClassUnitGroupRepository classUnitGroupRepository;
    private final UserHelper userHelper;

    public ClassUnitGroupService(ClassUnitGroupRepository classUnitGroupRepository, UserHelper userHelper) {
        this.classUnitGroupRepository = classUnitGroupRepository;
        this.userHelper = userHelper;
    }

    public ClassUnitGroup save(ClassUnitGroup classUnitGroup) {
        log.debug("Request to save ClassUnitGroup : {}", classUnitGroup);
        classUnitGroup.setDepartment(userHelper.getCurrentUserDepartment());
        return classUnitGroupRepository.save(classUnitGroup);
    }

    @Transactional(readOnly = true)
    public Page<ClassUnitGroup> findAll(Pageable pageable) {
        log.debug("Request to get all ClassUnitGroups");
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        return classUnitGroupRepository.findAllByDepartment_Id(pageable, departmentId);
    }

    @Transactional(readOnly = true)
    public Optional<ClassUnitGroup> findById(Long id) {
        log.debug("Request to get ClassUnitGroup by id: {}", id);
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        return classUnitGroupRepository.findByIdAndDepartment_Id(id, departmentId);
    }

    public void deleteById(Long id) {
        log.debug("Request to delete ClassUnitGroup by id: {}", id);
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        classUnitGroupRepository.deleteByIdAndDepartment_Id(id, departmentId);
    }
}
