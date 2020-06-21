package pl.edu.agh.geotime.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.agh.geotime.domain.AcademicUnit;
import pl.edu.agh.geotime.domain.ScheduleUnit;
import pl.edu.agh.geotime.domain.Semester;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitGroup;
import pl.edu.agh.geotime.service.criteria.ScheduleUnitCriteria;
import pl.edu.agh.geotime.service.dto.ScheduleUnitInfoDTO;
import pl.edu.agh.geotime.service.query.ScheduleUnitQueryService;
import pl.edu.agh.geotime.service.util.ScheduleUnitInfoUtil;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduleUnitInfoService {

    private final ScheduleUnitQueryService scheduleUnitQueryService;
    private final SemesterService semesterService;

    @Autowired
    public ScheduleUnitInfoService(ScheduleUnitQueryService scheduleUnitQueryService, SemesterService semesterService) {
        this.scheduleUnitQueryService = scheduleUnitQueryService;
        this.semesterService = semesterService;
    }

    private ScheduleUnitCriteria createScheduleUnitCommonCriteria(ZonedDateTime startDate, ZonedDateTime endDate) {
        Optional<Semester> currentSemester = semesterService.getCurrentSemester();
        if (!currentSemester.isPresent()) {
            return null;
        }

        ScheduleUnitCriteria scheduleUnitCriteria = new ScheduleUnitCriteria();
        scheduleUnitCriteria.setSemesterIdEquals(currentSemester.get().getId());
        scheduleUnitCriteria.setStartDateGreaterOrEqualThan(startDate);
        scheduleUnitCriteria.setEndDateLessOrEqualThan(endDate);

        return scheduleUnitCriteria;
    }

    private List<ScheduleUnit> getAcademicUnitSchedule(AcademicUnit academicUnit, AcademicUnitGroup academicUnitGroup,
                                                       ZonedDateTime startDate, ZonedDateTime endDate) {
        ScheduleUnitCriteria criteria = createScheduleUnitCommonCriteria(startDate, endDate);
        if(criteria == null) {
            return Collections.emptyList();
        }

        criteria.setAcademicUnitGroupEquals(academicUnitGroup);
        criteria.setAcademicUnitIdEquals(academicUnit.getId());
        return scheduleUnitQueryService.findByCriteria(criteria);
    }

    public List<ScheduleUnitInfoDTO> getAcademicUnitScheduleInfo(AcademicUnit academicUnit, AcademicUnitGroup academicUnitGroup,
                                                                 ZonedDateTime startDate, ZonedDateTime endDate) {
        List<ScheduleUnit> academicUnitSchedule = getAcademicUnitSchedule(academicUnit, academicUnitGroup, startDate, endDate);
        return ScheduleUnitInfoUtil.combineScheduleUnitsWithSameClassUnitGroup(academicUnitSchedule);
    }
}
