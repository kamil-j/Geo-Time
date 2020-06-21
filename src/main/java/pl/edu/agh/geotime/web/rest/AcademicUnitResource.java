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
import pl.edu.agh.geotime.domain.AcademicUnit;
import pl.edu.agh.geotime.service.AcademicUnitService;
import pl.edu.agh.geotime.web.rest.dto.AcademicUnitDTO;
import pl.edu.agh.geotime.web.rest.mapper.AcademicUnitMapper;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;
import pl.edu.agh.geotime.web.rest.util.HeaderUtil;
import pl.edu.agh.geotime.web.rest.util.PaginationUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/academic-units")
public class AcademicUnitResource {

    private static final String ENTITY_NAME = "academicUnit";

    private final Logger log = LoggerFactory.getLogger(AcademicUnitResource.class);
    private final AcademicUnitService academicUnitService;
    private final AcademicUnitMapper academicUnitMapper;

    AcademicUnitResource(AcademicUnitService academicUnitService, AcademicUnitMapper academicUnitMapper) {
        this.academicUnitService = academicUnitService;
        this.academicUnitMapper = academicUnitMapper;
    }

    @PostMapping
    @Timed
    public ResponseEntity<AcademicUnitDTO> createAcademicUnit(@Valid @RequestBody AcademicUnitDTO academicUnitDTO) throws URISyntaxException {
        log.debug("REST request to save AcademicUnit : {}", academicUnitDTO);
        if (academicUnitDTO.getId() != null) {
            throw new BadRequestAlertException("A new academicUnit cannot already have an ID", ENTITY_NAME, "idexists");
        }

        AcademicUnit result = academicUnitService.save(academicUnitMapper.toEntity(academicUnitDTO));
        return ResponseEntity.created(new URI("/api/academic-units/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(academicUnitMapper.toDto(result));
    }

    @PutMapping
    @Timed
    public ResponseEntity<AcademicUnitDTO> updateAcademicUnit(@Valid @RequestBody AcademicUnitDTO academicUnitDTO) throws URISyntaxException {
        log.debug("REST request to update AcademicUnit: {}", academicUnitDTO);
        if (academicUnitDTO.getId() == null) {
            return createAcademicUnit(academicUnitDTO);
        }

        AcademicUnit result = academicUnitService.save(academicUnitMapper.toEntity(academicUnitDTO));
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, result.getId().toString()))
            .body(academicUnitMapper.toDto(result));
    }

    @GetMapping
    @Timed
    public ResponseEntity<List<AcademicUnitDTO>> getAllAcademicUnits(Pageable pageable) {
        log.debug("REST request to get a page of AcademicUnits");
        Page<AcademicUnitDTO> page = academicUnitService.findAll(pageable)
            .map(academicUnitMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/academic-units");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<AcademicUnitDTO> getAcademicUnit(@PathVariable Long id) {
        log.debug("REST request to get AcademicUnit : {}", id);
        Optional<AcademicUnitDTO> academicUnitDTO = academicUnitService.findById(id)
            .map(academicUnitMapper::toDto);
        return ResponseUtil.wrapOrNotFound(academicUnitDTO);
    }

    @DeleteMapping("/{id}")
    @Timed
    public ResponseEntity<Void> deleteAcademicUnit(@PathVariable Long id) {
        log.debug("REST request to delete AcademicUnit : {}", id);
        academicUnitService.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
