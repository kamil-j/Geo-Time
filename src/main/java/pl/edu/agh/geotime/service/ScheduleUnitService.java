package pl.edu.agh.geotime.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.agh.geotime.domain.*;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitGroup;
import pl.edu.agh.geotime.repository.ScheduleUnitRepository;
import pl.edu.agh.geotime.service.errors.ScheduleConflictException;
import pl.edu.agh.geotime.service.helper.UserHelper;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static pl.edu.agh.geotime.config.Constants.TIME_SPACE_BETWEEN_SCHEDULE_UNITS;

@Service
@Transactional
public class ScheduleUnitService {

    private final Logger log = LoggerFactory.getLogger(ScheduleUnitService.class);
    private final DepartmentAccessService departmentAccessService;
    private final ScheduleUnitRepository scheduleUnitRepository;
    private final UserHelper userHelper;

    public ScheduleUnitService(DepartmentAccessService departmentAccessService, ScheduleUnitRepository scheduleUnitRepository,
                               UserHelper userHelper) {
        this.departmentAccessService = departmentAccessService;
        this.scheduleUnitRepository = scheduleUnitRepository;
        this.userHelper = userHelper;
    }

    @Transactional
    public ScheduleUnit save(ScheduleUnit scheduleUnit) {
        log.debug("Request to save ScheduleUnit: {}", scheduleUnit);
        departmentAccessService.checkAccess(scheduleUnit);
        checkScheduleConflicts(scheduleUnit);
        return scheduleUnitRepository.save(scheduleUnit);
    }

    @Transactional
    public List<ScheduleUnit> save(List<ScheduleUnit> scheduleUnitsList) {
        log.debug("Request to save the list of ScheduleUnit : {}", scheduleUnitsList);

        List<ScheduleUnit> results = new ArrayList<>();
        for(ScheduleUnit scheduleUnit : scheduleUnitsList) {
            ScheduleUnit savedScheduleUnit = save(scheduleUnit);
            results.add(savedScheduleUnit);
        }
        return results;
    }

    private void checkScheduleConflicts(ScheduleUnit scheduleUnit) {
        log.debug("Checking schedule conflicts : {}", scheduleUnit);

        Long scheduleUnitId = scheduleUnit.getId();
        ZonedDateTime startDate = scheduleUnit.getStartDate().minusMinutes(TIME_SPACE_BETWEEN_SCHEDULE_UNITS);
        ZonedDateTime endDate = scheduleUnit.getEndDate().plusMinutes(TIME_SPACE_BETWEEN_SCHEDULE_UNITS);
        Room room = scheduleUnit.getRoom();
        ClassUnit classUnit = scheduleUnit.getClassUnit();
        ClassUnitGroup classUnitGroup = classUnit.getClassUnitGroup();
        AcademicUnit academicUnit = classUnit.getAcademicUnit();
        AcademicUnitGroup academicUnitGroup = classUnit.getAcademicUnitGroup();
        UserExt user = classUnit.getUserExt();

        long conflictCount = scheduleUnitRepository.getConflictCount(scheduleUnitId, startDate, endDate, classUnitGroup,
            room, user, academicUnit, academicUnitGroup);

        if(conflictCount > 0) {
            log.error("Schedule conflict found! Breaking transaction...");
            throw new ScheduleConflictException();
        }
    }

    @Transactional(readOnly = true)
    public Optional<ScheduleUnit> findById(Long id) {
        log.debug("Request to get ScheduleUnit by id: {}", id);
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        return scheduleUnitRepository.findByIdAndClassUnit_Subdepartment_Department_Id(id, departmentId);
    }

    public void deleteById(Long id) {
        log.debug("Request to delete ScheduleUnit by id: {}", id);
        Long departmentId = userHelper.getCurrentUserDepartmentId();
        scheduleUnitRepository.deleteByIdAndClassUnit_Subdepartment_Department_Id(id, departmentId);
    }

    void deleteByClassUnitId(Long classUnitId) {
        log.debug("Request to delete ScheduleUnit by classUnitId: {}", classUnitId);
        scheduleUnitRepository.deleteByClassUnit_Id(classUnitId);
    }

    void deleteByClassUnitIdAndStartDateAfter(Long classUnitId, ZonedDateTime dateTime) {
        log.debug("Request to delete ScheduleUnit by classUnitId: {} and startDate after: {}", classUnitId, dateTime);
        scheduleUnitRepository.deleteByClassUnit_IdAndStartDateAfter(classUnitId, dateTime);
    }
}
