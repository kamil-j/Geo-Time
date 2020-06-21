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
import pl.edu.agh.geotime.domain.ClassUnitGroup;
import pl.edu.agh.geotime.service.ClassUnitGroupService;
import pl.edu.agh.geotime.web.rest.dto.ClassUnitGroupDTO;
import pl.edu.agh.geotime.web.rest.mapper.ClassUnitGroupMapper;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;
import pl.edu.agh.geotime.web.rest.util.HeaderUtil;
import pl.edu.agh.geotime.web.rest.util.PaginationUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing ClassUnitGroup.
 */
@RestController
@RequestMapping("/api/class-unit-groups")
public class ClassUnitGroupResource {

    private static final String ENTITY_NAME = "classUnitGroup";

    private final Logger log = LoggerFactory.getLogger(ClassUnitGroupResource.class);
    private final ClassUnitGroupService classUnitGroupService;
    private final ClassUnitGroupMapper classUnitGroupMapper;

    ClassUnitGroupResource(ClassUnitGroupService classUnitGroupService, ClassUnitGroupMapper classUnitGroupMapper) {
        this.classUnitGroupService = classUnitGroupService;
        this.classUnitGroupMapper = classUnitGroupMapper;
    }

    @PostMapping
    @Timed
    public ResponseEntity<ClassUnitGroupDTO> createClassUnitGroup(@Valid @RequestBody ClassUnitGroupDTO classUnitGroupDTO) throws URISyntaxException {
        log.debug("REST request to save ClassUnitGroup: {}", classUnitGroupDTO);
        if (classUnitGroupDTO.getId() != null) {
            throw new BadRequestAlertException("A new classUnitGroup cannot already have an ID", ENTITY_NAME, "idexists");
        }

        ClassUnitGroup result = classUnitGroupService.save(classUnitGroupMapper.toEntity(classUnitGroupDTO));
        return ResponseEntity.created(new URI("/api/class-unit-groups/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(classUnitGroupMapper.toDto(result));
    }

    @PutMapping
    @Timed
    public ResponseEntity<ClassUnitGroupDTO> updateClassUnitGroup(@Valid @RequestBody ClassUnitGroupDTO classUnitGroupDTO) throws URISyntaxException {
        log.debug("REST request to update ClassUnitGroup: {}", classUnitGroupDTO);
        if (classUnitGroupDTO.getId() == null) {
            return createClassUnitGroup(classUnitGroupDTO);
        }

        ClassUnitGroup result = classUnitGroupService.save(classUnitGroupMapper.toEntity(classUnitGroupDTO));
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, result.getId().toString()))
            .body(classUnitGroupMapper.toDto(result));
    }

    @GetMapping
    @Timed
    public ResponseEntity<List<ClassUnitGroupDTO>> getAllClassUnitGroups(Pageable pageable) {
        log.debug("REST request to get a page of ClassUnitGroups");
        Page<ClassUnitGroupDTO> page = classUnitGroupService.findAll(pageable)
            .map(classUnitGroupMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/class-unit-groups");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<ClassUnitGroupDTO> getClassUnitGroup(@PathVariable Long id) {
        log.debug("REST request to get ClassUnitGroup : {}", id);
        Optional<ClassUnitGroupDTO> classUnitGroupDTO = classUnitGroupService.findById(id)
            .map(classUnitGroupMapper::toDto);
        return ResponseUtil.wrapOrNotFound(classUnitGroupDTO);
    }

    @DeleteMapping("/{id}")
    @Timed
    public ResponseEntity<Void> deleteClassUnitGroup(@PathVariable Long id) {
        log.debug("REST request to delete ClassUnitGroup : {}", id);
        classUnitGroupService.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
