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
import pl.edu.agh.geotime.domain.Room;
import pl.edu.agh.geotime.service.RoomService;
import pl.edu.agh.geotime.web.rest.dto.RoomDTO;
import pl.edu.agh.geotime.web.rest.mapper.RoomMapper;
import pl.edu.agh.geotime.web.rest.errors.BadRequestAlertException;
import pl.edu.agh.geotime.web.rest.util.HeaderUtil;
import pl.edu.agh.geotime.web.rest.util.PaginationUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rooms")
public class RoomResource {

    private static final String ENTITY_NAME = "room";

    private final Logger log = LoggerFactory.getLogger(RoomResource.class);
    private final RoomService roomService;
    private final RoomMapper roomMapper;

    RoomResource(RoomService roomService, RoomMapper roomMapper) {
        this.roomService = roomService;
        this.roomMapper = roomMapper;
    }

    @PostMapping
    @Timed
    public ResponseEntity<RoomDTO> createRoom(@Valid @RequestBody RoomDTO roomDTO) throws URISyntaxException {
        log.debug("REST request to save Room: {}", roomDTO);
        if (roomDTO.getId() != null) {
            throw new BadRequestAlertException("A new room cannot already have an ID", ENTITY_NAME, "idexists");
        }

        Room result = roomService.save(roomMapper.toEntity(roomDTO));
        return ResponseEntity.created(new URI("/api/rooms/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(roomMapper.toDto(result));
    }

    @PutMapping
    @Timed
    public ResponseEntity<RoomDTO> updateRoom(@Valid @RequestBody RoomDTO roomDTO) throws URISyntaxException {
        log.debug("REST request to update Room: {}", roomDTO);
        if (roomDTO.getId() == null) {
            return createRoom(roomDTO);
        }

        Room result = roomService.save(roomMapper.toEntity(roomDTO));
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, result.getId().toString()))
            .body(roomMapper.toDto(result));
    }

    @GetMapping
    @Timed
    public ResponseEntity<List<RoomDTO>> getAllRooms(Pageable pageable) {
        log.debug("REST request to get a page of Rooms");
        Page<RoomDTO> page = roomService.findAll(pageable)
            .map(roomMapper::toDto);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/rooms");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Timed
    public ResponseEntity<RoomDTO> getRoom(@PathVariable Long id) {
        log.debug("REST request to get Room: {}", id);
        Optional<RoomDTO> roomDTO = roomService.findById(id)
            .map(roomMapper::toDto);
        return ResponseUtil.wrapOrNotFound(roomDTO);
    }

    @DeleteMapping("/{id}")
    @Timed
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        log.debug("REST request to delete Room : {}", id);
        roomService.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
