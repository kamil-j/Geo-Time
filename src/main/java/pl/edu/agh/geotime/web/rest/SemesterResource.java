package pl.edu.agh.geotime.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.geotime.domain.Semester;
import pl.edu.agh.geotime.service.SemesterService;
import pl.edu.agh.geotime.web.rest.dto.SemesterDTO;
import pl.edu.agh.geotime.web.rest.mapper.SemesterMapper;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;
import pl.edu.agh.geotime.web.rest.util.HeaderUtil;
import pl.edu.agh.geotime.web.rest.util.PaginationUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Semester.
 */
@RestController
@RequestMapping("/api/semesters")
public class SemesterResource {

    private static final String ENTITY_NAME = "semester";

    private final Logger log = LoggerFactory.getLogger(SemesterResource.class);
    private final SemesterService semesterService;
    private final SemesterMapper semesterMapper;

    SemesterResource(SemesterService semesterService, SemesterMapper semesterMapper) {
        this.semesterService = semesterService;
        this.semesterMapper = semesterMapper;
    }

    @PostMapping
    @Timed
    public ResponseEntity<SemesterDTO> createSemester(@Valid @RequestBody SemesterDTO semesterDTO) throws URISyntaxException {
        log.debug("REST request to save Semester: {}", semesterDTO);
        if (semesterDTO.getId() != null) {
            throw new BadRequestAlertException("A new semester cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Semester result = semesterService.save(semesterMapper.toEntity(semesterDTO));
        return ResponseEntity.created(new URI("/api/semesters/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(semesterMapper.toDto(result));
    }

    @PutMapping
    @Timed
    public ResponseEntity<SemesterDTO> updateSemester(@Valid @RequestBody SemesterDTO semesterDTO) throws URISyntaxException {
        log.debug("REST request to update Semester: {}", semesterDTO);
        if (semesterDTO.getId() == null) {
            return createSemester(semesterDTO);
        }
        Semester result = semesterService.save(semesterMapper.toEntity(semesterDTO));
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, result.getId().toString()))
            .body(semesterMapper.toDto(result));
    }

    @GetMapping
    @Timed
    public ResponseEntity<List<SemesterDTO>> getAllSemesters(Pageable pageable) {
        log.debug("REST request to get a page of Semesters");
        Page<SemesterDTO> page = semesterService.findAll(pageable)
            .map(semesterMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/semesters");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<SemesterDTO> getSemester(@PathVariable Long id) {
        log.debug("REST request to get Semester: {}", id);
        Optional<SemesterDTO> semester = semesterService.findById(id)
            .map(semesterMapper::toDto);
        return ResponseUtil.wrapOrNotFound(semester);
    }

    @DeleteMapping("/{id}")
    @Timed
    public ResponseEntity<Void> deleteSemester(@PathVariable Long id) {
        log.debug("REST request to delete Semester: {}", id);
        semesterService.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
