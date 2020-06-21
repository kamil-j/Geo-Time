package pl.edu.agh.geotime.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.geotime.service.SemesterService;
import pl.edu.agh.geotime.web.rest.dto.SemesterInfoDTO;

@RestController
@RequestMapping("/api/semester-info")
public class SemesterInfoResource {

    private final Logger log = LoggerFactory.getLogger(SemesterInfoResource.class);
    private final SemesterService semesterService;

    SemesterInfoResource(SemesterService semesterService) {
        this.semesterService = semesterService;
    }

    @GetMapping("/current")
    @Timed
    public ResponseEntity<SemesterInfoDTO> getInfoAboutCurrentSemester() {
        log.debug("REST request to get info about current semester");
        SemesterInfoDTO currentSemester = semesterService.getCurrentSemester()
            .map(SemesterInfoDTO::new)
            .orElse(null);

        return new ResponseEntity<>(currentSemester, HttpStatus.OK);
    }
}
