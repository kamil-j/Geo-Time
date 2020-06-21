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
import pl.edu.agh.geotime.domain.Department;
import pl.edu.agh.geotime.service.DepartmentService;
import pl.edu.agh.geotime.web.rest.dto.DepartmentDTO;
import pl.edu.agh.geotime.web.rest.mapper.DepartmentMapper;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;
import pl.edu.agh.geotime.web.rest.util.HeaderUtil;
import pl.edu.agh.geotime.web.rest.util.PaginationUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Department.
 */
@RestController
@RequestMapping("/api/departments")
public class DepartmentResource {

    private static final String ENTITY_NAME = "department";

    private final Logger log = LoggerFactory.getLogger(DepartmentResource.class);
    private final DepartmentService departmentService;
    private final DepartmentMapper departmentMapper;

    DepartmentResource(DepartmentService departmentService, DepartmentMapper departmentMapper) {
        this.departmentService = departmentService;
        this.departmentMapper = departmentMapper;
    }

    @PostMapping
    @Timed
    public ResponseEntity<DepartmentDTO> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) throws URISyntaxException {
        log.debug("REST request to save Department: {}", departmentDTO);
        if (departmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new department cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Department result = departmentService.save(departmentMapper.toEntity(departmentDTO));
        return ResponseEntity.created(new URI("/api/departments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(departmentMapper.toDto(result));
    }

    @PutMapping
    @Timed
    public ResponseEntity<DepartmentDTO> updateDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) throws URISyntaxException {
        log.debug("REST request to update Department: {}", departmentDTO);
        if (departmentDTO.getId() == null) {
            return createDepartment(departmentDTO);
        }
        Department result = departmentService.save(departmentMapper.toEntity(departmentDTO));
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, result.getId().toString()))
            .body(departmentMapper.toDto(result));
    }

    @GetMapping
    @Timed
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments(Pageable pageable) {
        log.debug("REST request to get a page of Departments");
        Page<DepartmentDTO> page = departmentService.findAll(pageable)
            .map(departmentMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/departments");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<DepartmentDTO> getDepartment(@PathVariable Long id) {
        log.debug("REST request to get Department : {}", id);
        Optional<DepartmentDTO> department = departmentService.findById(id)
            .map(departmentMapper::toDto);
        return ResponseUtil.wrapOrNotFound(department);
    }

    @DeleteMapping("/{id}")
    @Timed
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        log.debug("REST request to delete Department : {}", id);
        departmentService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
