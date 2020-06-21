package pl.edu.agh.geotime.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.geotime.domain.Semester;
import pl.edu.agh.geotime.domain.UserExt;
import pl.edu.agh.geotime.service.ClassUnitService;
import pl.edu.agh.geotime.service.SemesterService;
import pl.edu.agh.geotime.service.helper.UserHelper;
import pl.edu.agh.geotime.web.rest.dto.ext.ClassUnitExtDTO;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;
import pl.edu.agh.geotime.web.rest.mapper.ClassUnitExtMapper;
import pl.edu.agh.geotime.web.rest.util.PaginationUtil;

import java.util.List;

@RestController
@RequestMapping("/api/timetable")
public class TimetableResource {

    private final Logger log = LoggerFactory.getLogger(TimetableResource.class);
    private final ClassUnitService classUnitService;
    private final ClassUnitExtMapper classUnitExtMapper;
    private final SemesterService semesterService;
    private final UserHelper userHelper;

    TimetableResource(ClassUnitService classUnitService, ClassUnitExtMapper classUnitExtMapper, SemesterService semesterService,
                      UserHelper userHelper) {
        this.classUnitService = classUnitService;
        this.classUnitExtMapper = classUnitExtMapper;
        this.semesterService = semesterService;
        this.userHelper = userHelper;
    }

    @GetMapping("/classes")
    @Timed
    public ResponseEntity<List<ClassUnitExtDTO>> getUserNotBookedClasses(Pageable pageable, @RequestParam Long userId) {
        log.debug("REST request to get info about current user classes");
        UserExt user = userHelper.getActionUser(userId);

        Semester currentSemester = semesterService.getCurrentSemester().orElseThrow(
            () -> new BadRequestAlertException("Current semester not found!", "semester", "notexists")
        );
        Page<ClassUnitExtDTO> page = classUnitService.findNotBookedByUserAndSemester(pageable, user, currentSemester)
            .map(classUnitExtMapper::toDto);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/timetable/classes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
