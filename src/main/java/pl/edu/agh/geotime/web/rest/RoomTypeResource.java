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
import pl.edu.agh.geotime.domain.RoomType;
import pl.edu.agh.geotime.service.RoomTypeService;
import pl.edu.agh.geotime.web.rest.dto.RoomTypeDTO;
import pl.edu.agh.geotime.web.rest.mapper.RoomTypeMapper;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;
import pl.edu.agh.geotime.web.rest.util.HeaderUtil;
import pl.edu.agh.geotime.web.rest.util.PaginationUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing RoomType.
 */
@RestController
@RequestMapping("/api/room-types")
public class RoomTypeResource {

    private static final String ENTITY_NAME = "roomType";

    private final Logger log = LoggerFactory.getLogger(RoomTypeResource.class);
    private final RoomTypeService roomTypeService;
    private final RoomTypeMapper roomTypeMapper;

    RoomTypeResource(RoomTypeService roomTypeService, RoomTypeMapper roomTypeMapper) {
        this.roomTypeService = roomTypeService;
        this.roomTypeMapper = roomTypeMapper;
    }

    @PostMapping
    @Timed
    public ResponseEntity<RoomTypeDTO> createRoomType(@Valid @RequestBody RoomTypeDTO roomTypeDTO) throws URISyntaxException {
        log.debug("REST request to save RoomType: {}", roomTypeDTO);
        if (roomTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new roomType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RoomType result = roomTypeService.save(roomTypeMapper.toEntity(roomTypeDTO));
        return ResponseEntity.created(new URI("/api/room-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(roomTypeMapper.toDto(result));
    }

    @PutMapping
    @Timed
    public ResponseEntity<RoomTypeDTO> updateRoomType(@Valid @RequestBody RoomTypeDTO roomTypeDTO) throws URISyntaxException {
        log.debug("REST request to update RoomType: {}", roomTypeDTO);
        if (roomTypeDTO.getId() == null) {
            return createRoomType(roomTypeDTO);
        }
        RoomType result = roomTypeService.save(roomTypeMapper.toEntity(roomTypeDTO));
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, result.getId().toString()))
            .body(roomTypeMapper.toDto(result));
    }

    @GetMapping
    @Timed
    public ResponseEntity<List<RoomTypeDTO>> getAllRoomTypes(Pageable pageable) {
        log.debug("REST request to get a page of RoomTypes");
        Page<RoomTypeDTO> page = roomTypeService.findAll(pageable)
            .map(roomTypeMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/room-types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<RoomTypeDTO> getRoomType(@PathVariable Long id) {
        log.debug("REST request to get RoomType: {}", id);
        Optional<RoomTypeDTO> roomTypeDTO = roomTypeService.findById(id)
            .map(roomTypeMapper::toDto);
        return ResponseUtil.wrapOrNotFound(roomTypeDTO);
    }

    @DeleteMapping("/{id}")
    @Timed
    public ResponseEntity<Void> deleteRoomType(@PathVariable Long id) {
        log.debug("REST request to delete RoomType: {}", id);
        roomTypeService.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
