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
import pl.edu.agh.geotime.domain.SchedulingTimeFrame;
import pl.edu.agh.geotime.service.SchedulingTimeFrameInfoService;
import pl.edu.agh.geotime.service.SchedulingTimeFrameService;
import pl.edu.agh.geotime.web.rest.dto.SchedulingTimeFrameDTO;
import pl.edu.agh.geotime.web.rest.mapper.SchedulingTimeFrameMapper;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;
import pl.edu.agh.geotime.web.rest.util.HeaderUtil;
import pl.edu.agh.geotime.web.rest.util.PaginationUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/scheduling-time-frames")
public class SchedulingTimeFrameResource {

    private static final String ENTITY_NAME = "schedulingTimeFrame";

    private final Logger log = LoggerFactory.getLogger(SchedulingTimeFrameResource.class);
    private final SchedulingTimeFrameService schedulingTimeFrameService;
    private final SchedulingTimeFrameMapper schedulingTimeFrameMapper;
    private final SchedulingTimeFrameInfoService schedulingTimeFrameInfoService;

    SchedulingTimeFrameResource(SchedulingTimeFrameService schedulingTimeFrameService,
                                SchedulingTimeFrameMapper schedulingTimeFrameMapper,
                                SchedulingTimeFrameInfoService schedulingTimeFrameInfoService) {
        this.schedulingTimeFrameService = schedulingTimeFrameService;
        this.schedulingTimeFrameMapper = schedulingTimeFrameMapper;
        this.schedulingTimeFrameInfoService = schedulingTimeFrameInfoService;
    }

    @PostMapping
    @Timed
    public ResponseEntity<SchedulingTimeFrameDTO> createSchedulingTimeFrame(@Valid @RequestBody SchedulingTimeFrameDTO schedulingTimeFrameDTO) throws URISyntaxException {
        log.debug("REST request to save SchedulingTimeFrame: {}", schedulingTimeFrameDTO);
        if (schedulingTimeFrameDTO.getId() != null) {
            throw new BadRequestAlertException("A new schedulingTimeFrame cannot already have an ID", ENTITY_NAME, "idexists");
        }

        SchedulingTimeFrame result = schedulingTimeFrameService.save(schedulingTimeFrameMapper.toEntity(schedulingTimeFrameDTO));
        return ResponseEntity.created(new URI("/api/scheduling-time-frames/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(schedulingTimeFrameMapper.toDto(result));
    }

    @PutMapping
    @Timed
    public ResponseEntity<SchedulingTimeFrameDTO> updateSchedulingTimeFrame(@Valid @RequestBody SchedulingTimeFrameDTO schedulingTimeFrameDTO) throws URISyntaxException {
        log.debug("REST request to update SchedulingTimeFrame : {}", schedulingTimeFrameDTO);
        if (schedulingTimeFrameDTO.getId() == null) {
            return createSchedulingTimeFrame(schedulingTimeFrameDTO);
        }

        SchedulingTimeFrame result = schedulingTimeFrameService.save(schedulingTimeFrameMapper.toEntity(schedulingTimeFrameDTO));
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, result.getId().toString()))
            .body(schedulingTimeFrameMapper.toDto(result));
    }

    @GetMapping
    @Timed
    public ResponseEntity<List<SchedulingTimeFrameDTO>> getAllSchedulingTimeFrames(Pageable pageable) {
        log.debug("REST request to get a page of SchedulingTimeFrames");
        Page<SchedulingTimeFrameDTO> page = schedulingTimeFrameService.findAll(pageable)
            .map(schedulingTimeFrameMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/scheduling-time-frames");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<SchedulingTimeFrameDTO> getSchedulingTimeFrame(@PathVariable Long id) {
        log.debug("REST request to get SchedulingTimeFrame : {}", id);
        Optional<SchedulingTimeFrameDTO> schedulingTimeFrameDTO = schedulingTimeFrameService.findById(id)
            .map(schedulingTimeFrameMapper::toDto);
        return ResponseUtil.wrapOrNotFound(schedulingTimeFrameDTO);
    }

    @GetMapping("/user/{userId}")
    @Timed
    public ResponseEntity<SchedulingTimeFrameDTO> getUserSchedulingTimeFrame(@PathVariable Long userId) {
        log.debug("REST request to get currentUser SchedulingTimeFrame");
        Optional<SchedulingTimeFrameDTO> schedulingTimeFrame = schedulingTimeFrameInfoService.getUserSchedulingTimeFrames(userId)
            .map(schedulingTimeFrameMapper::toDto);
        return ResponseUtil.wrapOrNotFound(schedulingTimeFrame);
    }

    @DeleteMapping("/{id}")
    @Timed
    public ResponseEntity<Void> deleteSchedulingTimeFrame(@PathVariable Long id) {
        log.debug("REST request to delete SchedulingTimeFrame : {}", id);
        schedulingTimeFrameService.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
