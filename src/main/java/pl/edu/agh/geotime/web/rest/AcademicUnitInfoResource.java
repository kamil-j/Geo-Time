package pl.edu.agh.geotime.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.geotime.service.AcademicUnitService;
import pl.edu.agh.geotime.service.criteria.AcademicUnitCriteria;
import pl.edu.agh.geotime.service.dto.AcademicUnitGroupsInfoDTO;
import pl.edu.agh.geotime.service.dto.AcademicUnitInfoDTO;
import pl.edu.agh.geotime.service.query.AcademicUnitQueryService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/academic-units-info")
public class AcademicUnitInfoResource {

    private final Logger log = LoggerFactory.getLogger(AcademicUnitInfoResource.class);
    private final AcademicUnitService academicUnitService;
    private final AcademicUnitQueryService academicUnitQueryService;

    AcademicUnitInfoResource(AcademicUnitService academicUnitService, AcademicUnitQueryService academicUnitQueryService) {
        this.academicUnitService = academicUnitService;
        this.academicUnitQueryService = academicUnitQueryService;
    }

    @GetMapping
    @Timed
    public ResponseEntity<List<AcademicUnitInfoDTO>> getAllAcademicUnitsInfo(AcademicUnitCriteria criteria) {
        log.debug("REST request to get info about all academicUnits by criteria: {}", criteria);
        List<AcademicUnitInfoDTO> allAcademicUnitsInfo = academicUnitQueryService.findByCriteria(criteria);
        return new ResponseEntity<>(allAcademicUnitsInfo, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<AcademicUnitInfoDTO> getAcademicUnitInfo(@PathVariable Long id) {
        log.debug("REST request to get academicUnitInfo : {}", id);
        Optional<AcademicUnitInfoDTO> academicUnitInfo = academicUnitService.getAcademicUnitInfo(id);
        return ResponseUtil.wrapOrNotFound(academicUnitInfo);
    }

    @GetMapping("/{id}/groups")
    @Timed
    public ResponseEntity<AcademicUnitGroupsInfoDTO> getAcademicUnitGroupsInfo(@PathVariable Long id) {
        log.debug("REST request to get academicUnitGroupsInfo : {}", id);
        Optional<AcademicUnitGroupsInfoDTO> academicUnitGroupsInfo = academicUnitService.getAcademicUnitGroupsInfo(id);
        return ResponseUtil.wrapOrNotFound(academicUnitGroupsInfo);
    }
}
