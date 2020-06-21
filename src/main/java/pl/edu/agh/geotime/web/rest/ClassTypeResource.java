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
import pl.edu.agh.geotime.domain.ClassType;
import pl.edu.agh.geotime.service.ClassTypeService;
import pl.edu.agh.geotime.web.rest.dto.ClassTypeDTO;
import pl.edu.agh.geotime.web.rest.mapper.ClassTypeMapper;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;
import pl.edu.agh.geotime.web.rest.util.HeaderUtil;
import pl.edu.agh.geotime.web.rest.util.PaginationUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/class-types")
public class ClassTypeResource {

    private final Logger log = LoggerFactory.getLogger(ClassTypeResource.class);

    private static final String ENTITY_NAME = "classType";

    private final ClassTypeService classTypeService;
    private final ClassTypeMapper classTypeMapper;

    ClassTypeResource(ClassTypeService classTypeService, ClassTypeMapper classTypeMapper) {
        this.classTypeService = classTypeService;
        this.classTypeMapper = classTypeMapper;
    }

    @PostMapping
    @Timed
    public ResponseEntity<ClassTypeDTO> createClassType(@Valid @RequestBody ClassTypeDTO classTypeDTO) throws URISyntaxException {
        log.debug("REST request to save ClassType : {}", classTypeDTO);
        if (classTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new classType cannot already have an ID", ENTITY_NAME, "idexists");
        }

        ClassType result = classTypeService.save(classTypeMapper.toEntity(classTypeDTO));
        return ResponseEntity.created(new URI("/api/class-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(classTypeMapper.toDto(result));
    }

    @PutMapping
    @Timed
    public ResponseEntity<ClassTypeDTO> updateClassType(@Valid @RequestBody ClassTypeDTO classTypeDTO) throws URISyntaxException {
        log.debug("REST request to update ClassType : {}", classTypeDTO);
        if (classTypeDTO.getId() == null) {
            return createClassType(classTypeDTO);
        }

        ClassType result = classTypeService.save(classTypeMapper.toEntity(classTypeDTO));
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, result.getId().toString()))
            .body(classTypeMapper.toDto(result));
    }

    @GetMapping
    @Timed
    public ResponseEntity<List<ClassTypeDTO>> getAllClassTypes(Pageable pageable) {
        log.debug("REST request to get a page of ClassTypes");
        Page<ClassTypeDTO> page = classTypeService.findAll(pageable)
            .map(classTypeMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/class-types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<ClassTypeDTO> getClassType(@PathVariable Long id) {
        log.debug("REST request to get ClassType : {}", id);
        Optional<ClassTypeDTO> classTypeDTO = classTypeService.findById(id)
            .map(classTypeMapper::toDto);
        return ResponseUtil.wrapOrNotFound(classTypeDTO);
    }

    @DeleteMapping("/{id}")
    @Timed
    public ResponseEntity<Void> deleteClassType(@PathVariable Long id) {
        log.debug("REST request to delete ClassType : {}", id);
        classTypeService.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
