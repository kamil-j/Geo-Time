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
import pl.edu.agh.geotime.domain.StudyField;
import pl.edu.agh.geotime.service.StudyFieldService;
import pl.edu.agh.geotime.web.rest.dto.StudyFieldDTO;
import pl.edu.agh.geotime.web.rest.mapper.StudyFieldMapper;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;
import pl.edu.agh.geotime.web.rest.util.HeaderUtil;
import pl.edu.agh.geotime.web.rest.util.PaginationUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing StudyField.
 */
@RestController
@RequestMapping("/api/study-fields")
public class StudyFieldResource {

    private static final String ENTITY_NAME = "studyField";

    private final Logger log = LoggerFactory.getLogger(StudyFieldResource.class);
    private final StudyFieldService studyFieldService;
    private final StudyFieldMapper studyFieldMapper;

    StudyFieldResource(StudyFieldService studyFieldService, StudyFieldMapper studyFieldMapper) {
        this.studyFieldService = studyFieldService;
        this.studyFieldMapper = studyFieldMapper;
    }

    @PostMapping
    @Timed
    public ResponseEntity<StudyFieldDTO> createStudyField(@Valid @RequestBody StudyFieldDTO studyFieldDTO) throws URISyntaxException {
        log.debug("REST request to save StudyField: {}", studyFieldDTO);
        if (studyFieldDTO.getId() != null) {
            throw new BadRequestAlertException("A new studyField cannot already have an ID", ENTITY_NAME, "noaccess");
        }

        StudyField result = studyFieldService.save(studyFieldMapper.toEntity(studyFieldDTO));
        return ResponseEntity.created(new URI("/api/study-fields/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(studyFieldMapper.toDto(result));
    }

    @PutMapping
    @Timed
    public ResponseEntity<StudyFieldDTO> updateStudyField(@Valid @RequestBody StudyFieldDTO studyFieldDTO) throws URISyntaxException {
        log.debug("REST request to update StudyField : {}", studyFieldDTO);
        if (studyFieldDTO.getId() == null) {
            return createStudyField(studyFieldDTO);
        }

        StudyField result = studyFieldService.save(studyFieldMapper.toEntity(studyFieldDTO));
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, result.getId().toString()))
            .body(studyFieldMapper.toDto(result));
    }

    @GetMapping
    @Timed
    public ResponseEntity<List<StudyFieldDTO>> getAllStudyFields(Pageable pageable) {
        log.debug("REST request to get a page of StudyFields");
        Page<StudyFieldDTO> page = studyFieldService.findAll(pageable)
            .map(studyFieldMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/study-fields");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<StudyFieldDTO> getStudyField(@PathVariable Long id) {
        log.debug("REST request to get StudyField: {}", id);
        Optional<StudyFieldDTO> studyFieldDTO = studyFieldService.findById(id)
            .map(studyFieldMapper::toDto);
        return ResponseUtil.wrapOrNotFound(studyFieldDTO);
    }

    @DeleteMapping("/{id}")
    @Timed
    public ResponseEntity<Void> deleteStudyField(@PathVariable Long id) {
        log.debug("REST request to delete StudyField: {}", id);
        studyFieldService.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
