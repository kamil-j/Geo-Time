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
import pl.edu.agh.geotime.domain.ScheduleUnit;
import pl.edu.agh.geotime.service.ScheduleUnitService;
import pl.edu.agh.geotime.service.criteria.ScheduleUnitCriteria;
import pl.edu.agh.geotime.web.rest.dto.ScheduleUnitDTO;
import pl.edu.agh.geotime.web.rest.mapper.ScheduleUnitMapper;
import pl.edu.agh.geotime.service.query.ScheduleUnitQueryService;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;
import pl.edu.agh.geotime.web.rest.util.HeaderUtil;
import pl.edu.agh.geotime.web.rest.util.PaginationUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing ScheduleUnit.
 */
@RestController
@RequestMapping("/api/schedule-units")
public class ScheduleUnitResource {

    private static final String ENTITY_NAME = "scheduleUnit";

    private final Logger log = LoggerFactory.getLogger(ScheduleUnitResource.class);
    private final ScheduleUnitService scheduleUnitService;
    private final ScheduleUnitMapper scheduleUnitMapper;
    private final ScheduleUnitQueryService scheduleUnitQueryService;

    ScheduleUnitResource(ScheduleUnitService scheduleUnitService, ScheduleUnitMapper scheduleUnitMapper,
                         ScheduleUnitQueryService scheduleUnitQueryService) {
        this.scheduleUnitService = scheduleUnitService;
        this.scheduleUnitMapper = scheduleUnitMapper;
        this.scheduleUnitQueryService = scheduleUnitQueryService;
    }

    @PostMapping
    @Timed
    public ResponseEntity<ScheduleUnitDTO> createScheduleUnit(@Valid @RequestBody ScheduleUnitDTO scheduleUnitDTO) throws URISyntaxException {
        log.debug("REST request to save ScheduleUnit: {}", scheduleUnitDTO);
        if (scheduleUnitDTO.getId() != null) {
            throw new BadRequestAlertException("A new scheduleUnit cannot already have an ID", ENTITY_NAME, "idexists");
        }

        ScheduleUnit result = scheduleUnitService.save(scheduleUnitMapper.toEntity(scheduleUnitDTO));
        return ResponseEntity.created(new URI("/api/schedule-units/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(scheduleUnitMapper.toDto(result));
    }

    @PutMapping
    @Timed
    public ResponseEntity<ScheduleUnitDTO> updateScheduleUnit(@Valid @RequestBody ScheduleUnitDTO scheduleUnitDTO) throws URISyntaxException {
        log.debug("REST request to update ScheduleUnit: {}", scheduleUnitDTO);
        if (scheduleUnitDTO.getId() == null) {
            return createScheduleUnit(scheduleUnitDTO);
        }

        ScheduleUnit result = scheduleUnitService.save(scheduleUnitMapper.toEntity(scheduleUnitDTO));
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, result.getId().toString()))
            .body(scheduleUnitMapper.toDto(result));
    }

    @GetMapping
    @Timed
    public ResponseEntity<List<ScheduleUnitDTO>> getAllScheduleUnits(ScheduleUnitCriteria criteria, Pageable pageable) {
        log.debug("REST request to get ScheduleUnits by criteria: {}", criteria);
        Page<ScheduleUnitDTO> page = scheduleUnitQueryService.findByCriteria(criteria, pageable)
            .map(scheduleUnitMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/schedule-units");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<ScheduleUnitDTO> getScheduleUnit(@PathVariable Long id) {
        log.debug("REST request to get ScheduleUnit : {}", id);
        Optional<ScheduleUnitDTO> scheduleUnitDTO = scheduleUnitService.findById(id)
            .map(scheduleUnitMapper::toDto);
        return ResponseUtil.wrapOrNotFound(scheduleUnitDTO);
    }

    @DeleteMapping("/{id}")
    @Timed
    public ResponseEntity<Void> deleteScheduleUnit(@PathVariable Long id) {
        log.debug("REST request to delete ScheduleUnit : {}", id);
        scheduleUnitService.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
