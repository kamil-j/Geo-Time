package pl.edu.agh.geotime.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.ClassUnit;
import pl.edu.agh.geotime.domain.Semester;
import pl.edu.agh.geotime.domain.UserExt;
import pl.edu.agh.geotime.repository.ClassUnitRepository;
import pl.edu.agh.geotime.repository.SemesterRepository;
import pl.edu.agh.geotime.service.errors.EntityNotFoundException;
import pl.edu.agh.geotime.service.helper.ClassUnitHelper;
import pl.edu.agh.geotime.service.helper.UserHelper;
import pl.edu.agh.geotime.service.util.UserUtil;

import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class ClassUnitService {

    private final Logger log = LoggerFactory.getLogger(ClassUnitService.class);
    private final DepartmentAccessService departmentAccessService;
    private final ClassUnitRepository classUnitRepository;
    private final SemesterRepository semesterRepository;
    private final ClassUnitHelper classUnitHelper;
    private final UserHelper userHelper;

    public ClassUnitService(DepartmentAccessService departmentAccessService, ClassUnitRepository classUnitRepository,
                            SemesterRepository semesterRepository, ClassUnitHelper classUnitHelper, UserHelper userHelper) {
        this.departmentAccessService = departmentAccessService;
        this.classUnitRepository = classUnitRepository;
        this.semesterRepository = semesterRepository;
        this.classUnitHelper = classUnitHelper;
        this.userHelper = userHelper;
    }

    public ClassUnit save(ClassUnit classUnit) {
        log.debug("Request to save ClassUnit : {}", classUnit);
        departmentAccessService.checkAccess(classUnit);

        //Semester entity have to be connected to fill onlyHalfSemesterInd
        Long semesterId = classUnit.getSemester().getId();
        Semester semester = semesterRepository.findById(semesterId)
            .orElseThrow(() -> new EntityNotFoundException("semester", semesterId));
        classUnit.setSemester(semester);

        classUnitHelper.fillOnlyHalfSemesterInd(classUnit);
        return classUnitRepository.save(classUnit);
    }

    @Transactional(readOnly = true)
    public Page<ClassUnit> findAll(Pageable pageable) {
        log.debug("Request to get all ClassUnits");
        UserExt user = userHelper.getCurrentUser();
        if(UserUtil.isManager(user)) {
            Long departmentId = user.getSubdepartment().getDepartment().getId();
            return classUnitRepository.findAllBySubdepartment_Department_Id(pageable, departmentId);
        }
        Long subdepartmentId = user.getSubdepartment().getId();
        return classUnitRepository.findAllBySubdepartment_Id(pageable, subdepartmentId);
    }

    @Transactional(readOnly = true)
    public Optional<ClassUnit> findById(Long id) {
        log.debug("Request to get ClassUnit by id: {}", id);
        UserExt user = userHelper.getCurrentUser();
        if(UserUtil.isManager(user)) {
            Long departmentId = user.getSubdepartment().getDepartment().getId();
            return classUnitRepository.findByIdAndSubdepartment_Department_Id(id, departmentId);
        }
        Long subdepartmentId = user.getSubdepartment().getId();
        return classUnitRepository.findByIdAndSubdepartment_Id(id, subdepartmentId);
    }

    @Transactional(readOnly = true)
    public Optional<ClassUnit> findNotBookedByIdAndUser(Long id, UserExt user) {
        log.debug("Request to get not booked ClassUnit by id: {}, user: {}", id, user);
        return classUnitRepository.findByIdAndUserExtAndBookingUnitNull(id, user);
    }

    @Transactional(readOnly = true)
    public Optional<ClassUnit> findByIdAndUser(Long id, UserExt user) {
        log.debug("Request to get ClassUnit by id: {}, user: {}", id, user);
        return classUnitRepository.findByIdAndUserExt(id, user);
    }

    @Transactional(readOnly = true)
    public Set<ClassUnit> findBookedByClassUnitGroupIdIn(Set<Long> classUnitGroupIds) {
        log.debug("Request to get booked ClassUnits by classUnitGroupIds: {}", classUnitGroupIds);
        return classUnitRepository.findByBookingUnitIsNotNullAndClassUnitGroup_IdIn(classUnitGroupIds);
    }

    @Transactional(readOnly = true)
    public Set<ClassUnit> findBookedByUserIsNotAndClassUnitGroupIdIn(UserExt user, Set<Long> classUnitGroupIds) {
        log.debug("Request to get booked ClassUnits by user is not: {} and classUnitGroupIds: {}", user,
            classUnitGroupIds);
        return classUnitRepository.findByBookingUnitIsNotNullAndUserExtIsNotAndClassUnitGroup_IdIn(user, classUnitGroupIds);
    }

    @Transactional(readOnly = true)
    public Set<ClassUnit> findNotBookedByClassUnitGroupId(Long classUnitGroupId) {
        log.debug("Request to get not booked ClassUnits by classUnitGroupId: {}", classUnitGroupId);
        return classUnitRepository.findByBookingUnitIsNullAndClassUnitGroup_Id(classUnitGroupId);
    }

    @Transactional(readOnly = true)
    public Page<ClassUnit> findNotBookedByUserAndSemester(Pageable pageable, UserExt userExt, Semester semester) {
        log.debug("Request to get not booked ClassUnits by User: {} and Semester: {}", userExt, semester);
        return classUnitRepository.findByUserExtAndSemesterAndBookingUnitIsNullOrderByTitle(pageable, userExt, semester);
    }

    public void deleteById(Long id) {
        log.debug("Request to delete ClassUnit by id: {}", id);
        UserExt user = userHelper.getCurrentUser();
        if(UserUtil.isManager(user)) {
            Long departmentId = user.getSubdepartment().getDepartment().getId();
            classUnitRepository.deleteByIdAndSubdepartment_Department_Id(id, departmentId);
        }
        Long subdepartmentId = user.getSubdepartment().getId();
        classUnitRepository.deleteByIdAndSubdepartment_Id(id, subdepartmentId);
    }

    void recalculateOnlyHalfSemesterInd(Long semesterId) {
        log.debug("Request to recalculate onlySemesterHalf indicator by semesterId: {}", semesterId);
        Set<ClassUnit> semesterClassUnits = classUnitRepository.findBySemester_Id(semesterId);
        classUnitHelper.fillOnlyHalfSemesterInd(semesterClassUnits);
    }

    public void assign(Long classUnitId, Long userId) {
        ClassUnit classUnit = findById(classUnitId)
            .orElseThrow(() -> new EntityNotFoundException("classUnit", classUnitId));

        UserExt user = new UserExt();
        user.setId(userId);
        classUnit.setUserExt(user);

        save(classUnit);
    }
}
