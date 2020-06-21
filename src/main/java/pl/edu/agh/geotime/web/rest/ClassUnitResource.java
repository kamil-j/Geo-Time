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
import org.springframework.web.multipart.MultipartFile;
import pl.edu.agh.geotime.domain.ClassUnit;
import pl.edu.agh.geotime.service.ClassUnitService;
import pl.edu.agh.geotime.service.criteria.ClassUnitCriteria;
import pl.edu.agh.geotime.service.query.ClassUnitQueryService;
import pl.edu.agh.geotime.web.rest.dto.ClassUnitDTO;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;
import pl.edu.agh.geotime.web.rest.helper.ClassUnitResourceHelper;
import pl.edu.agh.geotime.web.rest.helper.ClassUnitUploadHelper;
import pl.edu.agh.geotime.web.rest.mapper.ClassUnitMapper;
import pl.edu.agh.geotime.web.rest.util.HeaderUtil;
import pl.edu.agh.geotime.web.rest.util.PaginationUtil;
import pl.edu.agh.geotime.web.rest.vm.ClassUnitCreateVM;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/class-units")
public class ClassUnitResource {

    private static final String ENTITY_NAME = "classUnit";

    private final Logger log = LoggerFactory.getLogger(ClassUnitResource.class);
    private final ClassUnitService classUnitService;
    private final ClassUnitMapper classUnitMapper;
    private final ClassUnitQueryService classUnitQueryService;
    private final ClassUnitResourceHelper classUnitResourceHelper;
    private final ClassUnitUploadHelper classUnitUploadHelper;

    ClassUnitResource(ClassUnitService classUnitService, ClassUnitMapper classUnitMapper,
                      ClassUnitQueryService classUnitQueryService, ClassUnitResourceHelper classUnitResourceHelper,
                      ClassUnitUploadHelper classUnitUploadHelper) {
        this.classUnitService = classUnitService;
        this.classUnitMapper = classUnitMapper;
        this.classUnitQueryService = classUnitQueryService;
        this.classUnitResourceHelper = classUnitResourceHelper;
        this.classUnitUploadHelper = classUnitUploadHelper;
    }

    @PostMapping
    @Timed
    public ResponseEntity<ClassUnitDTO> createClassUnit(@Valid @RequestBody ClassUnitCreateVM classUnitCreateVM) throws URISyntaxException {
        log.debug("REST request to save ClassUnit: {}", classUnitCreateVM);

        ClassUnit result = classUnitResourceHelper.create(classUnitCreateVM);

        return ResponseEntity.created(new URI("/api/class-units/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(classUnitMapper.toDto(result));
    }

    @PutMapping
    @Timed
    public ResponseEntity<ClassUnitDTO> updateClassUnit(@Valid @RequestBody ClassUnitDTO classUnitDTO) {
        log.debug("REST request to update ClassUnit: {}", classUnitDTO);
        if (classUnitDTO.getId() == null) {
            throw new BadRequestAlertException("An ID is required to update classUnit entity", ENTITY_NAME, "idexists");
        } else if (classUnitDTO.getSubdepartmentId() == null  && classUnitDTO.getUserId() == null) {
            throw new BadRequestAlertException("A SubdepartmentId is required if userId is not provided", "subdepartment", "notAllowedOperation");
        }

        ClassUnit result = classUnitResourceHelper.update(classUnitMapper.toEntity(classUnitDTO));
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, classUnitDTO.getId().toString()))
            .body(classUnitMapper.toDto(result));
    }

    @PutMapping("/assign")
    @Timed
    public ResponseEntity<ClassUnitDTO> assignClassUnit(@RequestParam Long classUnitId, @RequestParam Long userId) {
        log.debug("REST request to assign ClassUnit: {} to user: {}", classUnitId, userId);

        classUnitService.assign(classUnitId, userId);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, classUnitId.toString())).build();
    }

    @GetMapping
    @Timed
    public ResponseEntity<List<ClassUnitDTO>> getAllClassUnits(ClassUnitCriteria criteria, Pageable pageable) {
        log.debug("REST request to get ClassUnits by criteria: {}", criteria);
        Page<ClassUnitDTO> page = classUnitQueryService.findByCriteria(criteria, pageable)
            .map(classUnitMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/class-units");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<ClassUnitDTO> getClassUnit(@PathVariable Long id) {
        log.debug("REST request to get ClassUnit: {}", id);
        Optional<ClassUnitDTO> classUnitDTO = classUnitService.findById(id)
            .map(classUnitMapper::toDto);
        return ResponseUtil.wrapOrNotFound(classUnitDTO);
    }

    @DeleteMapping("/{id}")
    @Timed
    public ResponseEntity<Void> deleteClassUnit(@PathVariable Long id) {
        log.debug("REST request to delete ClassUnit: {}", id);
        classUnitService.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    @PostMapping("/upload")
    @Timed
    public ResponseEntity<Void> uploadFileWithClassUnits(@RequestParam MultipartFile file, @RequestParam Long subdepartmentId) throws IOException {
        log.debug("Handling upload file with class units.");

        if (file.isEmpty()) {
            log.error("Uploaded file is empty - fileName: {}", file.getName());
            throw new BadRequestAlertException("Uploaded file is empty - fileName: " + file.getName(),
                ENTITY_NAME, "fileEmpty");
        }

        classUnitUploadHelper.uploadFromFile(file, subdepartmentId);

        log.debug("File uploaded successfully");

        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert( "geoTimeApp.classUnit.uploaded", null))
            .build();
    }
}
