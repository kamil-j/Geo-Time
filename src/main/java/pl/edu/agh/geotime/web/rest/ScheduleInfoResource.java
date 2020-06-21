package pl.edu.agh.geotime.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.geotime.domain.AcademicUnit;
import pl.edu.agh.geotime.domain.enumeration.AcademicUnitGroup;
import pl.edu.agh.geotime.repository.AcademicUnitRepository;
import pl.edu.agh.geotime.service.ScheduleUnitInfoService;
import pl.edu.agh.geotime.service.dto.ScheduleUnitInfoDTO;
import pl.edu.agh.geotime.service.util.DateUtil;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * REST controller for managing schedule information.
 */
@RestController
@RequestMapping("/api/schedule-info")
public class ScheduleInfoResource {

    private final Logger log = LoggerFactory.getLogger(ScheduleInfoResource.class);
    private final ScheduleUnitInfoService scheduleUnitInfoService;
    private final AcademicUnitRepository academicUnitRepository;

    ScheduleInfoResource(ScheduleUnitInfoService scheduleUnitInfoService, AcademicUnitRepository academicUnitRepository) {
        this.scheduleUnitInfoService = scheduleUnitInfoService;
        this.academicUnitRepository = academicUnitRepository;
    }

    @GetMapping("/{academicUnitId}")
    @Timed
    public ResponseEntity<List<ScheduleUnitInfoDTO>> getInfoAboutSchedule(@PathVariable Long academicUnitId,
                                                                          @RequestParam(required = false) AcademicUnitGroup academicUnitGroup,
                                                                          @RequestParam(required = false) String startDate,
                                                                          @RequestParam(required = false) String endDate) {
        log.debug("REST request to get info about schedule - startDate : {}, endDate : {}", startDate, endDate);
        if(academicUnitId == null) {
            throw new BadRequestAlertException("A request for Schedule contains not allowed parameters! (academicUnitId == null)", "AcademicUnit", "notallowed");
        }

        AcademicUnit academicUnit = academicUnitRepository.findOneById(academicUnitId)
            .orElseThrow(() -> new BadRequestAlertException("Entity AcademicUnit with ID: " + academicUnitId + " not exists!", "AcademicUnit", "notexists"));

        ZonedDateTime startDateTime = DateUtil.convertToZonedDateTime(startDate);
        ZonedDateTime endDateTime = DateUtil.convertToZonedDateTime(endDate);

        List<ScheduleUnitInfoDTO> scheduleUnitInfo = scheduleUnitInfoService.getAcademicUnitScheduleInfo(academicUnit,
            academicUnitGroup, startDateTime, endDateTime);
        return new ResponseEntity<>(scheduleUnitInfo, HttpStatus.OK);
    }
}
