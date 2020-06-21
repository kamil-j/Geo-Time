package pl.edu.agh.geotime.web.rest;

import com.codahale.metrics.annotation.Timed;
import pl.edu.agh.geotime.domain.Subdepartment;
import pl.edu.agh.geotime.service.SubdepartmentService;
import pl.edu.agh.geotime.web.rest.mapper.SubdepartmentMapper;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;
import pl.edu.agh.geotime.web.rest.util.HeaderUtil;
import pl.edu.agh.geotime.web.rest.util.PaginationUtil;
import pl.edu.agh.geotime.web.rest.dto.SubdepartmentDTO;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Subdepartment.
 */
@RestController
@RequestMapping("/api/subdepartments")
public class SubdepartmentResource {

    private static final String ENTITY_NAME = "subdepartment";

    private final Logger log = LoggerFactory.getLogger(SubdepartmentResource.class);
    private final SubdepartmentService subdepartmentService;
    private final SubdepartmentMapper subdepartmentMapper;

    SubdepartmentResource(SubdepartmentService subdepartmentService, SubdepartmentMapper subdepartmentMapper) {
        this.subdepartmentService = subdepartmentService;
        this.subdepartmentMapper = subdepartmentMapper;
    }

    @PostMapping
    @Timed
    public ResponseEntity<SubdepartmentDTO> createSubdepartment(@Valid @RequestBody SubdepartmentDTO subdepartmentDTO) throws URISyntaxException {
        log.debug("REST request to save Subdepartment : {}", subdepartmentDTO);
        if (subdepartmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new subdepartment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Subdepartment result = subdepartmentService.save(subdepartmentMapper.toEntity(subdepartmentDTO));
        return ResponseEntity.created(new URI("/api/subdepartments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(subdepartmentMapper.toDto(result));
    }

    @PutMapping
    @Timed
    public ResponseEntity<SubdepartmentDTO> updateSubdepartment(@Valid @RequestBody SubdepartmentDTO subdepartmentDTO) throws URISyntaxException {
        log.debug("REST request to update Subdepartment : {}", subdepartmentDTO);
        if (subdepartmentDTO.getId() == null) {
            return createSubdepartment(subdepartmentDTO);
        }
        Subdepartment result = subdepartmentService.save(subdepartmentMapper.toEntity(subdepartmentDTO));
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, subdepartmentDTO.getId().toString()))
            .body(subdepartmentMapper.toDto(result));
    }

    @GetMapping
    @Timed
    public ResponseEntity<List<SubdepartmentDTO>> getAllSubdepartments(Pageable pageable) {
        log.debug("REST request to get a page of Subdepartments");
        Page<SubdepartmentDTO> page = subdepartmentService.findAll(pageable)
            .map(subdepartmentMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/subdepartments");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<SubdepartmentDTO> getSubdepartment(@PathVariable Long id) {
        log.debug("REST request to get Subdepartment : {}", id);
        Optional<SubdepartmentDTO> subdepartmentDTO = subdepartmentService.findById(id)
            .map(subdepartmentMapper::toDto);
        return ResponseUtil.wrapOrNotFound(subdepartmentDTO);
    }

    @DeleteMapping("/{id}")
    @Timed
    public ResponseEntity<Void> deleteSubdepartment(@PathVariable Long id) {
        log.debug("REST request to delete Subdepartment : {}", id);
        subdepartmentService.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
